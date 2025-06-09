package com.sideproject.parking_java.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sideproject.parking_java.dao.MemberDao;
import com.sideproject.parking_java.model.Member;
import com.sideproject.parking_java.model.MemberDetails;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private MemberDao memberDao;
 
	@Override
    public MemberDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        Member memberAuth = memberDao.getMemberAuthDao(account);
        if (memberAuth == null) {
            throw new UsernameNotFoundException("Can't find member: " + memberAuth);
        } else {
			MemberDetails memberDetails = new MemberDetails(memberAuth);
			return memberDetails;
		}
    }
}
