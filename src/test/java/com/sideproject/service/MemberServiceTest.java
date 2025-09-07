package com.sideproject.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sideproject.parking_java.dao.MemberDao;
import com.sideproject.parking_java.exception.AuthenticationError;
import com.sideproject.parking_java.exception.InvalidParameterError;
import com.sideproject.parking_java.model.Member;
import com.sideproject.parking_java.model.MemberDetails;
import com.sideproject.parking_java.service.MemberService;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberDao memberDao;

    @Mock
	private PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() { SecurityContextHolder.clearContext(); }

    @Test
    public void testPostMemberServiceWhenParasAreBlank() throws InvalidParameterError {
        Member member = new Member();
        Assertions.assertThrows(InvalidParameterError.class, () -> {
            memberService.postMemberService(member);
        });
    }
    @Test
    public void testPostMemberServiceWhenAccountIsUsed() throws InvalidParameterError {
        Member member = new Member();
        member.setAccount("testAccount");
        member.setPassword("testPassword");
        member.setEmail("testEmail");
        when(memberDao.getAccountByValueDao(member)).thenReturn(false);
        Assertions.assertThrows(InvalidParameterError.class, () -> {
            memberService.postMemberService(member);
        });
    }
    @Test
    public void testPostMemberServiceEncodeAndCallDao() {
        Member member = new Member();
        member.setAccount("testAccount");
        member.setPassword("testPassword");
        member.setEmail("testEmail");

        when(memberDao.getAccountByValueDao(member)).thenReturn(true);
        when(passwordEncoder.encode("testPassword")).thenReturn("encodedPassword");
        when(memberDao.postMemberDao(member)).thenReturn(1);

        Integer id = memberService.postMemberService(member);

        verify(passwordEncoder).encode("testPassword");

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberDao).postMemberDao(captor.capture());
        Assertions.assertEquals("encodedPassword", captor.getValue().getPassword());
        Assertions.assertEquals(1, id);
    }

    @Test 
    public void testGetMemberAuthServiceWhenUnauthenticated() throws AuthenticationError{
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(null, null));
        SecurityContextHolder.setContext(context);

        Assertions.assertThrows(AuthenticationError.class, () -> {
            memberService.getMemberAuthService();
        });
    }

    @Test 
    public void testGetMemberAuthServiceWhenPrincipalTypeError() throws AuthenticationError{
        Member member = new Member();
        member.setAccount("testAccount");
        member.setPassword("testPassword");
        member.setEmail("testEmail");
        member.setRole("testRole");
        
        MemberDetails memberDetails = new MemberDetails(member);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken("anonymousUser", null, memberDetails.getAuthorities()));
        SecurityContextHolder.setContext(context);

        Assertions.assertThrows(AuthenticationError.class, () -> {
            memberService.getMemberAuthService();
        });
    }

    @Test
    public void testGetMemberAuthServiceSuccess() {
        Member member = new Member();
        member.setAccount("testAccount");
        member.setPassword("testPassword");
        member.setEmail("testEmail");
        member.setRole("testRole");
        
        MemberDetails memberDetails = new MemberDetails(member);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(memberDetails, null, memberDetails.getAuthorities()));
        SecurityContextHolder.setContext(context);

        MemberDetails result = memberService.getMemberAuthService();
        Assertions.assertSame(memberDetails, result);
    }

    @Test
    public void testGetMemberBalanceAndStatusServiceWhenUnauthenticated() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        SecurityContextHolder.setContext(context);

        Assertions.assertThrows(NullPointerException.class, () -> {
            memberService.getMemberBalanceAndStatusService();
        });
    }

    @Test
    public void testGetMemberBalanceAndStatusServiceWhenPrincipalTypeError() {
        Member member = new Member();
        member.setAccount("testAccount");
        member.setPassword("testPassword");
        member.setEmail("testEmail");
        member.setRole("testRole");
        
        MemberDetails memberDetails = new MemberDetails(member);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken("anonymousUser", null, memberDetails.getAuthorities()));
        SecurityContextHolder.setContext(context);

        Assertions.assertThrows(ClassCastException.class, () -> {
            memberService.getMemberBalanceAndStatusService();
        });
    }

    @Test
    public void testGetMemberBalanceAndStatusServiceWhenSuccess() {
        Member member = new Member();
        member.setAccount("testAccount");
        member.setPassword("testPassword");
        member.setEmail("testEmail");
        member.setRole("testRole");
        
        MemberDetails memberDetails = new MemberDetails(member);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(memberDetails, null, memberDetails.getAuthorities()));
        SecurityContextHolder.setContext(context);

        Member mockMember = new Member();
        mockMember.setBalance(100);
        mockMember.setStatus("閒置");

        when(memberDao.getMemberBalanceAndStatusByAccount("testAccount")).thenReturn(mockMember);

        Member result = memberService.getMemberBalanceAndStatusService();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(100, result.getBalance());
        Assertions.assertEquals("閒置", result.getStatus());
    }
}

