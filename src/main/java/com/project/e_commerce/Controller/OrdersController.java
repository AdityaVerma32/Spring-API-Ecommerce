package com.project.e_commerce.Controller;


import com.project.e_commerce.DTO.CreateOrder;
import com.project.e_commerce.DTO.PaymentResponse;
import com.project.e_commerce.Model.OrdersItems;
import com.project.e_commerce.Model.OrdersTable;
import com.project.e_commerce.Service.OrdersService;
import com.project.e_commerce.Service.PaymentService;
import com.project.e_commerce.Utils.ApiResponse;
import com.stripe.exception.StripeException;
import org.apache.catalina.connector.Response;
import org.springframework.data.repository.config.RepositoryNameSpaceHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("orders")
public class OrdersController {

    private OrdersService ordersService;
    private PaymentService paymentService;

    public OrdersController(OrdersService ordersService, PaymentService paymentService) {
        this.ordersService = ordersService;
        this.paymentService = paymentService;
    }

    @PostMapping("create-order")
    public ResponseEntity<ApiResponse<Object>> createOrder(@RequestBody CreateOrder createOrder) throws StripeException {
        // Call the service to create the order
        ResponseEntity<ApiResponse<Object>> orderCreationResponse =
                ordersService.createOrder(createOrder.getCartId());

        // Extract the response body
        ApiResponse<Object> apiResponse = orderCreationResponse.getBody();

        if (apiResponse != null) {
            // Check the status

            Object data = apiResponse.getData();
            if (data instanceof List<?>) {
                List<OrdersItems> ordersItems = (List<OrdersItems>) data;

                PaymentResponse paymentResponse = paymentService.createPaymentLink(ordersItems);
                Map<String, Object> finalResponse = new HashMap<>();
                finalResponse.put("Order_details", ordersItems);
                finalResponse.put("Strip_payment_link", paymentResponse);


                // You can now use ordersTable as needed
                return new ResponseEntity<>(new ApiResponse<>(true, "Proceed to Payment", finalResponse), HttpStatus.OK);
            } else {
                // Handle the case where the data is not of type OrdersTable
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "Some Error Occurred", null));
            }

        }

        // Handle null response
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Order creation failed", null));
    }


    @PostMapping("delete-order/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteOrder(@PathVariable("id") Integer orderId) {
        return ordersService.deleteOrder(orderId);
    }

    @GetMapping("get-orders")
    public ResponseEntity<ApiResponse<Object>> fetchOrders() {
        return ordersService.fetchOrders();
    }


}
