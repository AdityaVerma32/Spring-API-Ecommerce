package com.project.e_commerce.Controller;

import com.project.e_commerce.Model.UserPrincipal;
import com.project.e_commerce.Service.CartService;
import com.project.e_commerce.Utils.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("cart")
public class CartController {

    private CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // this function is used to get the User Id of the Current Logged In USER.
    private Integer getUserId() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = userPrincipal.getUserId();
        return userId;
    }

    @PostMapping("add")
    public ResponseEntity<ApiResponse<Object>> addToCart(@RequestBody Integer prodId) {
        return cartService.addToCart(getUserId(), prodId);
    }

    @GetMapping("products")
    public ResponseEntity<ApiResponse<Object>> getCartProducts() {
        return cartService.getCartProducts(getUserId());
    }

    @PostMapping("update-product/{id}")
    public ResponseEntity<ApiResponse<Object>> changeQuantityOfProductInCart(@PathVariable("id") Integer prodId, @RequestBody Map<String, Integer> request) {
        return cartService.updateProductQuantity(getUserId(), prodId, request);
    }

    @DeleteMapping("delete-product/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteProduct(@PathVariable("id") Integer prodId) {
        return cartService.deleteCartProduct(prodId, getUserId());
    }

}
