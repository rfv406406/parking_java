package com.sideproject.parking_java.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.sideproject.parking_java.Model.Member;
import com.sideproject.parking_java.Model.MemberDetails;
import com.sideproject.parking_java.Utility.JwtUtil;

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
