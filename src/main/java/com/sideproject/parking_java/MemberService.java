package com.sideproject.parking_java;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.Exception.DatabaseError;
import com.sideproject.parking_java.Exception.InvalidParameterError;
import com.sideproject.parking_java.Model.Member;

@Component
public class MemberService {

    @Autowired
    private MemberDao memberDao;

    public Integer postMemberService(Member member) throws DatabaseError, InvalidParameterError {
        if (member.getAccount() == null || member.getAccount().equals("") ||
            member.getPassword() == null || member.getPassword().equals("") ||
            member.getEmail() == null || member.getEmail().equals("") ||
            member.getCreatTime().equals("")) {
                throw new InvalidParameterError("parameter is null or empty");
            }
        if (!memberDao.getAccountByValueDao(member)) {
            throw new InvalidParameterError("該帳號已被使用!");
        }
        return memberDao.postMemberDao(member);
    }

    @Autowired
    public Member postMemberAuthService(Member member) throws DatabaseError, InvalidParameterError {
        return memberDao.postGetMemberAuthDao(member);
    }
}
