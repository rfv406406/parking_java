package com.sideproject.parking_java.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sideproject.parking_java.exception.AuthenticationError;
import com.sideproject.parking_java.exception.DatabaseError;
import com.sideproject.parking_java.exception.InvalidParameterError;
import com.sideproject.parking_java.model.Member;
import com.sideproject.parking_java.service.JwtService;
import com.sideproject.parking_java.service.MemberService;


@RestController
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private JwtService jwtService;
    
    @PostMapping("/api/member")
    public ResponseEntity<String> postMember(@RequestBody Member member) throws DatabaseError, InvalidParameterError {
        memberService.postMemberService(member);
        ResponseCookie cookie = ResponseCookie.from("RegistrationCompleted", "TRUE").path("/").maxAge(60*60*24).build();
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, cookie.toString()).body("OK");
        return response;
    }

    @PostMapping("/api/member/login")
    public ResponseEntity<HashMap<String, Object>> postMemberAuth(@RequestBody Member member) throws DatabaseError, InvalidParameterError {
        HashMap<String, Object> tokenObject = jwtService.returnAuth(member);
        ResponseEntity<HashMap<String, Object>> response = ResponseEntity.status(HttpStatus.OK).body(tokenObject);
        return response;
    }

    @GetMapping("/api/member/auth")
    public ResponseEntity<String> getMemberAuth() throws AuthenticationError {
        String payload = memberService.getMemberAuthService();
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body(payload);
        return response;
    }

    @GetMapping("/api/member/balanceStatus")
    public ResponseEntity<Member> getMemberBalanceAndStatus() throws AuthenticationError {
        Member member = memberService.getMemberBalanceAndStatusService();
        ResponseEntity<Member> response = ResponseEntity.status(HttpStatus.OK).body(member);
        return response;
    }

    @GetMapping("/api/member/memberDetails")
    public ResponseEntity<Member> getMemberDetails() {
        Member memberDetails = memberService.getMemberDetailsService();
        ResponseEntity<Member> response = ResponseEntity.status(HttpStatus.OK).body(memberDetails);

        return response;
    }

    @PutMapping("/api/member/memberDetails/{memberId}")
    public ResponseEntity<String> putMethodName(@PathVariable("memberId") Integer memberId, @RequestBody Member member) {
        memberService.putMemberDetailsService(memberId, member);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("OK");
        
        return response;
    }
    
    
}
