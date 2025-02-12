package com.sideproject.parking_java;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.Model.TimeFormatModel;

@Component
public class StudentDao {
    
    @Autowired 
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public String insertStudent(Student student) {
        String sql = "INSERT INTO student(number, name, email, creatTime) VALUES (:number, :name, :email, :creatTime)";
        // LocalDateTime creatTime = LocalDateTime.now().withNano(0);       
        HashMap<String, Object> map = new HashMap<>();
        map.put("number", student.getNumber());
        map.put("name", student.getName());
        map.put("email", student.getEmail());
        map.put("creatTime", TimeFormatModel.timeFormat(new Date()));
         if (true) {
            throw new RuntimeException("123");
        } 
        namedParameterJdbcTemplate.update(sql, map);
        return "INSERT";
    }
}
