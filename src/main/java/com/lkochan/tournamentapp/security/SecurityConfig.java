package com.lkochan.tournamentapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import lombok.AllArgsConstructor;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private BCryptPasswordEncoder passwordEncoder;

    @SuppressWarnings("deprecation")
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeRequests(requests -> requests
                .requestMatchers(HttpMethod.DELETE, "/tournament/**").hasAnyRole("ADMIN", "CREATOR")
                .requestMatchers(HttpMethod.POST, "/tournament").hasAnyRole("CREATOR", "MODERATOR")
                .requestMatchers(HttpMethod.PUT, "/tournament/**").hasAnyRole("CREATOR", "MODERATOR")
                .requestMatchers(HttpMethod.GET, "/tournament/**").permitAll()

                .requestMatchers(HttpMethod.DELETE, "/player/**").hasAnyRole("CREATOR", "MODERATOR")
                .requestMatchers(HttpMethod.POST, "/player/**").hasAnyRole("CREATOR", "MODERATOR")
                .requestMatchers(HttpMethod.PUT, "/player/**").hasAnyRole("CREATOR", "MODERATOR")
                .requestMatchers(HttpMethod.GET, "/player/**").permitAll()

                .requestMatchers(HttpMethod.DELETE, "/match/**").hasAnyRole("CREATOR", "MODERATOR")
                .requestMatchers(HttpMethod.POST, "/match/add/**").hasAnyRole("CREATOR", "MODERATOR")
                .requestMatchers(HttpMethod.PUT, "/match/**").hasAnyRole("CREATOR", "MODERATOR")
                .requestMatchers(HttpMethod.GET, "/match/**").permitAll()

                .requestMatchers(HttpMethod.DELETE, "/user/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/user/register").permitAll()
                .requestMatchers(HttpMethod.PUT, "/user/credentials/*").hasRole("CREATOR")
                .requestMatchers(HttpMethod.PUT, "/user/is-banned/*", "/user/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/user/*").permitAll()
                .anyRequest().authenticated())
            .httpBasic(withDefaults())
            .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    // ADMIN, TOURNAMENT_CREATOR, TOURNAMENT_MANAGER, USER

    @Bean
    public UserDetailsService users() {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin"))
            .roles("ADMIN")
            .build();

        UserDetails creator = User.builder()
            .username("creator")
            .password(passwordEncoder.encode("creator"))
            .roles("CREATOR")
            .build();

        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder.encode("user"))
            .roles("USER")
            .build();
        
        return new InMemoryUserDetailsManager(admin, creator, user);
    }

}
