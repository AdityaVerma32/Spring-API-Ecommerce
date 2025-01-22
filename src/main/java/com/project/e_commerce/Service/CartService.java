package com.project.e_commerce.Service;

import com.project.e_commerce.Model.Cart;
import com.project.e_commerce.Model.CartItems;
import com.project.e_commerce.Model.Product;
import com.project.e_commerce.Model.Users;
import com.project.e_commerce.Repo.CartItemsRepo;
import com.project.e_commerce.Repo.CartRepo;
import com.project.e_commerce.Repo.ProductRepo;
import com.project.e_commerce.Repo.UserRepo;
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

    public CartService(UserRepo userRepo, CartRepo cartRepo, ProductRepo productRepo, CartItemsRepo cartItemsRepo) {
        this.userRepo = userRepo;
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
        this.cartItemsRepo = cartItemsRepo;
    }

    // Method to add a product to the cart
    public ResponseEntity<ApiResponse<Object>> addToCart(Integer userId, Integer prodId) {
        try {

            // Check if the user exists in the database
            Optional<Users> user = userRepo.findById(userId);
            if (user.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false,"User not found",null), HttpStatus.BAD_REQUEST);  // User not found
            }

            // Check if the product exists in the database
            Optional<Product> product = productRepo.findById(prodId);
            if (product.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false,"Product not found",null), HttpStatus.BAD_REQUEST);  // Product not found
            }

            // Check if the Product quantity is Greater than 0
            if (product.get().getStock() <= 0) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Product is out of Stock",null), HttpStatus.BAD_REQUEST);  // Product not found
            }

            // Check if the cart exists for the current user
            Cart cart = cartRepo.findByUser(user.get());

            if (cart == null) {
                // Create a new cart for the user if no cart exists
                Cart newCart = new Cart();
                newCart.setUser(user.get());
                newCart.setTotalPrice(product.get().getPrice()); // Set total price to the price of the first product
                Cart savedCart = cartRepo.save(newCart);

                // Create cart item for the product
                CartItems cartItem = new CartItems();
                cartItem.setCart(savedCart);
                cartItem.setProduct(product.get());
                cartItem.setPrice(product.get().getPrice());
                cartItem.setQuantity(1); // Default quantity for new product
                CartItems savedCartItem = cartItemsRepo.save(cartItem);

                // Check if both cart and cart item were saved successfully
                if (savedCart.getId() != null && savedCartItem.getId() != null) {
                    return new ResponseEntity<>(new ApiResponse<>(true,"Product added to cart.",null), HttpStatus.OK);  // Successfully added product
                } else {
                    return new ResponseEntity<>(new ApiResponse<>(false, "Error occurred while adding product to cart.", null), HttpStatus.BAD_REQUEST);  // Error adding product
                }
            } else {
                // Cart exists for the user, check if the product is already in the cart
                CartItems existingCartItem = cartItemsRepo.findByCartAndProduct(cart, product.get());

                if (existingCartItem == null) {
                    // Product not in the cart, add it

                    CartItems newCartItem = new CartItems();
                    newCartItem.setCart(cart);
                    newCartItem.setProduct(product.get());
                    newCartItem.setPrice(product.get().getPrice());
                    newCartItem.setQuantity(1); // Default quantity for new product
                    cartItemsRepo.save(newCartItem);

                    // Update total price of the cart
                    cart.setTotalPrice(cart.getTotalPrice().add(product.get().getPrice()));
                    cartRepo.save(cart); // Save updated cart
                    return new ResponseEntity<>(new ApiResponse<>(true,"Product added to cart.",null), HttpStatus.OK);  // Successfully added product
                } else {
                    // Product already in the cart, update quantity
                    existingCartItem.setQuantity(existingCartItem.getQuantity() + 1);
                    cartItemsRepo.save(existingCartItem);

                    // Update total price of the cart
                    cart.setTotalPrice(cart.getTotalPrice().add(product.get().getPrice()));
                    cartRepo.save(cart); // Save updated cart

                    return new ResponseEntity<>(new ApiResponse<>(true,"Product quantity updated in cart.",null), HttpStatus.OK);  // Successfully updated quantity
                }
            }
        } catch (Exception e) {
            // Exception handling for any unexpected errors
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred: " + e.getMessage(),null), HttpStatus.INTERNAL_SERVER_ERROR);  // Internal server error
        }
    }

    // Method to get all products in the cart for a user
    public ResponseEntity<ApiResponse<Object>> getCartProducts(Integer userId) {
        try {
            // Check if the user exists in the database
            Optional<Users> user = userRepo.findById(userId);
            if (user.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "User not found",null), HttpStatus.BAD_REQUEST);  // User not found
            }
            // Fetch the cart for the user
            Cart cart = cartRepo.findByUser(user.get());

            if (cart == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Cart not found for the user", null), HttpStatus.NOT_FOUND);  // Cart not found for the user
            }

            // Fetch all cart items for the cart
            List<CartItems> cartItems = cartItemsRepo.findByCart(cart);
            return new ResponseEntity<>(new ApiResponse<>(true,"Cart not found for the user",cartItems), HttpStatus.OK);  // Return list of cart items

        } catch (Exception e) {
            // Exception handling for any unexpected errors
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred: " + e.getMessage(),null), HttpStatus.INTERNAL_SERVER_ERROR);  // Internal server error
        }
    }

    public ResponseEntity<ApiResponse<Object>> updateProductQuantity(Integer userId, Integer prodId, Map<String, Integer> request) {
        Integer quantity = request.get("quantity");

        // Fetch the user
        Optional<Users> user = userRepo.findById(userId);
        if (user.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(false, "User not found",null), HttpStatus.NOT_FOUND);
        }

        // Fetch the cart
        Cart cart = cartRepo.findByUser(user.get());
        if (cart == null) {
            return new ResponseEntity<>(new ApiResponse<>(false,"Cart not found for the user",null), HttpStatus.NOT_FOUND);
        }

        // Fetch the product
        Optional<Product> product = productRepo.findById(prodId);
        if (product.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(false,"Product not found",null), HttpStatus.NOT_FOUND);
        }

        // Fetch the cart item
        CartItems cartItems = cartItemsRepo.findByCartAndProduct(cart, product.get());
        if (cartItems == null) {
            return new ResponseEntity<>(new ApiResponse<>(false,"Product not found in the cart",null), HttpStatus.NOT_FOUND);
        }

        int current_cart_stock = cartItems.getQuantity(); // Current quantity in cart
        int current_stock = product.get().getStock(); // Current stock in inventory
        int change_in_quantity = quantity - current_cart_stock;

        if (quantity < 0) {
            // Invalid request: quantity cannot be negative
            return new ResponseEntity<>(new ApiResponse<>(false,"Invalid quantity: cannot be less than zero",null), HttpStatus.BAD_REQUEST);
        }

        if (quantity == 0) {
            // Remove product from cart
            BigDecimal price = cartItems.getPrice().multiply(new BigDecimal(cartItems.getQuantity()));

            // Decrease the Total price from Cart table
            cart.setTotalPrice(cart.getTotalPrice().subtract(price));
            cartRepo.save(cart);
            // Delete the Product entry from cart Items table
            cartItemsRepo.delete(cartItems);
            return new ResponseEntity<>(new ApiResponse<>(true,"Product removed from cart",null), HttpStatus.OK);
        }

        if (change_in_quantity > 0) {
            // Increasing the quantity
            if (current_stock >= quantity) {
                // Update the quantity in the cart
                BigDecimal change_price = cartItems.getPrice().multiply(new BigDecimal(change_in_quantity));
                cart.setTotalPrice(cart.getTotalPrice().add(change_price));
                cartRepo.save(cart);
                // Update the Quantity in the Cart Items Table
                cartItems.setQuantity(quantity);
                cartItemsRepo.save(cartItems);

                return new ResponseEntity<>(new ApiResponse<>(true,"Product quantity updated successfully",null), HttpStatus.OK);
            } else {
                // Current stock is insufficient for the requested quantity
                return new ResponseEntity<>(new ApiResponse<>(false,"Insufficient stock. Current stock is already less than the quantity in your cart.",null), HttpStatus.BAD_REQUEST);
            }
        } else {
            // Decreasing the quantity
            if (current_stock >= quantity) {
                // Update the quantity in the cart
                BigDecimal change_price = cartItems.getPrice().multiply(new BigDecimal(change_in_quantity)).abs();
                cart.setTotalPrice(cart.getTotalPrice().subtract(change_price));
                cartRepo.save(cart);
                cartItems.setQuantity(quantity);
                cartItemsRepo.save(cartItems);

                return new ResponseEntity<>(new ApiResponse<>(true,"Product quantity updated successfully",null), HttpStatus.OK);
            } else {
                // Handle cases where stock is less than current cart stock
                if (current_stock < current_cart_stock) {
                    if (current_stock == 0) {
                        // Remove product from cart
                        BigDecimal change_price = cartItems.getPrice().multiply(new BigDecimal(cartItems.getQuantity())).abs();
                        cart.setTotalPrice(cart.getTotalPrice().subtract(change_price));
                        cartRepo.save(cart);
                        cartItems.setQuantity(current_stock);
                        cartItemsRepo.save(cartItems);
                        return new ResponseEntity<>(new ApiResponse<>(true,"Product is Out of Stock",null), HttpStatus.OK);
                    } else {

                        BigDecimal change_price = cartItems.getPrice().multiply(new BigDecimal(cartItems.getQuantity()-current_stock)).abs();
                        cart.setTotalPrice(cart.getTotalPrice().subtract(change_price));
                        cartRepo.save(cart);

                        cartItems.setQuantity(current_stock);
                        cartItemsRepo.save(cartItems);
                        return new ResponseEntity<>(new ApiResponse<>(false,"Stock is less than the quantity in your cart.Adjusted the quantity.",null), HttpStatus.BAD_REQUEST);
                    }
                }
            }
        }

        return new ResponseEntity<>(new ApiResponse<>(false,"An unexpected error occurred.",null), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    public ResponseEntity<ApiResponse<Object>> deleteCartProduct(Integer prodId, Integer userId) {
        try {
            // Fetch the user by userId
            Optional<Users> user = userRepo.findById(userId);
            if (user.isEmpty()) {
                // Return error if user is not found
                return new ResponseEntity<>(new ApiResponse<>(false,"User not found",null), HttpStatus.BAD_REQUEST);
            }

            // Fetch the cart associated with the user
            Cart cart = cartRepo.findByUser(user.get());
            if (cart == null) {
                // Return error if the cart does not exist
                return new ResponseEntity<>(new ApiResponse<>(false,"Cart not found for the user",null), HttpStatus.BAD_REQUEST);
            }

            // Fetch the product by prodId
            Optional<Product> product = productRepo.findById(prodId);
            if (product.isEmpty()) {
                // Return error if product is not found
                return new ResponseEntity<>(new ApiResponse<>(false,"Product not found",null), HttpStatus.BAD_REQUEST);
            }

            // Fetch the cart item for the given cart and product
            CartItems cartItems = cartItemsRepo.findByCartAndProduct(cart, product.get());
            if (cartItems == null) {
                // Return error if the cart item does not exist
                return new ResponseEntity<>(new ApiResponse<>(false,"Product not found in the cart",null), HttpStatus.BAD_REQUEST);
            }

            BigDecimal price = cartItems.getPrice().multiply(new BigDecimal(cartItems.getQuantity()));
            cart.setTotalPrice(cart.getTotalPrice().subtract(price));
            // Remove the cart item
            cartItemsRepo.delete(cartItems);


            // Return success response
            return new ResponseEntity<>(new ApiResponse<>(true,"Product removed from cart",null), HttpStatus.OK);

        } catch (Exception e) {
            // Handle any unexpected exceptions
            return new ResponseEntity<>(new ApiResponse<>(false,"An error occurred: " + e.getMessage(),null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
