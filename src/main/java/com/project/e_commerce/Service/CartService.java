package com.project.e_commerce.Service;

import com.project.e_commerce.Model.*;
import com.project.e_commerce.Repo.*;
import com.project.e_commerce.Utils.ApiResponse;
import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CartService {

    private final UserRepo userRepo;
    private final CartRepo cartRepo;
    private final ProductRepo productRepo;
    private final CartItemsRepo cartItemsRepo;
    private final OrdersRepo ordersRepo;
    private final OrdersItemsRepo ordersItemsRepo;


    public CartService(UserRepo userRepo, CartRepo cartRepo, ProductRepo productRepo, CartItemsRepo cartItemsRepo, OrdersItemsRepo ordersItemsRepo, OrdersRepo ordersRepo) {
        this.userRepo = userRepo;
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
        this.cartItemsRepo = cartItemsRepo;
        this.ordersRepo = ordersRepo;
        this.ordersItemsRepo = ordersItemsRepo;
    }

    // Method to add a product to the cart
    public ResponseEntity<ApiResponse<Object>> addToCart(Integer userId, Integer prodId) {
        try {
            // Check if the user exists
            Users user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Check if the product exists
            Product product = productRepo.findById(prodId).orElseThrow(() -> new IllegalArgumentException("Product not found"));

            // Check if the product is in stock
            if (product.getAvailable_quantity() <= 0) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Product is out of stock", null), HttpStatus.BAD_REQUEST);
            }

            // Fetch or create the user's cart
            Cart cart = cartRepo.findByUser(user);
            if (cart == null) {
                cart = createNewCart(user, product);
                return new ResponseEntity<>(new ApiResponse<>(true, "Product added to cart.", null), HttpStatus.OK);
            }

            // Handle case where cart is mapped to an order
            if (cart.getMapToOrder() == 1) {
                handleCartWithMappedOrder(cart, product);
            } else {
                handleCartWithoutMappedOrder(cart, product);
            }

            return new ResponseEntity<>(new ApiResponse<>(true, "Cart updated successfully.", null), HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            // Handles invalid user or product scenarios
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Generic error handling for unexpected issues
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper method to create a new cart
    private Cart createNewCart(Users user, Product product) {
        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setTotalPrice(product.getPrice());
        newCart.setMapToOrder(0); // Initially, the cart is not linked to an order
        newCart.setOrderId(null);
        Cart savedCart = cartRepo.save(newCart);

        addCartItem(savedCart, product, 1);
        return savedCart;
    }

    // Handle cart updates when the cart is linked to an order
    private void handleCartWithMappedOrder(Cart cart, Product product) {
        Integer orderId = cart.getOrderId();
        OrdersTable existingOrder = ordersRepo.findById(orderId).orElseThrow(() -> new IllegalStateException("Mapped order not found"));

        CartItems cartItem = cartItemsRepo.findByCartAndProduct(cart, product);
        OrdersItems orderItem = ordersItemsRepo.findByOrdersAndProduct(existingOrder, product);

        if (cartItem == null && orderItem == null) {
            // Add new item to both cart and order
            addCartItem(cart, product, 1);
            addOrderItem(existingOrder, product, 1);
        } else {
            // Update quantity in both cart and order
            updateCartItem(cartItem, 1);
            updateOrderItem(orderItem, 1);
        }

        // Update inventory and totals
        updateInventory(product, 1);
        updateCartTotal(cart, product.getPrice());
        updateOrderTotal(existingOrder, product.getPrice());
    }

    // Handle cart updates when the cart is not linked to an order
    private void handleCartWithoutMappedOrder(Cart cart, Product product) {
        CartItems cartItem = cartItemsRepo.findByCartAndProduct(cart, product);

        if (cartItem == null) {
            addCartItem(cart, product, 1);
        } else {
            updateCartItem(cartItem, 1);
        }

        updateCartTotal(cart, product.getPrice());
    }

    // Add a new item to the cart
    private void addCartItem(Cart cart, Product product, int quantity) {
        CartItems newCartItem = new CartItems();
        newCartItem.setCart(cart);
        newCartItem.setProduct(product);
        newCartItem.setPrice(product.getPrice());
        newCartItem.setQuantity(quantity);
        cartItemsRepo.save(newCartItem);
    }

    // Add a new item to the order
    private void addOrderItem(OrdersTable order, Product product, int quantity) {
        OrdersItems newOrderItem = new OrdersItems();
        newOrderItem.setOrders(order);
        newOrderItem.setProduct(product);
        newOrderItem.setPrice(product.getPrice());
        newOrderItem.setQuantity(quantity);
        ordersItemsRepo.save(newOrderItem);
    }

    // Update an existing cart item
    private void updateCartItem(CartItems cartItem, int quantity) {
        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItemsRepo.save(cartItem);
    }

    // Update an existing order item
    private void updateOrderItem(OrdersItems orderItem, int quantity) {
        orderItem.setQuantity(orderItem.getQuantity() + quantity);
        ordersItemsRepo.save(orderItem);
    }

    // Update inventory (deduct available and add to reserved)
    private void updateInventory(Product product, int quantity) {
        product.setAvailable_quantity(product.getAvailable_quantity() - quantity);
        product.setReserved_quantity(product.getReserved_quantity() + quantity);
        productRepo.save(product);
    }

    // Update cart total price
    private void updateCartTotal(Cart cart, BigDecimal price) {
        cart.setTotalPrice(cart.getTotalPrice().add(price));
        cartRepo.save(cart);
    }

    // Update order total amount
    private void updateOrderTotal(OrdersTable order, BigDecimal price) {
        order.setTotalAmount(order.getTotalAmount().add(price));
        ordersRepo.save(order);
    }


    // Method to get all products in the cart for a user
    public ResponseEntity<ApiResponse<Object>> getCartProducts(Integer userId) {
        try {
            // Check if the user exists in the database
            Optional<Users> user = userRepo.findById(userId);
            if (user.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "User not found", null), HttpStatus.BAD_REQUEST);  // User not found
            }
            // Fetch the cart for the user
            Cart cart = cartRepo.findByUser(user.get());

            if (cart == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Cart not found for the user", null), HttpStatus.NOT_FOUND);  // Cart not found for the user
            }

            // Fetch all cart items for the cart
            List<CartItems> cartItems = cartItemsRepo.findByCart(cart);
            return new ResponseEntity<>(new ApiResponse<>(true, "Cart not found for the user", cartItems), HttpStatus.OK);  // Return list of cart items

        } catch (Exception e) {
            // Exception handling for any unexpected errors
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);  // Internal server error
        }
    }

    public ResponseEntity<ApiResponse<Object>> updateProductQuantity(Integer userId, Integer prodId, Map<String, Integer> request) {
        Integer quantity = request.get("quantity");

        // Fetch the user
        Optional<Users> user = userRepo.findById(userId);
        if (user.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(false, "User not found", null), HttpStatus.NOT_FOUND);
        }

        // Fetch the cart
        Cart cart = cartRepo.findByUser(user.get());
        if (cart == null) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Cart not found for the user", null), HttpStatus.NOT_FOUND);
        }

        // Fetch the product
        Optional<Product> product = productRepo.findById(prodId);
        if (product.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Product not found", null), HttpStatus.NOT_FOUND);
        }

        // Fetch the cart item
        CartItems cartItems = cartItemsRepo.findByCartAndProduct(cart, product.get());
        if (cartItems == null) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Product not found in the cart", null), HttpStatus.NOT_FOUND);
        }

        int current_cart_stock = cartItems.getQuantity(); // Current quantity in cart
        int current_stock = product.get().getAvailable_quantity(); // Current stock in inventory
        int change_in_quantity = quantity - current_cart_stock; // 1 or -1

        if (quantity < 0) {
            // Invalid request: quantity cannot be negative
            return new ResponseEntity<>(new ApiResponse<>(false, "Invalid quantity: cannot be less than zero", null), HttpStatus.BAD_REQUEST);
        }

        // Now we need to check if the Cart is Mapped to Orders Table
        if (cart.getMapToOrder() == 1) { // If the Cart is mapped to Order then update both cart table and Orders table

            Integer orderId = cart.getOrderId();
            Optional<OrdersTable> existingOrder = ordersRepo.findById(orderId);
            OrdersItems ordersItems = ordersItemsRepo.findByOrdersAndProduct(existingOrder.get(), product.get());

            if (quantity == 0) { // If the new Quantity needs to be 0 then delete this Item from the cart table and Orders table

                // Delete product from cart and Orders table
                deleteProductFromCart(cart, cartItems, product.get());
                deleteProductFromOrder(existingOrder.get(), ordersItems, product.get());

                // changes the available quantity and reserved quantity
                updateInventory(product.get(), change_in_quantity);
                return new ResponseEntity<>(new ApiResponse<>(true, "Product removed from cart", null), HttpStatus.OK);
            }

            if (current_stock < quantity && change_in_quantity > 0) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Insufficient stock.", null), HttpStatus.BAD_REQUEST);
            } else {
                // Update the Cart Items table and Order Items table
                updateCartItem(cartItems, change_in_quantity);
                updateOrderItem(ordersItems, change_in_quantity);

                // Update Cart Table and Orders table
                updateCartTotal(cart, product.get().getPrice().multiply(new BigDecimal(change_in_quantity)));
                updateOrderTotal(existingOrder.get(), product.get().getPrice().multiply(new BigDecimal(change_in_quantity)));

                // update Inventory
                updateInventory(product.get(), change_in_quantity);

                return new ResponseEntity<>(new ApiResponse<>(true, "Product quantity updated successfully", null), HttpStatus.OK);
            }
        } else {
            if (quantity == 0) {
                // Remove product from cart
                BigDecimal price = cartItems.getPrice().multiply(new BigDecimal(cartItems.getQuantity()));

                // Decrease the Total price from Cart table
                cart.setTotalPrice(cart.getTotalPrice().subtract(price));
                cartRepo.save(cart);
                // Delete the Product entry from cart Items table
                cartItemsRepo.delete(cartItems);
                return new ResponseEntity<>(new ApiResponse<>(true, "Product removed from cart", null), HttpStatus.OK);
            }

            if (change_in_quantity > 0) {
                // Increasing the quantity
                if (current_stock >= quantity) {
                    // Update the quantity in the cart
                    BigDecimal change_price = product.get().getPrice().multiply(new BigDecimal(change_in_quantity));
                    updateCartTotal(cart, change_price);
                    // Update the Quantity in the Cart Items Table
                    updateCartItem(cartItems, change_in_quantity);

                    return new ResponseEntity<>(new ApiResponse<>(true, "Product quantity updated successfully", null), HttpStatus.OK);
                } else {
                    // Current stock is insufficient for the requested quantity
                    return new ResponseEntity<>(new ApiResponse<>(false, "Insufficient stock. Current stock is already less than the quantity in your cart.", null), HttpStatus.BAD_REQUEST);
                }
            } else {
                // Decreasing the quantity
                if (current_stock >= quantity) {
                    // Update the quantity in the cart
                    BigDecimal change_price = product.get().getPrice().multiply(new BigDecimal(change_in_quantity));
                    updateCartTotal(cart, change_price);
                    updateCartItem(cartItems, change_in_quantity);

                    return new ResponseEntity<>(new ApiResponse<>(true, "Product quantity updated successfully", null), HttpStatus.OK);
                } else {
                    // Handle cases where stock is less than current cart stock
                    if (current_stock < current_cart_stock) {
                        if (current_stock == 0) {
                            // Remove product from cart
                            BigDecimal change_price = cartItems.getPrice().multiply(new BigDecimal(cartItems.getQuantity())).abs();
                            updateCartTotal(cart, change_price);
                            cartItemsRepo.delete(cartItems);
                            return new ResponseEntity<>(new ApiResponse<>(true, "Product is Out of Stock", null), HttpStatus.OK);
                        } else {
                            BigDecimal change_price = cartItems.getPrice().multiply(new BigDecimal(current_stock - current_cart_stock));
                            updateCartTotal(cart, change_price);
                            cartItems.setQuantity(current_stock);
                            cartItemsRepo.save(cartItems);
                            return new ResponseEntity<>(new ApiResponse<>(false, "Stock is less than the quantity in your cart.Adjusted the quantity.", null), HttpStatus.BAD_REQUEST);
                        }
                    }
                }
            }
        }

        return new ResponseEntity<>(new ApiResponse<>(false, "An unexpected error occurred.", null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Delete product from cart and Orders table
//    deleteProductFromCart(cart, cartItems, product);
//    deleteProductFromOrder(existingOrder, ordersItems, product);

    private void deleteProductFromCart(Cart cart, CartItems cartItems, Product product){
        BigDecimal change_price = cartItems.getPrice().multiply(new BigDecimal(cartItems.getQuantity()));
        cart.setTotalPrice(cart.getTotalPrice().subtract(change_price));
        cartItemsRepo.delete(cartItems);
    }

    private void deleteProductFromOrder(OrdersTable existingOrder,OrdersItems ordersItems,Product product){
        BigDecimal change_price = ordersItems.getPrice().multiply(new BigDecimal(ordersItems.getQuantity()));
        existingOrder.setTotalAmount(existingOrder.getTotalAmount().subtract(change_price));
        ordersItemsRepo.delete(ordersItems);
    }

    public ResponseEntity<ApiResponse<Object>> deleteCartProduct(Integer prodId, Integer userId) {
        try {
            // Fetch the user by userId
            Optional<Users> user = userRepo.findById(userId);
            if (user.isEmpty()) {
                // Return error if user is not found
                return new ResponseEntity<>(new ApiResponse<>(false, "User not found", null), HttpStatus.BAD_REQUEST);
            }

            // Fetch the cart associated with the user
            Cart cart = cartRepo.findByUser(user.get());
            if (cart == null) {
                // Return error if the cart does not exist
                return new ResponseEntity<>(new ApiResponse<>(false, "Cart not found for the user", null), HttpStatus.BAD_REQUEST);
            }

            // Fetch the product by prodId
            Optional<Product> product = productRepo.findById(prodId);
            if (product.isEmpty()) {
                // Return error if product is not found
                return new ResponseEntity<>(new ApiResponse<>(false, "Product not found", null), HttpStatus.BAD_REQUEST);
            }

            // Fetch the cart item for the given cart and product
            CartItems cartItems = cartItemsRepo.findByCartAndProduct(cart, product.get());
            if (cartItems == null) {
                // Return error if the cart item does not exist
                return new ResponseEntity<>(new ApiResponse<>(false, "Product not found in the cart", null), HttpStatus.BAD_REQUEST);
            }

            BigDecimal price = cartItems.getPrice().multiply(new BigDecimal(cartItems.getQuantity()));
            cart.setTotalPrice(cart.getTotalPrice().subtract(price));
            // Remove the cart item
            cartItemsRepo.delete(cartItems);


            // Return success response
            return new ResponseEntity<>(new ApiResponse<>(true, "Product removed from cart", null), HttpStatus.OK);

        } catch (Exception e) {
            // Handle any unexpected exceptions
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
