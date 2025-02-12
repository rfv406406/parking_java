package com.sideproject.parking_java;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.Model.Member;

@Component
public class MemberService {

    @Autowired
    private MemberDao memberDao;

    public Integer postMemberService(Member member) {
        return memberDao.postMemberDao(member);
    }
}
