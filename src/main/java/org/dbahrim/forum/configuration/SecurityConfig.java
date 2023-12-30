package org.dbahrim.forum.configuration;

import lombok.RequiredArgsConstructor;
import org.dbahrim.forum.configuration.security.JwtFilter;
import org.dbahrim.forum.configuration.security.UserService;
import org.dbahrim.forum.controllers.AuthController;
import org.dbahrim.forum.data.UserRepository;
import org.dbahrim.forum.models.User;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
    private final JwtFilter jwtFilter;
    private final UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception {
        http
            .authenticationProvider(authenticationProvider).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests((auth) -> auth
                .requestMatchers(HttpMethod.OPTIONS).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                .requestMatchers("/api/posts/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/reports/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/signup").anonymous()
                .requestMatchers(HttpMethod.POST, "/api/login").anonymous()
                .requestMatchers("/api/categories/**").hasRole(ROLE_ADMIN)
                .requestMatchers("/api/reports/**").hasRole(ROLE_ADMIN)
                .requestMatchers("/api/vote/**").authenticated()
                .requestMatchers(HttpMethod.GET,"/api/docs/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/swagger-ui**").permitAll()
                .requestMatchers("/" + RestConfiguration.BASE_PATH + "/**").hasRole(ROLE_ADMIN)
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/api/docs/**").permitAll()
                .requestMatchers("/api/docs").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/api/top/**").permitAll()
                .anyRequest().denyAll()
            )
            .csrf(CsrfConfigurer::disable)
            .sessionManagement(SessionManagementConfigurer::disable)
        ;
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                // Spring Security should completely ignore URLs starting with /resources/
                .requestMatchers("/resources/**");
    }

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}

