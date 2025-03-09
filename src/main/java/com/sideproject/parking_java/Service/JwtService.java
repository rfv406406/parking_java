package com.sideproject.parking_java.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.sideproject.parking_java.model.Member;
import com.sideproject.parking_java.model.MemberDetails;
import com.sideproject.parking_java.utility.JwtUtil;

@Service
public class JwtService {
    // @Autowired
    // private MemberDao meberDao;
    // @Autowired
    // private UserDetailsServiceImpl userDetailsServiceImpl;
    @Autowired
    private AuthenticationManager authenticationManager;
  

    public String returnAuth(Member member) {
        String account = member.getAccount();
        String password = member.getPassword();

        Authentication authentication = new UsernamePasswordAuthenticationToken(account, password);
        authentication = authenticationManager.authenticate(authentication);
        System.out.println("authentication: "+authentication);

        MemberDetails getPrincipal = (MemberDetails)authentication.getPrincipal();
        String token = JwtUtil.generateToken(getPrincipal);

        return token;
    }
}
