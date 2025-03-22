package com.sideproject.parking_java.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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
    public void doFilterInternal(@org.springframework.lang.NonNull HttpServletRequest request, @org.springframework.lang.NonNull HttpServletResponse response, @org.springframework.lang.NonNull FilterChain chain) throws IOException, ServletException{
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String authorizationHeader = httpRequest.getHeader("Authorization");
        
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
        
        chain.doFilter(request, response);
        
    }
}
