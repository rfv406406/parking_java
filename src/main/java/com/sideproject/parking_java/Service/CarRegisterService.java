package com.sideproject.parking_java.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sideproject.parking_java.dao.CarRegisterDao;
import com.sideproject.parking_java.model.Car;
import com.sideproject.parking_java.utility.MemberIdUtil;

@Service
public class CarRegisterService {
    @Autowired
    private CarRegisterDao carRegisterDao;

    public List<Car> getCarRegisterData() {
        int memberId = MemberIdUtil.getMemberIdUtil();
        List<Car> car = carRegisterDao.getCarRegisterDataDao(memberId);
        return car;
    }

    public void postCarRegisterService(Car car) {
        int memberId = MemberIdUtil.getMemberIdUtil();
        carRegisterDao.postInsertCar(memberId, car);
        int carId = carRegisterDao.getCarIdDao(memberId);
        carRegisterDao.postInsertCarImage(carId, car);
    }
}
