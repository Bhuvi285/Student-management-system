package com.bsn.studentmanagement.service.impl;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bsn.studentmanagement.model.Users;
import com.bsn.studentmanagement.repository.UsersRepository;

@Service
public class UserServiceImpl implements UserDetailsService {

	private UsersRepository userRepository;

	public UserServiceImpl(UsersRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users users = userRepository.findByUsername(username)
		.orElseThrow(() -> new UsernameNotFoundException("Invalid Username")); 
		
		return User.withUsername(username)
				.password(users.getPassword())
				.disabled(!users.isActive())
				.build();
	}

}


/*In simple terms:

It takes the username entered during login → searches the Users table → gets the password and account status → gives that information to Spring Security.

There are two important parts here:

UserServiceImpl → tells Spring Security how to find a user
UsersRepository → tells Spring Data JPA how to search the database
*/



























