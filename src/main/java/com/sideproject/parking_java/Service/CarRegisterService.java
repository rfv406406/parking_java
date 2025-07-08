package com.sideproject.parking_java.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sideproject.parking_java.dao.CarRegisterDao;
import com.sideproject.parking_java.model.Car;
import com.sideproject.parking_java.utility.MemberIdUtil;

@Service
public class CarRegisterService {
    @Autowired
    private CarRegisterDao carRegisterDao;
    @Autowired
    private AwsS3Service awsS3Service;

    public List<Car> getCarRegisterDataService() {
        int memberId = MemberIdUtil.getMemberIdUtil();
        List<Car> car = carRegisterDao.getCarRegisterDataDao(memberId);
        return car;
    }

    @Transactional
    public void postCarRegisterService(Car car) throws IOException {
        int memberId = MemberIdUtil.getMemberIdUtil();
        carRegisterDao.postInsertCarDao(memberId, car);
        int carId = carRegisterDao.getCarIdDao(memberId);
        if (car.getCarImage() != null) {
            String fileName = awsS3Service.uploadFile(car.getCarImage());
            String imgUrl = awsS3Service.returnUrl(fileName);
            carRegisterDao.postInsertCarImageDao(carId, car, imgUrl);
        }
    }

    public void deleteCarRegisterDataService(Integer carId) {
        int memberId = MemberIdUtil.getMemberIdUtil();
        carRegisterDao.deleteCarDao(carId, memberId);
        carRegisterDao.deleteCarImageDao(carId);
    }
}
