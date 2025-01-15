package com.project.e_commerce.Service;

import com.project.e_commerce.Model.Product;
import com.project.e_commerce.Repo.ProductRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepo productRepo;
    private final CloudinaryService cloudinaryService;

    // Constructor for dependency injection
    public ProductService(ProductRepo productRepo, CloudinaryService cloudinaryService) {
        this.productRepo = productRepo;
        this.cloudinaryService = cloudinaryService;
    }

    /**
     * Fetch all products from the database.
     *
     * @return ResponseEntity containing the list of all products with HTTP status.
     */
    public ResponseEntity<List<Product>> getProducts() {
        try {
            List<Product> products = productRepo.findAll();
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fetch a single product by its ID.
     *
     * @param id Product ID.
     * @return ResponseEntity containing the product (if found) with HTTP status.
     */
    public ResponseEntity<Optional<Product>> getProductById(Integer id) {
        try {
            Optional<Product> product = productRepo.findById(id);
            if (product.isPresent()) {
                return new ResponseEntity<>(product, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Optional.empty(), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(Optional.empty(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Create a new product with the given details and image file.
     *
     * @param productName Product name.
     * @param description Product description.
     * @param price Product price.
     * @param stock Product stock count.
     * @param imageFile Image file for the product.
     * @return ResponseEntity with a success or failure message and HTTP status.
     * @throws IOException if an error occurs while uploading the image.
     */
    public ResponseEntity<String> createProduct(
            String productName,
            String description,
            BigDecimal price,
            Integer stock,
            MultipartFile imageFile) {
        try {
            // Store image in Cloudinary and get the image link
            String imageURL = CloudinaryService.uploadImage(imageFile);

            // Check if image upload was successful
            if (imageURL != null) {
                Product product = new Product();
                product.setProductName(productName);
                product.setPrice(price);
                product.setProductImage(imageURL);
                product.setDescription(description);
                product.setStock(stock);

                // Save the product to the database
                Product saved = productRepo.save(product);

                return new ResponseEntity<>("Product created successfully. Product ID: " + saved.getId(), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Failed to upload product image.", HttpStatus.BAD_REQUEST);
            }
        } catch (IOException e) {
            // Handle IOException specifically
            return new ResponseEntity<>("Error uploading image: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            // Handle other exceptions
            return new ResponseEntity<>("An error occurred while creating the product: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a product by its ID.
     *
     * @param prodId Product ID.
     * @return ResponseEntity containing a success or failure message with HTTP status.
     */
    public ResponseEntity<Optional<String>> deleteProductById(Integer prodId) {
        try {
            // Fetch the product by ID
            Optional<Product> productOptional = productRepo.findById(prodId);

            // Check if the product exists
            if (productOptional.isPresent()) {
                Product product = productOptional.get();

                // Extract the image URL from the product
                String imageUrl = product.getProductImage();

                // Delete the image from Cloudinary
                boolean isImageDeleted = cloudinaryService.deleteImage(imageUrl);
                if (!isImageDeleted) {
                    return new ResponseEntity<>(Optional.of("Failed to delete the product image from Cloudinary."),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }

                // Delete the product from the database
                productRepo.deleteById(prodId);

                return new ResponseEntity<>(Optional.of("Product deleted successfully."), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Optional.of("Product not found with ID: " + prodId), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // Handle exceptions and return a response
            return new ResponseEntity<>(Optional.of("An error occurred while deleting the product: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
