package com.sideproject.parking_java.Utility;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

import com.sideproject.parking_java.Model.Member;
import com.sideproject.parking_java.Utility.TimeFormat;

@Component
public class Jwt {
    private static final long expirationTime = 7*24*60*60*1000;

    @Value("${secretkey}")
    private String secretKey;

    // private int id;
    // private String account;
    // private String email;
    // private Date exp;
    // private Date now;

    // public Jwt(Member memberAuth) {
    //     this.account = memberAuth.getAccount();
    //     this.id = memberAuth.getId();
    //     this.email = memberAuth.getEmail();
    //     this.now = new Date();
    //     this.exp = new Date(now.getTime() + expirationTime);
    // }

    public String generateToken(Member member) {
        // 使用 hmacShaKeyFor 生成 Key 物件
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationTime);

        Claims claim = Jwts.claims();
        claim.setSubject(member.getAccount());
        claim.setExpiration(exp);
        claim.put("id", member.getId());
        claim.put("email", member.getEmail());

        return Jwts.builder()
                .setClaims(claim)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public Claims parseToken(String token) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        System.out.println("KEY: "+ key);

        Jws<Claims> parser = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        System.out.println("parser: "+ parser);
        Claims claims = parser.getBody();

        return claims;
    }
}
