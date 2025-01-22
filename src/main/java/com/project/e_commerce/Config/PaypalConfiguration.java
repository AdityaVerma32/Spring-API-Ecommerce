package com.project.e_commerce.Config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "paypal")
public class PaypalConfiguration {

    private String baseUrl;
    private String clientId;
    private String secret;
}
