package com.project.e_commerce.Service;

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
}
