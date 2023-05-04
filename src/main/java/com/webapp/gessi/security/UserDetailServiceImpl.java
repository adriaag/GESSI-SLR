package com.webapp.gessi.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService{

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		if(username.equals("usuari")) return new UserDetailsImpl(username);
		else {
			System.out.println(username + " doesn't exist");
			throw new UsernameNotFoundException("The user doesn't exist.");
		}
	}

}
