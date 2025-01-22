package com.project.e_commerce.Controller;

import com.project.e_commerce.Model.ShippingAddress;
import com.project.e_commerce.Service.ShippingAddressService;
import com.project.e_commerce.Utils.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("shipping")
public class ShippingAddressController {

    public ShippingAddressService shippingAddressService;

    public ShippingAddressController(ShippingAddressService shippingAddressService) {
        this.shippingAddressService = shippingAddressService;
    }

    // Add new Shipping Address
    @PostMapping("add-new")
    public ResponseEntity<ApiResponse<Object>> addAddress(@RequestBody ShippingAddress shippingAddress) {
        return shippingAddressService.createNewAddress(shippingAddress);
    }

    // Delete any Shipping Address
    @DeleteMapping("delete-address/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteAddress(@PathVariable Integer id) {
        return shippingAddressService.deleteAddress(id);
    }

    // Update Shipping Address
    @PutMapping("update-address/{id}")
    public ResponseEntity<ApiResponse<Object>> updateShippingAddress(@RequestBody ShippingAddress shippingAddress, @PathVariable Integer id) {
        return shippingAddressService.updateAddress(id, shippingAddress);
    }

    // Fetching all the Shipping address of the current Users
    @GetMapping("fetch-all")
    public ResponseEntity<ApiResponse<Object>> fetchAllShippingAddresses() {
        return shippingAddressService.fetchAllShippingAddress();
    }

}
