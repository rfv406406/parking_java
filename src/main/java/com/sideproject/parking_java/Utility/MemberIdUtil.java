package com.sideproject.parking_java.utility;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sideproject.parking_java.model.MemberDetails;

public class MemberIdUtil {
    public static Integer getMemberIdUtil() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();
        Object principal = auth.getPrincipal();
        if(principal == "anonymousUser") {
            return null;
        } else {
            MemberDetails memberDetails = (MemberDetails)principal;
            int memberId = memberDetails.getId();
            return memberId;
        }
    }
}
