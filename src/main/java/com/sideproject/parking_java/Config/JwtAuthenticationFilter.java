package com.sideproject.parking_java.Config;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.Utility.Jwt;
import com.sideproject.parking_java.Exception.AuthenticationError;

@Component
// @WebFilter(urlPatterns = "/api/member/auth")
public class JwtAuthenticationFilter implements Filter{
    
    @Autowired
    private Jwt jwt;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws AuthenticationError, IOException, ServletException{
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String authorizationHeader = httpRequest.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader != "" && authorizationHeader.startsWith("Bearer ")){
            String accessToken = authorizationHeader.replace("Bearer ", "");
            Map<String, Object> payload = jwt.parseToken(accessToken);
            request.setAttribute("payloads",payload);
            System.out.println("payload" + payload);

            // Log request details
            // System.out.println("Request URI: " + httpRequest.getRequestURI());
            // System.out.println("Remote Address: " + httpRequest.getRemoteAddr());

            // Proceed with the next filter in the chain or the target resource
            chain.doFilter(request, response);

            // Log after response is sent
            // System.out.println("Response sent to client.");
        } else {
            throw new AuthenticationError("AuthorizationHeader is null or empty");
        }
    }
}
