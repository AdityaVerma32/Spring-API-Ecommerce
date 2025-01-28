package com.project.e_commerce.Service;

import com.project.e_commerce.DTO.PaymentResponse;
import com.project.e_commerce.DTO.PaymentSuccessRequest;
import com.project.e_commerce.Enums.OrderStatus;
import com.project.e_commerce.Enums.PaymentMethod;
import com.project.e_commerce.Model.*;
import com.project.e_commerce.Model.UserPrincipal;
import com.project.e_commerce.Repo.*;
import com.project.e_commerce.Utils.ApiResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Address;
import com.stripe.model.PaymentIntent;
import com.stripe.model.ShippingDetails;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import org.hibernate.query.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Integer.valueOf;


@Service
public class PaymentService {

    @Value("${stripe.secretKey}")
    private String secretKey;

    private OrdersItemsRepo ordersItemsRepo;
    private OrdersRepo ordersRepo;
    private ShippingAddressRepo shippingAddressRepo;
    private UserRepo userRepo;
    private PaymentDetailsRepo paymentDetailsRepo;
    private EmailService emailService;

    public PaymentService(OrdersItemsRepo ordersItemsRepo, OrdersRepo ordersRepo, ShippingAddressRepo shippingAddressRepo, UserRepo userRepo, PaymentDetailsRepo paymentDetailsRepo, EmailService emailService) {
        this.ordersItemsRepo = ordersItemsRepo;
        this.ordersRepo = ordersRepo;
        this.shippingAddressRepo = shippingAddressRepo;
        this.userRepo = userRepo;
        this.paymentDetailsRepo = paymentDetailsRepo;
        this.emailService = emailService;
    }

    private Integer getUserId() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = userPrincipal.getUserId();
        System.out.println("Debug getUserId");
        return userId;
    }

    public PaymentResponse createPaymentLink(List<OrdersItems> ordersItems) throws StripeException {
        Stripe.apiKey = secretKey;

        Integer orderId = ordersItems.get(0).getOrders().getId();

        // Create a list of line items for the session
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        // Iterate over the order items and create line items dynamically
        for (OrdersItems item : ordersItems) {
            // Create line item for each order item
            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(item.getQuantity().longValue())  // Use the quantity from the order item
                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("INR")  // You can also use item.getCurrency() if different per item
                            .setUnitAmount(item.getPrice().multiply(new BigDecimal(100)).longValue())   // Convert price in INR to paise
                            .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName(item.getProduct().getProductName())  // Use the product name from the order item
                                    .build())
                            .build())
                    .build();

            // Add the line item to the list
            lineItems.add(lineItem);
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:5173/payment-Success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:5173/page-not-found")
                .addAllLineItem(lineItems)
                .setShippingAddressCollection(SessionCreateParams.ShippingAddressCollection.builder()
                        .addAllowedCountry(SessionCreateParams.ShippingAddressCollection.AllowedCountry.IN)
                        .addAllowedCountry(SessionCreateParams.ShippingAddressCollection.AllowedCountry.AE)
                        .addAllowedCountry(SessionCreateParams.ShippingAddressCollection.AllowedCountry.CD)
                        .build()
                )
                .putMetadata("OrderId", orderId.toString())
                .build();

        Session session = Session.create(params);
        PaymentResponse res = new PaymentResponse();
        res.setPaymentUrl(session.getUrl());

        return res;
    }

    @Transactional
    public ResponseEntity<?> saveOrderData(PaymentSuccessRequest paymentSuccessRequest) {
        try {
            Stripe.apiKey = secretKey;

            // Fetch session details from the Request
            Session session = Session.retrieve(paymentSuccessRequest.getSessionId());
            // Extract Order ID from the Session ID
            String orderId = session.getMetadata().get("OrderId");
            Optional<Users> user = userRepo.findById(getUserId());
            Optional<OrdersTable> order = ordersRepo.findById(valueOf(orderId));
            // Extraxt Payment Status from the Session ID
            String paymentStatus = session.getPaymentStatus();
            // Extract the Payment Intent id from the Session id which will be used to store the payment Details
            String paymentIntentId = session.getPaymentIntent();
            // Extract Payment Intent
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

            // Extract Shipping Address
            ShippingDetails address = session.getShippingDetails();
            Address stripeAddress = null;
            if (address != null) {
                stripeAddress = address.getAddress();
            }
            // Inserting Shipping Address Details in the DB.
            ShippingAddress shippingAddress = new ShippingAddress();
            shippingAddress.setStreet(stripeAddress.getLine1());
            shippingAddress.setCity(stripeAddress.getCity());
            shippingAddress.setCountry(stripeAddress.getCountry());
            shippingAddress.setState(stripeAddress.getState());
            shippingAddress.setPostalCode(stripeAddress.getPostalCode());
            shippingAddress.setUser(user.get());
            ShippingAddress savedAddress = shippingAddressRepo.save(shippingAddress);

            // Inserting Payment details into the DB.
            PaymentDetails paymentDetails = new PaymentDetails();
            paymentDetails.setPaymentId(paymentIntentId);
            paymentDetails.setPaymentStatus(OrderStatus.PAID);
            paymentDetails.setPaymentMethod(PaymentMethod.STRIPE);
            paymentDetails.setOrder(order.get());
            PaymentDetails savedPaymentDetail = paymentDetailsRepo.save(paymentDetails);

            // Updating the Order details.
            order.get().setShippingAddress(savedAddress);
            order.get().setOrderStatus(OrderStatus.PAID);
            order.get().setPaymentMethod(PaymentMethod.STRIPE);
            OrdersTable updatedOrder = ordersRepo.save(order.get());

            emailService.sendOrderSuccessFullEmail(user.get().getEmail(), user.get().getFirstName(), updatedOrder);
            List<OrdersItems> order_items = ordersItemsRepo.findByOrders(updatedOrder);

            // Return session details as a response
            return new ResponseEntity<>(new ApiResponse<>(true, "Order Placed Successfully.", order_items), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching session details: " + e.getMessage());
        }
    }

}
