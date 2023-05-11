package com.webapp.gessi.security;

import java.sql.SQLException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.webapp.gessi.domain.controllers.UserController;
import com.webapp.gessi.domain.dto.userDTO;

@Service
public class UserDetailServiceImpl implements UserDetailsService{
	
	private UserController userController;
	
	public UserDetailServiceImpl() {
		this.userController = new UserController();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		userDTO user;
		try {
			user = userController.getUser(username);
			if(user != null) return new UserDetailsImpl(user);
			else {
				System.out.println(username + " doesn't exist");
				throw new UsernameNotFoundException("The user doesn't exist.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UsernameNotFoundException("DB Error");
		}
		
		
	}

}
