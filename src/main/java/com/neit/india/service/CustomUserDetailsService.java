package com.neit.india.service;

import com.neit.india.entity.User;
import com.neit.india.repository.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.stream.Collectors;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));

        logger.debug("User: {} Roles: {}", user.getUsername(), user.getRole());
        logger.info("Loaded user: {} with role: {} has authorities {}", user.getUsername(), user.getRole(), user.getAuthorities());


        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // must be encoded
                .authorities(new SimpleGrantedAuthority(user.getRole())) // single role
                .build();
    }
}
