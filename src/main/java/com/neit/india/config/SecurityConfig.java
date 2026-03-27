package com.neit.india.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


/*@EnableWebSecurity*/
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/home", "/about", "/contact", "/students/view", "students/view/{id}", "/login", "/images/**","/css/**", "/js/**").permitAll()

                        // Student pages
                        .requestMatchers("/students/view").permitAll() // if you want public view
                        .requestMatchers("/students/register").hasRole("ADMIN") // only Admin can register
                        .requestMatchers("/students/**").hasRole("STUDENT") // other student URLs

                        // Admin pages
                        .requestMatchers("/admin/**", "/students/**", "/admin-superadmin/**").hasRole("ADMIN")

                        // SuperAdmin pages
                        .requestMatchers("/superadmin/**", "/admin-superadmin/**", "/students/delete/**").hasRole("SUPERADMIN")



                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")              // use your custom login.html
                        .loginProcessingUrl("/login")     // Spring Security handles POST /login
                        .defaultSuccessUrl("/home", true) // redirect here after login
                        .permitAll()
                )

                .logout(logout -> logout.permitAll());
        System.out.println("SecurityConfig run :: ");
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}