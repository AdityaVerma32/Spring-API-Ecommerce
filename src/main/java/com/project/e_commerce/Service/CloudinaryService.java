package com.project.e_commerce.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    // Initialize Cloudinary instance with credentials
    static Cloudinary cloudinary = new Cloudinary();

    /**
     * Upload an image to Cloudinary.
     *
     * @param file The image file to upload.
     * @return The secure URL of the uploaded image.
     * @throws IOException if an error occurs during upload.
     */
    public String uploadImage(MultipartFile file) {
        try {
            // Attempt to upload the file to Cloudinary
            Map uploadedResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return (String) uploadedResult.get("secure_url");
        } catch (IOException e) {
            // Log the exception for debugging
            System.err.println("Image upload failed: " + e.getMessage());
            e.printStackTrace();

            // Throw a custom exception or handle the error based on your application logic
            throw new RuntimeException("Failed to upload image. Please try again.");
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            System.err.println("Unexpected error during image upload: " + e.getMessage());
            e.printStackTrace();

            // Re-throw as a runtime exception or handle the error
            throw new RuntimeException("An unexpected error occurred while uploading the image.");
        }
    }


    /**
     * Delete an image from Cloudinary using its URL.
     *
     * @param imageUrl The URL of the image to delete.
     * @return true if the image is deleted successfully, false otherwise.
     */
    public boolean deleteImage(String imageUrl) {
        try {
            // Extract the public ID from the image URL
            String publicId = extractPublicIdFromUrl(imageUrl);

            // Delete the image from Cloudinary
            Map deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

            // Check the result of the deletion
            return "ok".equals(deleteResult.get("result"));
        } catch (Exception e) {
            // Log or handle the exception (optional)
            System.err.println("Error while deleting image from Cloudinary: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extract the public ID of the image from its Cloudinary URL.
     *
     * @param imageUrl The full Cloudinary URL of the image.
     * @return The public ID of the image.
     */
    private String extractPublicIdFromUrl(String imageUrl) {
        // Cloudinary URLs typically follow this format:
        // https://res.cloudinary.com/{cloudName}/image/upload/v{version}/{publicId}.{format}
        String[] parts = imageUrl.split("/");
        String publicIdWithFormat = parts[parts.length - 1]; // Extract the last part of the URL
        return publicIdWithFormat.substring(0, publicIdWithFormat.lastIndexOf(".")); // Remove file extension
    }
}
