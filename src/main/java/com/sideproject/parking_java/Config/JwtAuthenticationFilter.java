package com.sideproject.parking_java.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sideproject.parking_java.exception.AuthenticationError;
import com.sideproject.parking_java.service.UserDetailsServiceImpl;
import com.sideproject.parking_java.utility.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;
    
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws AuthenticationError, IOException, ServletException{
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String authorizationHeader = httpRequest.getHeader("Authorization");
        try {
            if (authorizationHeader != null && !authorizationHeader.isEmpty() && authorizationHeader.startsWith("Bearer ")){
                String accessToken = authorizationHeader.replace("Bearer ", "");
                String account = JwtUtil.parseToken(accessToken);
                UserDetails memberDetails = userDetailsServiceImpl.loadUserByUsername(account);
                Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetails, null, memberDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                // Log request details
                // System.out.println("Request URI: " + httpRequest.getRequestURI());
                // System.out.println("Remote Address: " + httpRequest.getRemoteAddr());
                // Proceed with the next filter in the chain or the target resource
                // Log after response is sent
                // System.out.println("Response sent to client.");
            }
        } catch(AuthenticationError e) {
            throw new AuthenticationError("AuthorizationHeader is null or empty");
        } finally {
            chain.doFilter(request, response);
        }
    }
}
