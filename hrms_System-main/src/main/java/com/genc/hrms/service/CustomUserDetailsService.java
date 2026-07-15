package com.genc.hrms.service;

import com.genc.hrms.model.UserDetails;
import com.genc.hrms.repository.UserDetailsRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class
CustomUserDetailsService implements UserDetailsService {

    private final UserDetailsRepository userDetailsRepository;
    public CustomUserDetailsService(UserDetailsRepository userDetailsRepository) {
        this.userDetailsRepository = userDetailsRepository;
    }

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = userDetailsRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (!userDetails.getActive()) {
            throw new UsernameNotFoundException("User account is inactive: " + username);
        }
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + userDetails.getRole())
        );
        return User.builder()
                .username(userDetails.getUsername())
                .password(userDetails.getPassword())
                .authorities(authorities)
                .build();
    }
}

