package com.sideproject.parking_java.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class MemberDetails implements UserDetails{

    private final Member member;

    public MemberDetails(Member member) {
        this.member = member;
    }

    @Override
    public String getUsername() {
        return member.getAccount();
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    public int getId() {
        return member.getId();
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> arrayList = new ArrayList<>();
        GrantedAuthority authorities = new SimpleGrantedAuthority(member.getRole());
        arrayList.add(authorities);
        return arrayList;
    }

    public boolean isEnabled() {
        return true;
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }
}
