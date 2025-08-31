package com.sideproject.parking_java.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.sideproject.parking_java.dao.MemberDao;
import com.sideproject.parking_java.dao.TransactionDao;
import com.sideproject.parking_java.exception.DatabaseError;
import com.sideproject.parking_java.model.Tappay;
import com.sideproject.parking_java.model.TappayPayload;
import com.sideproject.parking_java.model.TappayResponse;
import com.sideproject.parking_java.model.Transaction;
import com.sideproject.parking_java.utility.MemberIdUtil;
import com.sideproject.parking_java.utility.TimeUtils;

@Service
public class TappayService {
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private TransactionDao transactionDao;

    public String postTappayService(Tappay tappay) throws RestClientException, DatabaseError {
        int memberId = MemberIdUtil.getMemberIdUtil();
        int deposit = tappay.getDeposit();
        Transaction transaction = new Transaction();
        transaction.setAmount(deposit);
        transaction.setTransactionType("DEPOSIT");
        transaction.setStatus("未付款");

        String prime = tappay.getPrime();
        String orderNumber = TimeUtils.timeFormat(new Date()) + Integer.toString(memberId);

        TappayPayload tappayPayload = new TappayPayload();
        tappayPayload.setPrime(prime);
        tappayPayload.setAmount(Integer.toString(deposit));
        tappayPayload.setOrderNumber(orderNumber);

        String PARTNER_KEY = tappayPayload.getPartnerKey();

        try {
            int depositAccountId = memberDao.getDepositAccountIdDao(memberId);
            int insertID = transactionDao.postInsertTransactionDao(memberId, depositAccountId, orderNumber, transaction);

            if (insertID == 0) {
                throw new DatabaseError("transaction insert failed");
            }
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", PARTNER_KEY);
            HttpEntity<Object> requestEntity = new HttpEntity<>(tappayPayload, headers);
            String url = "https://sandbox.tappaysdk.com/tpc/payment/pay-by-prime";
            TappayResponse tappayResponse = restTemplate.postForObject(url, requestEntity, TappayResponse.class);

            if (tappayResponse == null) {
                return new TappayResponse().getMsg();
            } else if (tappayResponse.getMsg().equals("Success")) {
                Date currentTime = new Date();
                String transactionsTime = TimeUtils.timeFormat(currentTime);
                transactionDao.putTappayUpdateStatusDao(orderNumber, depositAccountId, transactionsTime);
                transactionDao.putUpdateBalanceDao(memberId, deposit);
                return tappayResponse.getMsg();
            } else {
                return tappayResponse.getMsg();
            }

        } catch(RestClientException | DatabaseError e) {
            System.err.println("Error: " + e);
            return new TappayResponse().getMsg();
        } 
        
    }
    
}
