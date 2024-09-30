package com.bugwarriors.chickstable.security;

import com.bugwarriors.chickstable.entity.UsersEntity;
import com.bugwarriors.chickstable.repository.UsersRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
	private final UsersRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	UsersEntity user = userRepository.findByUserId(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found.");
        }

        return new AuthDetails(user);
    }

}