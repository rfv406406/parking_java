package com.sideproject.parking_java;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudentService {

    @Autowired
    private StudentDao studentDao;

    public String StudentInsertService(Student student) {
        return studentDao.insertStudent(student);
    }
}
