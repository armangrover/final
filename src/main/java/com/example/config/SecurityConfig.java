package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class SecurityConfig {

        @Autowired
        private UserDetailsService userDetailsService;

        @Bean
        @Order(1)
        public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
                http
                                .securityMatcher("/api/**")
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/api/auth/register",
                                                                "/api/auth/login",
                                                                "/api/auth/logout",
                                                                "/api/charts/**",
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .httpBasic();
                return http.build();
        }

        @Bean
        @Order(2)
        public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/",
                                                                "/login",
                                                                "/register",
                                                                "/css/**",
                                                                "/js/**",
                                                                "/images/**",
                                                                "/favicon.ico",
                                                                "/error",
                                                                "/logout")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/dashboard") // Removed 'true' parameter
                                                .failureUrl("/login?error=true")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll())
                                .sessionManagement(session -> session
                                                .sessionFixation().migrateSession()
                                                .maximumSessions(1)
                                                .and()
                                );
                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder);
                return authProvider;
        }
}