package com.govtech.restaurant_selection.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(authorizeHttpRequests ->
                                                authorizeHttpRequests
                                                        .requestMatchers("/login", "/register/**", "/css/**", "/js/**", "/images/**","/favicon.ico", "/h2-console/**")
                                                        .permitAll()
                                                        .requestMatchers("/", "/home","/sessions/**", "/restaurant/**", "/error", "/session/**")
                                                        .authenticated())
                    .formLogin(form -> form
                                        .loginPage("/login")
                                        .defaultSuccessUrl("/")
                                        .loginProcessingUrl("/login")
                                        .failureUrl("/login?error=true")
                                        .permitAll())
                    .logout(logout -> logout.deleteCookies("JSESSIONID")
                                            .invalidateHttpSession(true)
                                            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                            .permitAll());
http.headers().frameOptions().disable();
        return http.build();

    }

    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

}
