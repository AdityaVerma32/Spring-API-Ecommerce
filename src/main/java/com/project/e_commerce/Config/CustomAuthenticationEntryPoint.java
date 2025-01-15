package com.project.e_commerce.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        System.out.println(authException.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 status
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Unauthorized Access\"}");
    }
}
