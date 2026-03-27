package com.neit.india.service;

import com.neit.india.entity.Student;
import com.neit.india.entity.User;
import com.neit.india.repository.StudentRepository;
import com.neit.india.repository.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.slf4j.Logger;

import java.util.Optional;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    private final StudentRepository studentRepository;

    public CustomUserDetailsService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // First try to load Admin/User
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            logger.info("Loaded admin/user: {} with role {}", user.getUsername(), user.getRole());
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword()) // must be encoded
                    .authorities(new SimpleGrantedAuthority(user.getRole()))
                    .build();
        }

        // Try student by username
        Optional<Student> studentOpt = studentRepository.findByUsername(username);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            logger.info("Loaded student by username: {} with role STUDENT", student.getUsername());
            return org.springframework.security.core.userdetails.User.builder()
                    .username(student.getUsername())
                    .password(student.getPassword())
                    .authorities(new SimpleGrantedAuthority("ROLE_STUDENT"))
                    .build();
        }

        // Try student by email
        studentOpt = studentRepository.findByEmail(username);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            logger.info("Loaded student by email: {} with role STUDENT", student.getEmail());
            return org.springframework.security.core.userdetails.User.builder()
                    .username(student.getEmail())
                    .password(student.getPassword())
                    .authorities(new SimpleGrantedAuthority("ROLE_STUDENT"))
                    .build();
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }

}
