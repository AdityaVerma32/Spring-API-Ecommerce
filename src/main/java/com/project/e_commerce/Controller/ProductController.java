package com.project.e_commerce.Controller;

import com.project.e_commerce.Model.Product;
import com.project.e_commerce.Service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
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
    public ResponseEntity<?> getProducts() {
        try {
            // Fetching the products from the service
            List<Product> products = productService.getProducts().getBody();

            // Check if the product list is empty
            if (products == null || products.isEmpty()) {
                return new ResponseEntity<>("No products found", HttpStatus.NOT_FOUND);
            }

            // Return the list of products with HTTP status OK
            return new ResponseEntity<>(products, HttpStatus.OK);

        } catch (Exception e) {
            // In case of any unexpected error, send a 500 Internal Server Error response
            return new ResponseEntity<>("Error retrieving products: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to fetch a product by its ID.
     *
     * @param id The ID of the product to fetch.
     * @return ResponseEntity with the product and HTTP status OK, or 404 if not found.
     */
    @GetMapping("product/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Integer id) {
        try {
            // Fetching product by ID from the service
            Optional<Product> product = productService.getProductById(id).getBody();

            // If product is not found, return 404 Not Found
            if (product == null || !product.isPresent()) {
                return new ResponseEntity<>("Product not found with ID: " + id, HttpStatus.NOT_FOUND);
            }

            // Return the product with HTTP status OK
            return new ResponseEntity<>(product.get(), HttpStatus.OK);

        } catch (Exception e) {
            // In case of any unexpected error, send a 500 Internal Server Error response
            return new ResponseEntity<>("Error retrieving product with ID: " + id + " - " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
