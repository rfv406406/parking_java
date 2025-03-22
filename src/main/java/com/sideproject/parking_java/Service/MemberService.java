package com.sideproject.parking_java.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.dao.MemberDao;
import com.sideproject.parking_java.exception.AuthenticationError;
import com.sideproject.parking_java.exception.DatabaseError;
import com.sideproject.parking_java.exception.InvalidParameterError;
import com.sideproject.parking_java.model.Member;
import com.sideproject.parking_java.utility.MemberIdUtil;

@Component
public class MemberService {
    @Autowired
    private MemberDao memberDao;
    @Autowired
	private PasswordEncoder passwordEncoder;

    public Integer postMemberService(Member member) throws DatabaseError, InvalidParameterError {
        if (member.getAccount() == null || member.getAccount().equals("") ||
            member.getPassword() == null || member.getPassword().equals("") ||
            member.getEmail() == null || member.getEmail().equals("")) {
                throw new InvalidParameterError("parameter is null or empty");
            }
        if (!memberDao.getAccountByValueDao(member)) {
            throw new InvalidParameterError("該帳號已被使用!");
        }
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        return memberDao.postMemberDao(member);
    }

    public String getMemberAuthService() throws AuthenticationError {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();
        Object principal = auth.getPrincipal();
        UserDetails userDetails = (UserDetails)principal;
        if (userDetails == null) {
            throw new AuthenticationError("userDetails is null");
        }

        return userDetails.getUsername();
    }

    public String getMemberStatusService() throws AuthenticationError {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();
        Object principal = auth.getPrincipal();
        UserDetails userDetails = (UserDetails)principal;
        String account = userDetails.getUsername();
        Member msmberStatus = memberDao.getMemberStatusByAccount(account);
        String status = msmberStatus.getStatus();
        return status;
    }

    public Member getMemberDetailsService() {
        int memberId = MemberIdUtil.getMemberIdUtil();
        Member memberDetails = memberDao.getMemberDetailsByMemberIdDao(memberId);
        return memberDetails;
    }
}
