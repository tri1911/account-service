package account.auth.config;

import account.auth.exception.handler.CustomAccessDeniedHandler;
import account.auth.exception.handler.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(basic -> basic.authenticationEntryPoint(restAuthenticationEntryPoint)) // FIXME: Use Customizer.defaults() or this?
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/signup", "/actuator/shutdown", "/error").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMINISTRATOR")
                        .requestMatchers("/api/acct/**").hasRole("ACCOUNTANT")
                        .requestMatchers("/api/empl/**").hasAnyRole("ACCOUNTANT", "USER")
                        .requestMatchers("/api/security/events/**").hasRole("AUDITOR")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex ->
                        ex
                                .authenticationEntryPoint(restAuthenticationEntryPoint) // FIXME: Is is redundant?
                                .accessDeniedHandler(customAccessDeniedHandler)
                ) // Handle auth errors
                .csrf(AbstractHttpConfigurer::disable) // For Postman
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)) // For the H2 console
                .sessionManagement(sessions -> sessions
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(13);
    }
}
