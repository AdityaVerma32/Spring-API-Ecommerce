package com.project.e_commerce.Service;

import com.project.e_commerce.Enums.OrderStatus;
import com.project.e_commerce.Enums.PaymentMethod;
import com.project.e_commerce.Model.*;
import com.project.e_commerce.Repo.*;
import com.project.e_commerce.Utils.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrdersService {

    private final CartRepo cartRepo;
    private final CartItemsRepo cartItemsRepo;
    private final OrdersRepo ordersRepo;
    private final OrdersItemsRepo ordersItemsRepo;
    private final ProductRepo productRepo;
    private final UserRepo userRepo;

    private Integer getUserId() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = userPrincipal.getUserId();
        return userId;
    }

    // Constructor to initialize the repositories
    public OrdersService(OrdersItemsRepo ordersItemsRepo, CartRepo cartRepo, CartItemsRepo cartItemsRepo, OrdersRepo ordersRepo, ProductRepo productRepo, UserRepo userRepo) {
        this.ordersItemsRepo = ordersItemsRepo;
        this.cartRepo = cartRepo;
        this.cartItemsRepo = cartItemsRepo;
        this.ordersRepo = ordersRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
    }

    // Method to create an order with transactional support to ensure atomicity
    @Transactional
    public ResponseEntity<ApiResponse<Object>> createOrder(Integer cartId) {

        // 1. Retrieve the cart
        Optional<Cart> optionalCart = cartRepo.findById(cartId);
        if (optionalCart.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Cart not Found", null), HttpStatus.BAD_REQUEST);
        }
        Cart cart = optionalCart.get();

        if (cart.getMapToOrder() == 1) { // Order is already created
            Integer orderId = cart.getOrderId();
            Optional<OrdersTable> existingOrder = ordersRepo.findById(orderId);
            List<OrdersItems> ordersItems = ordersItemsRepo.findByOrders(existingOrder.get());

            return new ResponseEntity<>(new ApiResponse<>(true, "Order Created Successfully", ordersItems), HttpStatus.OK);
        } else {  // we need to create order for this and then mark mapToOrderbit as 1 and set the order id into cart.
            try {
                // 2. Retrieve cart items
                List<CartItems> cartItems = cartItemsRepo.findByCart(cart);
                if (cartItems.isEmpty()) {
                    return new ResponseEntity<>(new ApiResponse<>(false, "Cart is Empty", null), HttpStatus.BAD_REQUEST);
                }

                // 3. Validate inventory (Check if quantity in cart does not exceed available stock)
                for (CartItems item : cartItems) {
                    Product product = item.getProduct(); // Assuming CartItems has a Product relationship
                    if (item.getQuantity() > product.getAvailable_quantity()) {
                        return new ResponseEntity<>(new ApiResponse<>(false, "Quantity Mismatch between Inventory and Order Product Quantity", null), HttpStatus.BAD_REQUEST);
                    }
                }

                // 4. Calculate total amount from cart
                BigDecimal totalAmount = cart.getTotalPrice();

                // 6. Create the order object
                OrdersTable order_table = new OrdersTable();
                order_table.setUser(cart.getUser());
                order_table.setTotalAmount(totalAmount);
                order_table.setOrderStatus(OrderStatus.PENDING);
                order_table.setPaymentMethod(PaymentMethod.NONE); // Example payment method (adjust as needed)
                order_table.setShippingAddress(null);
                // Save the order to the database
                OrdersTable savedOrder = ordersRepo.save(order_table);

                // 7. Store the Order Items related to the order
                for (CartItems item : cartItems) {
                    Product product = item.getProduct(); // Assuming CartItems has a Product relationship
                    OrdersItems ordersItems = new OrdersItems();
                    ordersItems.setProduct(product);
                    ordersItems.setOrders(savedOrder);
                    ordersItems.setPrice(product.getPrice());
                    ordersItems.setQuantity(item.getQuantity());
                    ordersItemsRepo.save(ordersItems);

                    // After adding the item in the Order Items table, changing the available and Reserved Quantity
                    product.setReserved_quantity(item.getQuantity());
                    product.setAvailable_quantity(product.getAvailable_quantity() - item.getQuantity());
                    productRepo.save(product); // Save updated product inventory
                }

                cart.setMapToOrder(1);
                cart.setOrderId(savedOrder.getId());

                // 9. Return success response
                return new ResponseEntity<>(new ApiResponse<>(true, "Order Created Successfully", ordersItemsRepo.findByOrders(savedOrder)), HttpStatus.OK);
            } catch (Exception e) {
                // Catch and handle any unexpected errors
                return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while creating the order: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    /**
     * Deletes an order along with its associated order items if they exist.
     * The operation is atomic, meaning if any part fails, no changes are made to the database.
     *
     * @param orderId The ID of the order to delete
     * @return ResponseEntity indicating success or failure
     */
    @Transactional // Ensures atomicity; if any step fails, all changes will be rolled back
    public ResponseEntity<ApiResponse<Object>> deleteOrder(Integer orderId) {
        try {
            // 1. Retrieve the order by its ID
            Optional<OrdersTable> optionalOrder = ordersRepo.findById(orderId);
            if (optionalOrder.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Order not found", null), HttpStatus.NOT_FOUND); // Return if order does not exist
            }

            OrdersTable order = optionalOrder.get();

            // 2. Retrieve the associated order items
            List<OrdersItems> ordersItems = ordersItemsRepo.findByOrders(order);
            if (ordersItems.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "No items found for this order", null), HttpStatus.BAD_REQUEST); // Return if no items found
            }

            // 3. Delete associated order items
            for (OrdersItems item : ordersItems) {

                // Updating the Quantity of the Product
                Product product = item.getProduct();
                product.setAvailable_quantity(product.getAvailable_quantity() + item.getQuantity());
                product.setReserved_quantity(product.getReserved_quantity() - item.getQuantity());
                productRepo.save(product);
                // Then Deleting the Product from the OrderItemsTable
                ordersItemsRepo.delete(item); // Delete each order item
            }

            // 4. Delete the order itself
            ordersRepo.delete(order); // Delete the order

            // Now delete the mapping of this Order from the Cart.
            Cart cart = cartRepo.findByOrderId(orderId);

            cart.setOrderId(null);
            cart.setMapToOrder(0);
            cartRepo.save(cart);
            // 5. Return a success response
            return new ResponseEntity<>(new ApiResponse<>(true, "Order Deleted Successfully", null), HttpStatus.OK);

        } catch (Exception e) {
            // 6. Handle any exceptions that occur during the delete operation
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while deleting the order: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ApiResponse<Object>> fetchOrders() {
        Optional<Users> user = userRepo.findById(getUserId());
        return new ResponseEntity<>(new ApiResponse<>(true, "Orders Details", ordersRepo.findByUser(user.get())), HttpStatus.OK);
    }
}
