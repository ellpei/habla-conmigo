package com.habla.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AdminUserDetailService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!"admin".equals(username)) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new User(username, "$2a$10$owMFeFhvZbkOC2WOZq07bOE34sNVrxr.s3TN45JrZd.fmNAQOnKvi",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }
}