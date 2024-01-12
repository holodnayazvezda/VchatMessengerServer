package com.example.vchatmessengerserver.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class NoPopupBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        int statusCode = response.getStatus();
        if (statusCode == 200) {
            statusCode = HttpServletResponse.SC_UNAUTHORIZED;
        }
        response.sendError(statusCode, authException.getMessage());
    }
}
