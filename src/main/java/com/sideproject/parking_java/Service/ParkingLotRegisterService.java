package com.sideproject.parking_java.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sideproject.parking_java.Dao.ParkingLotDataIdByMemberIdDao;
import com.sideproject.parking_java.Dao.ParkingLotImagesDao;
import com.sideproject.parking_java.Dao.ParkingLotRegisterDao;
import com.sideproject.parking_java.Dao.ParkingLotSquareDao;
import com.sideproject.parking_java.Exception.DatabaseError;
import com.sideproject.parking_java.Exception.InvalidParameterError;
import com.sideproject.parking_java.Model.ParkingLot;
import com.sideproject.parking_java.utility.MemberIdUtil;

@Service
public class ParkingLotRegisterService{
    @Autowired
    private ParkingLotRegisterDao parkingLotRegisterDao;
    @Autowired
    private ParkingLotDataIdByMemberIdDao parkingLotDataIdByMemberIdDao;
    @Autowired
    private ParkingLotImagesDao parkingLotImagesDao;
    @Autowired
    private ParkingLotSquareDao parkingLotSquareDao;

    public void postParkingLotRegister(ParkingLot parkingLot) throws InvalidParameterError, DatabaseError {

        // SecurityContext context = SecurityContextHolder.getContext();
        // Authentication auth = context.getAuthentication();
        // Object principal = auth.getPrincipal();
        // MemberDetails memberDetails = (MemberDetails)principal;
        // int memberId = memberDetails.getId();
        // int memberId = MemberIdUtil.getMemberIdUtil();
        int memberId = MemberIdUtil.getMemberIdUtil();

        if (parkingLot.getName() == null || parkingLot.getName().equals("") ||
            parkingLot.getAddress() == null || parkingLot.getAddress().equals("") ||
            parkingLot.getNearLandmark() == null || parkingLot.getNearLandmark().equals("") ||
            parkingLot.getOpeningTimeAm() == null || parkingLot.getOpeningTimeAm().equals("") ||
            parkingLot.getOpeningTimePm() == null || parkingLot.getOpeningTimePm().equals("") ||
            parkingLot.getSpaceInOut() == null || parkingLot.getSpaceInOut().equals("") ||
            parkingLot.getCarWidth() == null || parkingLot.getCarWidth().equals("") ||
            parkingLot.getCarHeight() == null || parkingLot.getCarHeight().equals("") ||
            parkingLot.getLatitude() == null || parkingLot.getLatitude().equals("") ||
            parkingLot.getLongitude() == null || parkingLot.getLongitude().equals("") ||
            parkingLot.getCarSpaceNumber() == null || parkingLot.getCarSpaceNumber().isEmpty() 
            ) {
            throw new InvalidParameterError("parameter is null or empty");
        }

        int parkingLotRegisterDaoInsertId = parkingLotRegisterDao.postParkingLotRegisterDao(parkingLot, memberId);
        if (parkingLotRegisterDaoInsertId == 0) {
            throw new DatabaseError("ParkingLotRegisterDao inserted failed");
        }

        int parkingLotDataId = parkingLotDataIdByMemberIdDao.getParkingLotDataIdByMemberId(memberId);

        parkingLotImagesDao.input_parkinglotimages(parkingLot, parkingLotDataId);

        int parkingLotDataIdByMemberIdDaoInsertId = parkingLotSquareDao.inputParkingLotSquare(parkingLot, parkingLotDataId);
        if (parkingLotDataIdByMemberIdDaoInsertId == 0) {
            throw new DatabaseError("ParkingLotSquareDao inserted failed");
        }
    }

    public void dateteParkingLotRegister(ParkingLot parkingLot) throws InvalidParameterError, DatabaseError {
        int memberId = MemberIdUtil.getMemberIdUtil();
        
        int insertId = parkingLotRegisterDao.deleteParkingLotRegisterDao(parkingLot, memberId);
        if (insertId == 0) {
            throw new DatabaseError("ParkingLotRegisterDao inserted failed");
        }
    }
}
