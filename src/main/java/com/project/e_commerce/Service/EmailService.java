package com.project.e_commerce.Service;

import com.project.e_commerce.Model.OrdersTable;
import com.project.e_commerce.Model.ShippingAddress;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for handling email operations.
 */
@Service
public class EmailService {

    // JavaMailSender is a Spring-provided interface for sending emails
    private JavaMailSender javaMailSender;

    /**
     * Constructor to initialize JavaMailSender.
     * @param javaMailSender Injected JavaMailSender bean.
     */
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * Sends a registration confirmation email to the user.
     *
     * @param to      The recipient's email address.
     * @param username The username or name of the recipient.
     */
    public void sendRegistrationEmail(String to, String username) {
        // Create a simple email message
        SimpleMailMessage message = new SimpleMailMessage();

        // Set the recipient's email address
        message.setTo(to);

        // Set the subject of the email
        message.setSubject("Registration Successful");

        // Set the body of the email with a personalized message
        message.setText("Hello " + username + ",\n\n" +
                "Thank you for registering with us!\n\n" +
                "Best regards,\nYour Company");

        // Send the email
        javaMailSender.send(message);
    }

    public void sendOrderSuccessFullEmail(String to, String username, OrdersTable orders){
        // Create a simple email message
        SimpleMailMessage message = new SimpleMailMessage();

        // Set the recipient's email address
        message.setTo(to);

        // Set the subject of the email
        message.setSubject("Order Successfully Placed");

        // Get the shipping address from the order object
        ShippingAddress shippingAddress = orders.getShippingAddress();

        // Create a visually appealing email body with HTML content
        String emailContent = "<html>"
                + "<body style='font-family: Arial, sans-serif; color: #333;'>"
                + "<h2 style='color: #4CAF50;'>Hello, " + username + "!</h2>"
                + "<p style='font-size: 16px;'>Your order has been successfully placed. Below are the details:</p>"
                + "<table style='width: 100%; border-collapse: collapse; margin-top: 20px;'>"
                + "<tr>"
                + "<td style='padding: 10px; font-weight: bold; background-color: #f2f2f2;'>Order ID:</td>"
                + "<td style='padding: 10px;'>" + orders.getId() + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td style='padding: 10px; font-weight: bold; background-color: #f2f2f2;'>Total Amount:</td>"
                + "<td style='padding: 10px;'>$" + orders.getTotalAmount() + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td style='padding: 10px; font-weight: bold; background-color: #f2f2f2;'>Shipping Address:</td>"
                + "<td style='padding: 10px;'>"
                + "<strong>Street:</strong> " + shippingAddress.getStreet() + "<br>"
                + "<strong>City:</strong> " + shippingAddress.getCity() + "<br>"
                + "<strong>State:</strong> " + shippingAddress.getState() + "<br>"
                + "<strong>Postal Code:</strong> " + shippingAddress.getPostalCode() + "<br>"
                + "<strong>Country:</strong> " + shippingAddress.getCountry()
                + "</td>"
                + "</tr>"
                + "</table>"
                + "<p style='font-size: 16px;'>Thank you for shopping with us!</p>"
                + "<p style='font-size: 14px;'>If you have any questions, feel free to contact us.</p>"
                + "</body>"
                + "</html>";

        // Set the email text as HTML content
        message.setText(emailContent);

        // Send the email
        javaMailSender.send(message);
    }


}
