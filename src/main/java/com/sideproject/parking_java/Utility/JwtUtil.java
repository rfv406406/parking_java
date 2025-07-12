package com.sideproject.parking_java.utility;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.sideproject.parking_java.model.MemberDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    private static final long EWPIRATION_TIME = 7*24*60*60*1000;

    private static final String SECRET_KEY = "ewrhkerhwekrh28323623423hkjsdhfksdlhfkj234jh23k4hkjsdhfksjdfhlasd123124dsfsdgz";

    public static String generateToken(MemberDetails member) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date exp = new Date(now.getTime() + EWPIRATION_TIME);

        Claims claim = Jwts.claims();
        claim.setSubject(member.getUsername());
        claim.setExpiration(exp);
        claim.put("id", member.getId());

        return Jwts.builder()
                .setClaims(claim)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public static String parseToken(String token) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        Jws<Claims> parser = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        Claims claims = parser.getBody();
        String account = claims.getSubject();

        return account;
    }
}
