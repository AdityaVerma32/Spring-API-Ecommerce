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
import com.stripe.model.ShippingDetails;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
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

    private final OrdersItemsRepo ordersItemsRepo;
    private final OrdersRepo ordersRepo;
    private final ShippingAddressRepo shippingAddressRepo;
    private final UserRepo userRepo;
    private final PaymentDetailsRepo paymentDetailsRepo;
    private final EmailService emailService;
    private final ProductRepo productRepo;
    private final CartRepo cartRepo;
    private final CartItemsRepo cartItemsRepo;
    private final ExpiredSessionIdRepo expiredSessionIdRepo;

    public PaymentService(OrdersItemsRepo ordersItemsRepo, OrdersRepo ordersRepo, ShippingAddressRepo shippingAddressRepo, UserRepo userRepo, PaymentDetailsRepo paymentDetailsRepo, EmailService emailService, ProductRepo productRepo, CartRepo cartRepo, CartItemsRepo cartItemsRepo, ExpiredSessionIdRepo expiredSessionIdRepo) {
        this.ordersItemsRepo = ordersItemsRepo;
        this.ordersRepo = ordersRepo;
        this.shippingAddressRepo = shippingAddressRepo;
        this.userRepo = userRepo;
        this.paymentDetailsRepo = paymentDetailsRepo;
        this.emailService = emailService;
        this.productRepo = productRepo;
        this.cartRepo = cartRepo;
        this.cartItemsRepo = cartItemsRepo;
        this.expiredSessionIdRepo = expiredSessionIdRepo;
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
    public ResponseEntity<?> saveOrderData(PaymentSuccessRequest paymentSuccessRequest) throws StripeException, MessagingException {

        Stripe.apiKey = secretKey;
        String requestSessionId = paymentSuccessRequest.getSessionId();
        ExpiredSessionId expiredSessionId = expiredSessionIdRepo.findBySessionId(requestSessionId);

        // Fetch session details from the Request
        Session session = Session.retrieve(requestSessionId);
        // Extract Order ID from the Session ID
        String orderId = session.getMetadata().get("OrderId");
        Optional<Users> user = userRepo.findById(getUserId());
        Optional<OrdersTable> order = ordersRepo.findById(valueOf(orderId));
        List<OrdersItems> ordersItems = ordersItemsRepo.findByOrders(order.get());

        if (expiredSessionId == null) {

            // Extract the Payment Intent id from the Session id which will be used to store the payment Details
            String paymentIntentId = session.getPaymentIntent();

            // Extract Shipping Address
            ShippingDetails address = session.getShippingDetails();
            Address stripeAddress = address.getAddress();
            String PhoneNo = address.getPhone();

            // Inserting Shipping Address Details in the DB.
            ShippingAddress shippingAddress = new ShippingAddress();
            shippingAddress.setStreet(stripeAddress.getLine1());
            shippingAddress.setCity(stripeAddress.getCity());
            shippingAddress.setCountry(stripeAddress.getCountry());
            shippingAddress.setState(stripeAddress.getState());
            shippingAddress.setPostalCode(stripeAddress.getPostalCode());
            shippingAddress.setUser(user.get());
            if (PhoneNo != null) {
                shippingAddress.setPhoneNo(PhoneNo);
            }
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

            // Now, we will change the Reserved Quantity
            for (OrdersItems item : ordersItems) {
                Product product = item.getProduct();
                product.setReserved_quantity(product.getReserved_quantity() - item.getQuantity());
                productRepo.save(product);
            }

            // Delete the Items from the Cart and Cart Items.
            Cart cart = cartRepo.findByOrderId(updatedOrder.getId());
            List<CartItems> cartItems = cartItemsRepo.findByCart(cart);
            for (CartItems items : cartItems) {
                cartItemsRepo.delete(items);
            }
            cartRepo.delete(cart);

            emailService.sendOrderSuccessFullEmail(user.get().getEmail(), user.get().getFirstName(), updatedOrder);
            List<OrdersItems> order_items = ordersItemsRepo.findByOrders(updatedOrder);

            ExpiredSessionId savedExpiredSessionId = new ExpiredSessionId(requestSessionId);
            expiredSessionIdRepo.save(savedExpiredSessionId);

            // Return session details as a response
            return new ResponseEntity<>(new ApiResponse<>(true, "Order Placed Successfully.", order_items), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse<>(true, "Order Placed Successfully.", ordersItems), HttpStatus.OK);
        }


    }

}
