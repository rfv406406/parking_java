package com.sideproject.parking_java;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sideproject.parking_java.Model.Member;


@RestController
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping("/api/member")
    public ResponseEntity<String> postMember(@RequestBody Member member) {
       memberService.postMemberService(member);
       ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("ok");
       return response;
    }
    
}
