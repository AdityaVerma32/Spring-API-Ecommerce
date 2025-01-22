package com.project.e_commerce.Controller;


import com.project.e_commerce.DTO.CreateOrder;
import com.project.e_commerce.Service.OrdersService;
import com.project.e_commerce.Utils.ApiResponse;
import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("orders")
public class OrdersController {

    private OrdersService ordersService;

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @PostMapping("create-order")
    public ResponseEntity<ApiResponse<Object>> createOrder(@RequestBody CreateOrder createOrder){
        return ordersService.createOrder(createOrder.getCartId(),createOrder.getAddressId());
    }

    @PostMapping("delete-order/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteOrder(@PathVariable("id") Integer orderId){
        return ordersService.deleteOrder(orderId);
    }


}
