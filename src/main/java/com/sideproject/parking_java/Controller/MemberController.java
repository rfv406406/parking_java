package com.sideproject.parking_java.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sideproject.parking_java.Exception.AuthenticationError;
import com.sideproject.parking_java.Exception.DatabaseError;
import com.sideproject.parking_java.Exception.InvalidParameterError;
import com.sideproject.parking_java.Model.Member;
import com.sideproject.parking_java.Service.JwtService;
import com.sideproject.parking_java.Service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
@RestController
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private JwtService jwtService;
    
    @PostMapping("/api/member")
    public ResponseEntity<String> postMember(@RequestBody Member member) throws DatabaseError, InvalidParameterError {
       memberService.postMemberService(member);
       ResponseCookie cookie = ResponseCookie.from("RegistrationCompleted", "TRUE").path("/").maxAge(60*5).httpOnly(true).build();
       ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, cookie.toString()).body("ok");
       return response;
    }

    @PostMapping("/api/member/auth")
    public ResponseEntity<String> postMemberAuth(@RequestBody Member member) throws DatabaseError, InvalidParameterError {
        String token = jwtService.returnAuth(member);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body(token);
        return response;
    }

    @GetMapping("/api/member/auth")
    public ResponseEntity<String> getMemberAuth(HttpServletRequest httpRequest) throws AuthenticationError {
        Map<String, Object> payload = memberService.getMemberAuthService(httpRequest);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("payload" + payload);
        return response;
    }

    @GetMapping("/api/member/status")
    public ResponseEntity<String> getMemberStatus(HttpServletRequest httpRequest) throws AuthenticationError {
        String status = memberService.getMemberStatusService(httpRequest);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("status" + status);
        return response;
    }
    
}
