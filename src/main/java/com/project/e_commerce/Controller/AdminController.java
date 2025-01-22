package com.project.e_commerce.Controller;

import com.project.e_commerce.Model.Product;
import com.project.e_commerce.Service.ProductService;
import com.project.e_commerce.Utils.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("admin")
public class AdminController {

    private final ProductService productService;

    // Constructor for dependency injection
    public AdminController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Fetch all products from the database.
     *
     * @return ResponseEntity containing the list of all products with HTTP status.
     */
    @GetMapping("products")
    public ResponseEntity<ApiResponse<Object>> getProducts() {
        try {
            return productService.getProducts();
        } catch (Exception e) {
            // Handle unexpected exceptions
            return ResponseEntity.internalServerError().body(null);
        }
    }

    /**
     * Create a new product with the given details.
     *
     * @param product_name Name of the product.
     * @param description  Description of the product.
     * @param price        Price of the product.
     * @param stock        Stock quantity of the product.
     * @param imageFile    Image file of the product.
     * @return ResponseEntity with success or failure message and HTTP status.
     * @throws IOException if an error occurs while uploading the image.
     */
    @PostMapping("product")
    public ResponseEntity<ApiResponse<Object>> createProduct(
            @RequestParam("product_name") String product_name,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("stock") Integer stock,
            @RequestParam("image") MultipartFile imageFile) {
        try {
            return productService.createProduct(product_name, description, price, stock, imageFile);
        } catch (Exception e) {
            // Handle unexpected exceptions
            return ResponseEntity.internalServerError().body(new ApiResponse<>(false, "An unexpected error occurred: " + e.getMessage(), null));
        }
    }

    /**
     * Fetch a single product by its ID.
     *
     * @param prodId ID of the product to fetch.
     * @return ResponseEntity containing the product (if found) with HTTP status.
     */
    @GetMapping("product/{id}")
    public ResponseEntity<ApiResponse<Object>> getProductById(@PathVariable("id") Integer prodId) {
        try {
            return productService.getProductById(prodId);
        } catch (Exception e) {
            // Handle unexpected exceptions
            return ResponseEntity.internalServerError().body(new ApiResponse<>(false, "Some Error Occurred", null));
        }
    }

    /**
     * Delete a product by its ID, including its associated image from Cloudinary.
     *
     * @param prodId ID of the product to delete.
     * @return ResponseEntity containing success or failure message with HTTP status.
     */
    @DeleteMapping("product/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteProductById(@PathVariable("id") Integer prodId) {
        try {
            return productService.deleteProductById(prodId);
        } catch (Exception e) {
            // Handle unexpected exceptions
            return ResponseEntity.internalServerError().body(new ApiResponse<>(false, "An unexpected error occurred: " + e.getMessage(), null));
        }
    }

    @PutMapping("product/{id}")
    public ResponseEntity<ApiResponse<Object>> updateProduct(@PathVariable("id") Integer prodId,
                                                             @RequestParam(value = "product_name", required = false) String product_name,
                                                             @RequestParam(value = "description", required = false) String description,
                                                             @RequestParam(value = "price", required = false) BigDecimal price,
                                                             @RequestParam(value = "stock", required = false) Integer stock,
                                                             @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        System.out.println("Debug1 " + price);
        try {
            return productService.updateProduct(prodId, product_name, description, price, stock, imageFile);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse<>(false, "An unexpected error occurred: " + e.getMessage(), null));
        }
    }

}
