package com.sideproject.parking_java;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sideproject.parking_java.Exception.DatabaseError;
import com.sideproject.parking_java.Exception.InvalidParameterError;
import com.sideproject.parking_java.Model.Member;

import org.springframework.http.ResponseCookie;

@RestController
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping("/api/member")
    public ResponseEntity<String> postMember(@RequestBody Member member) throws DatabaseError, InvalidParameterError {
       memberService.postMemberService(member);
       ResponseCookie cookie = ResponseCookie.from("RegistrationCompleted", "TRUE").path("/").maxAge(60*5).build();
       ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, cookie.toString()).body("ok");
       return response;
    }
}
