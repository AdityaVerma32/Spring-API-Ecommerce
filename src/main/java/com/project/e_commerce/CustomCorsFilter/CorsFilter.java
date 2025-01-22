package com.project.e_commerce.CustomCorsFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.*;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class CorsFilter extends OncePerRequestFilter {

    private final CorsProcessor processor = new DefaultCorsProcessor();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        System.out.println(request);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173")); // Frontend origin
        config.setExposedHeaders(Arrays.asList("authorization"));
        config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "authorization"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        source.registerCorsConfiguration("/**",config);

        CorsConfiguration corsConfiguration = source.getCorsConfiguration(request);

        boolean isPreFlight = CorsUtils.isPreFlightRequest(request);

//        // Handle preflight (OPTIONS) request
//        if (isPreFlight) {
//            // Set the necessary headers for preflight request
//            response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
//            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//            response.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization");
//            response.setHeader("Access-Control-Allow-Credentials", "true");
//            response.setHeader("Access-Control-Max-Age", "3600"); // Cache preflight response for 1 hour
//            response.setStatus(HttpServletResponse.SC_OK);
//            return;  // End the filter chain for OPTIONS request
//        }

        boolean isvalid = this.processor.processRequest(corsConfiguration, request, response);
        if(!isvalid || CorsUtils.isPreFlightRequest(request)){
            return;
        }

        filterChain.doFilter(request, response);

    }
}
