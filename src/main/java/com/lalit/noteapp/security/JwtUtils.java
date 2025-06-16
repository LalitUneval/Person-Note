package com.lalit.noteapp.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    private String getSecretKey(){
        return "yMSC8Ta3Zo0pIDh0MVVrxghb46ks8pqyxfeuNOnSWlY";
    }
    private SecretKey generateKey() {
        byte []decode=Decoders.BASE64.decode(getSecretKey());
        return Keys.hmacShaKeyFor(decode);
    }

    public String generateJwtToken(Authentication authentication){

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // when the user is login it provide that role . This is already converted into simpleGrantAuthorites
        // so know we need back to it in the original form
        String authorities = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("auth",authorities)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+ 5*60*1000))
                .signWith(generateKey())
                .compact();
    }
    Authentication getAuthentication(String token){
        Claims claims = Jwts
                .parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        // actually, here we convert the Array of the role into list because we need to give back to SimpleGrantAuthorities
        Collection<? extends GrantedAuthority> authorities = Arrays
                .stream(
                        claims
                                .get("auth")
                                .toString().
                                split(",")
                ).map(SimpleGrantedAuthority::new).toList();
        User principle = new User(claims.getSubject(),"",authorities);
        return new UsernamePasswordAuthenticationToken(principle , token , authorities);
    }
    public boolean validateJwtToken(String token){
        try{
            Jwts.parser().verifyWith(generateKey()).build().parseSignedClaims(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public String getUserNameFromJwtToken(String token){
        return Jwts.parser().verifyWith(generateKey()).build().parseSignedClaims(token).getPayload().getSubject();
    }
}
