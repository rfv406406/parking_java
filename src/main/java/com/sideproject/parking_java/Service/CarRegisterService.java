package com.sideproject.parking_java.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sideproject.parking_java.dao.CarRegisterDao;
import com.sideproject.parking_java.model.Car;
import com.sideproject.parking_java.utility.MemberIdUtil;

@Service
public class CarRegisterService {
    @Autowired
    private CarRegisterDao carRegisterDao;

    public void postCarRegisterService(Car car) {
        int memberId = MemberIdUtil.getMemberIdUtil();
        carRegisterDao.postInsertCar(memberId, car);
    }
}
