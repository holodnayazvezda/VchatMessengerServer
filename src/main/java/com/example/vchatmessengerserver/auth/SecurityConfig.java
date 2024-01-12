package com.example.vchatmessengerserver.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, PasswordEncoder passwordEncoder) throws Exception {
        auth
                .authenticationProvider(customAuthenticationProvider)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(basic -> basic
                        .authenticationEntryPoint(new NoPopupBasicAuthenticationEntryPoint()))
                .authorizeHttpRequests((request) -> request.requestMatchers(
                        "/docs",
                        "/docs/**",
                        "/docs.yaml",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/name/**",
                        "/nickname/**",
                        "/password/**",
                        "/nickname-controller/**",
                        "/name-controller/**",
                        "/password-controller/**",
                        "/user/create",
                        "/user-controller/create",
                        "/user/get_base_info",
                        "/user-controller/get_base_info",
                        "/user/exists",
                        "/user-controller/exists",
                        "/user/change_password_by_secret_words",
                        "/user-controller/change_password_by_secret_words")
                        .permitAll().anyRequest().fullyAuthenticated());
        return httpSecurity.build();
    }
}