package com.example.jwt.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//工具類別
@Component
public class JwtTokenUtil implements Serializable {

    private static final String CLAIM_KEY_USERNAME = "sub";
    //5天(毫秒)
    private static final long EXPIRATION_TIME = 432000000;
    //JWT密碼
    private static final String SECRET = "secret";
    //簽發JWT
    public String generateToken(UserDetails userDetails){
        Map<String,Object> claims = new HashMap<>(16);
        claims.put(CLAIM_KEY_USERNAME,userDetails.getUsername());
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(Instant.now().toEpochMilli()+EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512,SECRET)
                .compact();
    }

    //驗證JWT
    public Boolean validateToken(String token, UserDetails userDetails){
        User user = (User) userDetails;
        String username = getUsernameFromToken(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }

    //獲取token是否過期
    public Boolean isTokenExpired(String token){
        Date expiration = getExpirationDateFormToken(token);
        return expiration.before(new Date());
    }

    //獲取token的過期時間
    public Date getExpirationDateFormToken(String token){
        Date expiration = getClaimsFormToken(token).getExpiration();
        return expiration;
    }

    //根據token獲取username
    public String getUsernameFromToken(String token){
        String username = getClaimsFormToken(token).getSubject();
        return username;
    }

    //解析JWT
    public Claims getClaimsFormToken(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }

}
