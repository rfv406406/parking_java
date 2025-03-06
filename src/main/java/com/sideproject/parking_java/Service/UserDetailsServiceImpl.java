package com.sideproject.parking_java.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sideproject.parking_java.Dao.MemberDao;
import com.sideproject.parking_java.Model.Member;
import com.sideproject.parking_java.Model.MemberDetails;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private MemberDao memberDao;
 
	@Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        Member memberAuth = memberDao.postGetMemberAuthDao(account);
        if (memberAuth == null) {
            throw new UsernameNotFoundException("Can't find member: " + memberAuth);
        } else {
			MemberDetails memberDetails = new MemberDetails(memberAuth);
			return memberDetails;
		}
    }
}
