package com.webapp.gessi.security;

import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.io.IOException;


public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		AuthCredentials credentials = new AuthCredentials();
		
		try {
			credentials = new ObjectMapper().readValue(request.getReader(), AuthCredentials.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.io.IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		UsernamePasswordAuthenticationToken usernamePAT = new UsernamePasswordAuthenticationToken(
				credentials.getUsername(), credentials.getPassword(), Collections.emptyList());
		
		return getAuthenticationManager().authenticate(usernamePAT);
		
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request,
            HttpServletResponse response,FilterChain filter, Authentication authResult)
            throws IOException, ServletException, java.io.IOException {
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
		String token = TokenBuilder.buildAuthToken(userDetails.getUsername());
		
		response.addHeader("Authorization", "Bearer "+token);
		response.getWriter().flush();
		
		
		super.successfulAuthentication(request, response, filter, authResult);
		
	}
	


}
