package com.sideproject.parking_java.Config;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sideproject.parking_java.Exception.AuthenticationError;
import com.sideproject.parking_java.Service.UserDetailsServiceImpl;
import com.sideproject.parking_java.Utility.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
// @WebFilter(urlPatterns = "/api/member/auth")
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;
    
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws AuthenticationError, IOException, ServletException{
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String authorizationHeader = httpRequest.getHeader("Authorization");
        try {
            if (authorizationHeader != null && authorizationHeader != "" && authorizationHeader.startsWith("Bearer ")){
                String accessToken = authorizationHeader.replace("Bearer ", "");
                Map<String, Object> payload = JwtUtil.parseToken(accessToken);
                System.out.println("payload: "+payload);
                // MemberDetails memberDetails = userDetailsServiceImpl.loadUserByUsername(account);
                // Authentication authentication = new UsernamePasswordAuthenticationToken( account, memberDetails.getPassword(), memberDetails.getAuthorities());
                // SecurityContextHolder.getContext().setAuthentication(authentication);
                // Log request details
                // System.out.println("Request URI: " + httpRequest.getRequestURI());
                // System.out.println("Remote Address: " + httpRequest.getRemoteAddr());
                // Proceed with the next filter in the chain or the target resource
                chain.doFilter(request, response);
    
                // Log after response is sent
                // System.out.println("Response sent to client.");
            }
        } catch(AuthenticationError e) {
            throw new AuthenticationError("AuthorizationHeader is null or empty");
        }
    }
}
