package com.webapp.gessi.security;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class TokenBuilder {
	
	private final static String ACCESS_TOKEN_SECRET = "abcd"; //placeholder
	private final static Long ACCESS_TOKEN_LIFE_SECONDS = 24*60*60L;
	
	public static String buildToken(String username) {
		long expirationTime = ACCESS_TOKEN_LIFE_SECONDS * 1000;
		Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);
		
		Map<String, Object> tokenData = new HashMap<>();
		tokenData.put("username", username);
		
		return Jwts.builder()
				.setSubject("proves")
				.setExpiration(expirationDate)
				.addClaims(tokenData)
				.signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes()))
				.compact();			
	}
	
	public static UsernamePasswordAuthenticationToken getAuthenitcation(String token) throws JwtException{
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(ACCESS_TOKEN_SECRET.getBytes())
				.build()
				.parseClaimsJws(token)
				.getBody();
		
		String nom = claims.getSubject();
		return new UsernamePasswordAuthenticationToken(nom, null, Collections.emptyList());
	}

}
