package com.sideproject.parking_java;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sideproject.parking_java.Test.Test;


@RestController
public class StudentController {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @PostMapping("/students")
    public String insert(@RequestBody Student student) {
        String sql = "INSERT INTO student(number, name, email, creatTime) VALUES (:number, :name, :email, :creatTime)";
        // if (true) {
        //     throw new RuntimeException("213");
        // }
        LocalDateTime creatTime = LocalDateTime.now();
        Test.getData(creatTime);
        


        HashMap<String, Object> map = new HashMap<>();
        map.put("number", student.getNumber());
        map.put("name", student.getName());
        map.put("email", student.getEmail());
        map.put("creatTime", creatTime);
        namedParameterJdbcTemplate.update(sql, map);
        return "INSERT";
    }
    
}
