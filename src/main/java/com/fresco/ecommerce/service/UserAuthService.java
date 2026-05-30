package com.fresco.ecommerce.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fresco.ecommerce.models.User;
import com.fresco.ecommerce.repo.UserRepo;
@Service
public class UserAuthService implements UserDetailsService {

	@Autowired
	private UserRepo userRepo;

	@Override
	public User loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
	}
}