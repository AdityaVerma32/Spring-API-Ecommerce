package com.project.e_commerce.Service;

import com.project.e_commerce.Model.Product;
import com.project.e_commerce.Repo.ProductRepo;
import com.project.e_commerce.Utils.ApiResponse;
import jakarta.transaction.Transactional;
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
    public ResponseEntity<ApiResponse<Object>> getProducts() {
        try {
            List<Product> products = productRepo.findAll();
            return new ResponseEntity<>(new ApiResponse<>(true, "Products", products), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Error Occurred while Fetching data", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fetch a single product by its ID.
     *
     * @param id Product ID.
     * @return ResponseEntity containing the product (if found) with HTTP status.
     */
    public ResponseEntity<ApiResponse<Object>> getProductById(Integer id) {
        try {
            Optional<Product> product = productRepo.findById(id);
            if (product.isPresent()) {
                return new ResponseEntity<>(new ApiResponse<>(true, "Successfully Fetched", product), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse<>(false, "Some Error Occurred", null), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Error Occurred Successfully", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Create a new product with the given details and image file.
     *
     * @param productName        Product name.
     * @param description        Product description.
     * @param price              Product price.
     * @param available_quantity Product stock count.
     * @param imageFile          Image file for the product.
     * @return ResponseEntity with a success or failure message and HTTP status.
     * @throws IOException if an error occurs while uploading the image.
     */
    public ResponseEntity<ApiResponse<Object>> createProduct(
            String productName,
            String description,
            BigDecimal price,
            Integer available_quantity,
            MultipartFile imageFile) {
        try {
            // Store image in Cloudinary and get the image link
            String imageURL = cloudinaryService.uploadImage(imageFile);

            // Check if image upload was successful
            if (imageURL != null) {
                Product product = new Product();
                product.setProductName(productName);
                product.setPrice(price);
                product.setProductImage(imageURL);
                product.setDescription(description);
                product.setAvailable_quantity(available_quantity);
                product.setReserved_quantity(0);

                // Save the product to the database
                Product savedProduct = productRepo.save(product);

                return new ResponseEntity<>(new ApiResponse<>(true, "Product created successfully. Product ID: " + savedProduct.getId(), savedProduct), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(new ApiResponse<>(false, "Failed to upload product image.", null), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            // Handle other exceptions
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while creating the product: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a product by its ID.
     *
     * @param prodId Product ID.
     * @return ResponseEntity containing a success or failure message with HTTP status.
     */
    public ResponseEntity<ApiResponse<Object>> deleteProductById(Integer prodId) {
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
                    return new ResponseEntity<>(new ApiResponse<>(false, "Failed to delete the product image from Cloudinary.", null),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }

                // Delete the product from the database
                productRepo.deleteById(prodId);

                return new ResponseEntity<>(new ApiResponse<>(false, "Product deleted successfully.", null), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse<>(false, "Product not found with ID: " + prodId, null), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // Handle exceptions and return a response
            return new ResponseEntity<>(new ApiResponse<>(false, "An error occurred while deleting the product: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<Object>> updateProduct(
            Integer prodId,
            String productName,
            String description,
            BigDecimal price,
            Integer stock,
            MultipartFile imageFile) {
        try {
            // Fetch the product by ID from the database
            Optional<Product> product = productRepo.findById(prodId);

            System.out.println("Here" + price);
            // Check if the product exists
            if (product.isPresent()) {
                Product existingProduct = product.get();

                // Update product name if provided
                if (!productName.trim().isEmpty()) {
                    existingProduct.setProductName(productName);
                }

                // Update description if provided
                if (!description.trim().isEmpty()) {
                    existingProduct.setDescription(description);
                }

                // Update price if provided
                if (price != null) {
                    existingProduct.setPrice(price);
                }

                // Update stock if provided
                if (stock != null) {
                    existingProduct.setAvailable_quantity(stock);
                }

                // Update product image if a new image file is provided
                if (!imageFile.isEmpty()) {
                    String existingImage = existingProduct.getProductImage();

                    // Upload the new image to Cloudinary
                    String newImage = cloudinaryService.uploadImage(imageFile);

                    // Set the new image URL in the product
                    existingProduct.setProductImage(newImage);

                    // Delete the old image from Cloudinary
                    if (existingImage != null && !existingImage.isEmpty()) {
                        cloudinaryService.deleteImage(existingImage);
                    }
                }

                // Save the updated product to the database
                Product updatedProduct = productRepo.save(existingProduct);

                // Return a successful response
                return new ResponseEntity<>(
                        new ApiResponse<>(true, "Product updated successfully", updatedProduct),
                        HttpStatus.OK
                );
            } else {
                // Return a response indicating the product was not found
                return new ResponseEntity<>(
                        new ApiResponse<>(false, "Product not found", null),
                        HttpStatus.BAD_REQUEST
                );
            }
        } catch (Exception e) {
            // Handle unexpected exceptions
            System.err.println("Unexpected error occurred: " + e.getMessage());
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "An unexpected error occurred while updating the product", null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}

