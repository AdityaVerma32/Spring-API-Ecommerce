package com.project.e_commerce.Controller;

import com.project.e_commerce.Model.Product;
import com.project.e_commerce.Service.ProductService;
import com.project.e_commerce.Utils.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Endpoint to fetch all products.
     *
     * @return ResponseEntity with a list of all products and HTTP status OK.
     */
    @GetMapping("products")
    public ResponseEntity<ApiResponse<Object>> getProducts() {
        try {
            // Fetching the products from the service
            return productService.getProducts();
        } catch (Exception e) {
            // In case of any unexpected error, send a 500 Internal Server Error response
            return new ResponseEntity<>(new ApiResponse<>(false,"Error retrieving products: " + e.getMessage(),null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to fetch a product by its ID.
     *
     * @param id The ID of the product to fetch.
     * @return ResponseEntity with the product and HTTP status OK, or 404 if not found.
     */
    @GetMapping("products/{id}")
    public ResponseEntity<ApiResponse<Object>> getProductById(@PathVariable Integer id) {
        try {
            // Fetching product by ID from the service
            return productService.getProductById(id);
        } catch (Exception e) {
            // In case of any unexpected error, send a 500 Internal Server Error response
            return new ResponseEntity<>(new ApiResponse<>(false,"Error retrieving product with ID: " + id + " - " + e.getMessage(),null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
