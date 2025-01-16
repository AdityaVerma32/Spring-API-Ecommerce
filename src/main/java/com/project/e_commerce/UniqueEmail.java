//package com.project.e_commerce;
//
//import com.project.e_commerce.Service.UniqueEmailValidator;
//import jakarta.validation.Constraint;
//import jakarta.validation.Payload;
//
//import java.lang.annotation.*;
//
//@Documented
//@Constraint(validatedBy = UniqueEmailValidator.class)
//@Retention(RetentionPolicy.RUNTIME)
//@Target({ElementType.FIELD,ElementType.METHOD})
//public @interface UniqueEmail {
//
//    String message() default "Email is already in use";
//    String email() default "";
//
//    Class<?>[] groups() default {};
//
//    Class<? extends Payload>[] payload() default {};
//
//}

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

class Main {

    public static void main(String[] args) throws IOException {
        URL url = new URL("https://api-m.sandbox.paypal.com/v2/checkout/orders");
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("POST");

        httpConn.setRequestProperty("Content-Type", "application/json");
        httpConn.setRequestProperty("PayPal-Request-Id", "7b92603e-77ed-4896-8e78-5dea2050476a");
        httpConn.setRequestProperty("Authorization", "Bearer 6V7rbVwmlM1gFZKW_8QtzWXqpcwQ6T5vhEGYNJDAAdn3paCgRpdeMdVYmWzgbKSsECednupJ3Zx5Xd-g");

        httpConn.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
        writer.write("{\n" +
                "  \"intent\": \"CAPTURE\",\n" +
                "  \"payment_source\": {\n" +
                "    \"paypal\": {\n" +
                "      \"experience_context\": {\n" +
                "        \"payment_method_preference\": \"IMMEDIATE_PAYMENT_REQUIRED\",\n" +
                "        \"landing_page\": \"LOGIN\",\n" +
                "        \"shipping_preference\": \"GET_FROM_FILE\",\n" +
                "        \"user_action\": \"PAY_NOW\",\n" +
                "        \"return_url\": \"https://example.com/returnUrl\",\n" +
                "        \"cancel_url\": \"https://example.com/cancelUrl\"\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"purchase_units\": [\n" +
                "    {\n" +
                "      \"invoice_id\": \"90210\",\n" +
                "      \"amount\": {\n" +
                "        \"currency_code\": \"USD\",\n" +
                "        \"value\": \"230.00\",\n" +
                "        \"breakdown\": {\n" +
                "          \"item_total\": {\n" +
                "            \"currency_code\": \"USD\",\n" +
                "            \"value\": \"220.00\"\n" +
                "          },\n" +
                "          \"shipping\": {\n" +
                "            \"currency_code\": \"USD\",\n" +
                "            \"value\": \"10.00\"\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"items\": [\n" +
                "        {\n" +
                "          \"name\": \"T-Shirt\",\n" +
                "          \"description\": \"Super Fresh Shirt\",\n" +
                "          \"unit_amount\": {\n" +
                "            \"currency_code\": \"USD\",\n" +
                "            \"value\": \"20.00\"\n" +
                "          },\n" +
                "          \"quantity\": \"1\",\n" +
                "          \"category\": \"PHYSICAL_GOODS\",\n" +
                "          \"sku\": \"sku01\",\n" +
                "          \"image_url\": \"https://example.com/static/images/items/1/tshirt_green.jpg\",\n" +
                "          \"url\": \"https://example.com/url-to-the-item-being-purchased-1\",\n" +
                "          \"upc\": {\n" +
                "            \"type\": \"UPC-A\",\n" +
                "            \"code\": \"123456789012\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"name\": \"Shoes\",\n" +
                "          \"description\": \"Running, Size 10.5\",\n" +
                "          \"sku\": \"sku02\",\n" +
                "          \"unit_amount\": {\n" +
                "            \"currency_code\": \"USD\",\n" +
                "            \"value\": \"100.00\"\n" +
                "          },\n" +
                "          \"quantity\": \"2\",\n" +
                "          \"category\": \"PHYSICAL_GOODS\",\n" +
                "          \"image_url\": \"https://example.com/static/images/items/1/shoes_running.jpg\",\n" +
                "          \"url\": \"https://example.com/url-to-the-item-being-purchased-2\",\n" +
                "          \"upc\": {\n" +
                "            \"type\": \"UPC-A\",\n" +
                "            \"code\": \"987654321012\"\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}");
        writer.flush();
        writer.close();
        httpConn.getOutputStream().close();

        InputStream responseStream = httpConn.getResponseCode() / 100 == 2
                ? httpConn.getInputStream()
                : httpConn.getErrorStream();
        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
        String response = s.hasNext() ? s.next() : "";
        System.out.println(response);
    }
}
