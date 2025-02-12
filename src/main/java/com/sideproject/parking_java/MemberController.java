package com.sideproject.parking_java;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sideproject.parking_java.Model.Member;


@RestController
public class MemberController {
    @Autowired
    private MemberService studentService;

    @PostMapping("/students")
    public String insert(@RequestBody Member student) {
       return studentService.StudentInsertService(student);
    }
    
}
