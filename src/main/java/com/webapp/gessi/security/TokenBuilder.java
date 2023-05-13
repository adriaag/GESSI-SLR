package com.webapp.gessi.security;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.webapp.gessi.config.ConfigParser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class TokenBuilder {
	
	private final static byte[] ACCESS_TOKEN_SECRET = ConfigParser.getConfig().getSecret();
	private final static Long AUTH_TOKEN_LIFE_SECONDS = 24*60*60L;
	private final static Long CHANGE_PASSWORD_TOKEN_LIFE_SECONDS = 1*60*60L;
	
	public static String buildAuthToken(String username) {
		long expirationTime = AUTH_TOKEN_LIFE_SECONDS * 1000;
		Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);
		
		Map<String, Object> tokenData = new HashMap<>();
		tokenData.put("username", username);
		
		return Jwts.builder()
				.setSubject("Auth")
				.setExpiration(expirationDate)
				.addClaims(tokenData)
				.signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET), SignatureAlgorithm.HS256)
				.compact();			
	}
	
	public static String buildChangePwdToken(String username) {
		long expirationTime = CHANGE_PASSWORD_TOKEN_LIFE_SECONDS * 1000;
		Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);
		
		Map<String, Object> tokenData = new HashMap<>();
		tokenData.put("username", username);
		
		return Jwts.builder()
				.setSubject("Change Password")
				.setExpiration(expirationDate)
				.addClaims(tokenData)
				.signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET), SignatureAlgorithm.HS256)
				.compact();			
	}
	
	public static UsernamePasswordAuthenticationToken getAuthenitcation(String token){
		try {
			Claims claims = Jwts.parserBuilder()
					.setSigningKey(ACCESS_TOKEN_SECRET)
					.build()
					.parseClaimsJws(token)
					.getBody();
			
			String nom = (String)claims.get("username");
			return new UsernamePasswordAuthenticationToken(nom, null, Collections.emptyList());
		}
		catch(JwtException e) {
			return null;
		}
	}
	
	public static String decodeUsername(String token){
		try {
			Claims claims = Jwts.parserBuilder()
					.setSigningKey(ACCESS_TOKEN_SECRET)
					.build()
					.parseClaimsJws(token)
					.getBody();
			
			String user = (String)claims.get("username");
			return user;
		}
		catch(JwtException e) {
			return null;
		}
	}

}
