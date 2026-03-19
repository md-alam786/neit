package com.neit.india.config;

import com.neit.india.entity.Role;
import com.neit.india.entity.User;
import com.neit.india.repository.RoleRepository;
import com.neit.india.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/*@Configuration*/
@Component
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    public DataInitializer(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // use passwordEncoder.encode("password") here


    @Bean
    CommandLineRunner initData(RoleRepository roleRepository,
                               UserRepository userRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            // Ensure roles exist
            if (roleRepository.findByName("ROLE_PUBLIC").isEmpty()) {
                roleRepository.save(new Role(null, "ROLE_PUBLIC"));
            }
            if (roleRepository.findByName("ROLE_STUDENT").isEmpty()) {
                roleRepository.save(new Role(null, "ROLE_STUDENT"));
            }
            if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
                roleRepository.save(new Role(null, "ROLE_ADMIN"));
            }
            if (roleRepository.findByName("ROLE_SUPERADMIN").isEmpty()) {
                roleRepository.save(new Role(null, "ROLE_SUPERADMIN"));
            }

            // Ensure a default superadmin exists
//            if (userRepository.findByUsername("superadmin").isEmpty()) {
//                User superadmin = new User();
//                superadmin.setUsername("superadmin");
//                superadmin.setEmail("superadmin@example.com");
//                superadmin.setPassword(passwordEncoder.encode("superadmin123"));
//                superadmin.getRoles().add(roleRepository.findByName("ROLE_SUPERADMIN").get());
//                userRepository.save(superadmin);
//            }
//
//            User admin = new User();
//            admin.setEmail("admin@example.com");
//            admin.setUsername("admin");
//            admin.setPassword(passwordEncoder.encode("admin123"));
//            userRepository.save(admin);
        };
    }
}