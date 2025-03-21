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
import com.sideproject.parking_java.dao.TappayDao;
import com.sideproject.parking_java.exception.DatabaseError;
import com.sideproject.parking_java.model.Tappay;
import com.sideproject.parking_java.model.TappayPayload;
import com.sideproject.parking_java.model.TappayResponse;
import com.sideproject.parking_java.utility.MemberIdUtil;
import com.sideproject.parking_java.utility.TimeFormat;

@Service
public class TappayService {

    @Autowired
    private MemberDao memberDao;
    @Autowired
    private TappayDao tappayDao;

    public String postTappayService(Tappay tappay) throws RestClientException, DatabaseError {
        int memberId = MemberIdUtil.getMemberIdUtil();
        int deposit = tappay.getDeposit();
        String prime = tappay.getPrime();
        String orderNumber = TimeFormat.timeFormat(new Date()) + Integer.toString(memberId);

        TappayPayload tappayPayload = new TappayPayload();
        tappayPayload.setPrime(prime);
        tappayPayload.setAmount(Integer.toString(tappay.getDeposit()));
        tappayPayload.setOrderNumber(orderNumber);

        String PARTNER_KEY = tappayPayload.getPartnerKey();

        try {
            int depositAccountId = memberDao.getDepositAccountIdDao(memberId);
            int insertID = tappayDao.postTappayInsertTransactionsDao(orderNumber, memberId, depositAccountId, deposit);

            if (insertID == 0) {
                throw new DatabaseError("transaction insert failed");
            }
            // 建立 RestTemplate
            RestTemplate restTemplate = new RestTemplate();
            // 設置請求頭
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", PARTNER_KEY);
            // 將 payload 與 headers 封裝到 HttpEntity 中
            HttpEntity<Object> requestEntity = new HttpEntity<>(tappayPayload, headers);
            // 請求 URL
            String url = "https://sandbox.tappaysdk.com/tpc/payment/pay-by-prime";
            // 發送 POST 請求，並接收回應字串
            TappayResponse tappayResponse = restTemplate.postForObject(url, requestEntity, TappayResponse.class);

            if (tappayResponse == null) {
                return new TappayResponse().getMsg();
            } else if (tappayResponse.getMsg().equals("Success")) {
                tappayDao.postTappayUpdateStatusDao(orderNumber, depositAccountId);
                tappayDao.postTappayUpdateBalanceDao(memberId, deposit);
                return tappayResponse.getMsg();
            } else {
                return tappayResponse.getMsg();
            }

        } catch(RestClientException | DatabaseError e) {
            System.out.println("Error: " + e);
            return new TappayResponse().getMsg();
        } 
        
    }
    
}
