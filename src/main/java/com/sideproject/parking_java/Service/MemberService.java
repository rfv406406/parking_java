package com.sideproject.parking_java.Service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.Dao.MemberDao;
import com.sideproject.parking_java.Exception.AuthenticationError;
import com.sideproject.parking_java.Exception.DatabaseError;
import com.sideproject.parking_java.Exception.InvalidParameterError;
import com.sideproject.parking_java.Model.Member;
import com.sideproject.parking_java.Utility.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class MemberService {

    @Autowired
    private MemberDao memberDao;
    // @Autowired
	// private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwt;

    public Integer postMemberService(Member member) throws DatabaseError, InvalidParameterError {
        if (member.getAccount() == null || member.getAccount().equals("") ||
            member.getPassword() == null || member.getPassword().equals("") ||
            member.getEmail() == null || member.getEmail().equals("")) {
                throw new InvalidParameterError("parameter is null or empty");
            }
        if (!memberDao.getAccountByValueDao(member)) {
            throw new InvalidParameterError("該帳號已被使用!");
        }
    
        return memberDao.postMemberDao(member);
    }

    // public String postMemberAuthService(Member member) throws DatabaseError, InvalidParameterError {
    //     Member memberAuth = memberDao.postGetMemberAuthDao(member.getAccount());
    //     String token = jwt.generateToken(memberAuth);
    //     return token;
    // }

    public Map<String, Object> getMemberAuthService(HttpServletRequest httpRequest) throws AuthenticationError {
        Map<String, Object> payload = (Map<String, Object>)httpRequest.getAttribute("payloads");
        return payload;
    }

    public String getMemberStatusService(HttpServletRequest httpRequest) throws AuthenticationError {
        Map<String, Object> payload = (Map<String, Object>)httpRequest.getAttribute("payloads");
        System.out.println("httpRequest"+httpRequest);
        int id = (Integer)payload.get("id");
        Member msmberStatus = memberDao.getMemberStatusById(id);
        String status = msmberStatus.getStatus();
        return status;
    }
}
