package com.sideproject.parking_java.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.sideproject.parking_java.model.Member;
import com.sideproject.parking_java.model.MemberDetails;
import com.sideproject.parking_java.utility.JwtUtil;

@Service
public class JwtService {
    @Autowired
    private AuthenticationManager authenticationManager;

    public HashMap<String, Object> returnAuth(Member member) throws Exception{
        try {
            String account = member.getAccount();
            String password = member.getPassword();
            HashMap<String, Object> tokenObject = new HashMap<>();
            Authentication authToken = new UsernamePasswordAuthenticationToken(account, password);
            Authentication authentication = authenticationManager.authenticate(authToken);
            MemberDetails getPrincipal = (MemberDetails)authentication.getPrincipal();
            String token = JwtUtil.generateToken(getPrincipal);
            tokenObject.put("token", token);
            return tokenObject;
        } catch (AuthenticationException e) {
            throw new Exception("帳號或密碼錯誤");
        }
    }
}
