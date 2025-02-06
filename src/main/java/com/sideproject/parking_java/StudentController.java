package com.sideproject.parking_java;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class StudentController {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @PostMapping("/students")
    public String insert(@RequestBody Student student) {
        String sql = "INSERT INTO student(number, name, email) VALUES (:number, :name, :email)";
        // throw new InvalidParameterError("invaild parameter");
        HashMap<String, Object> map = new HashMap<>();
        map.put("number", student.getNumber());
        map.put("name", student.getName());
        map.put("email", student.getEmail());
        namedParameterJdbcTemplate.update(sql, map);
        return "INSERT";
    }
    
}
