package com.sideproject.parking_java.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.sideproject.parking_java.Exception.AuthenticationError;
import com.sideproject.parking_java.Exception.DatabaseError;
import com.sideproject.parking_java.Exception.InvalidParameterError;
import com.sideproject.parking_java.Model.Member;
import com.sideproject.parking_java.Service.MemberService;
import com.sideproject.parking_java.Utility.Jwt;

import io.jsonwebtoken.Claims;
@RestController
public class MemberController {
    @Autowired
    private MemberService memberService;
    
    @PostMapping("/api/member")
    public ResponseEntity<String> postMember(@RequestBody Member member) throws DatabaseError, InvalidParameterError {
       memberService.postMemberService(member);
       ResponseCookie cookie = ResponseCookie.from("RegistrationCompleted", "TRUE").path("/").maxAge(60*5).httpOnly(true).build();
       ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, cookie.toString()).body("ok");
       return response;
    }

    @PostMapping("/api/member/auth")
    public ResponseEntity<String> postMemberAuth(@RequestBody Member member) throws DatabaseError, InvalidParameterError {
        String token = memberService.postMemberAuthService(member);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body(token);
        return response;
    }

    @GetMapping("/api/member/auth")
    public ResponseEntity<String> getMemberAuth(@RequestHeader("Authorization") String authorizationHeader) throws AuthenticationError {
        Claims payload = memberService.getMemberAuthService(authorizationHeader);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("payload" + payload);
        return response;
    }

    @GetMapping("/api/mamber/status")
    public ResponseEntity<String> getMemberStatus(@RequestHeader("Authorization") String authorizationHeader) throws AuthenticationError {
        String status = memberService.getMemberStatusService(authorizationHeader);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("status" + status);
        return response;
    }
    
}
