package com.example.sto_app.config;

import com.example.sto_app.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public org.springframework.security.authentication.AuthenticationManager authenticationManager(
            org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .cors(cors -> {})

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                                // ========================================
                                // PUBLIC
                                // ========================================

                                .requestMatchers(
                                        "/api/auth/**",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**"
                                ).permitAll()

                                // Клиент может создать заявку
                                .requestMatchers(
                                        org.springframework.http.HttpMethod.POST,
                                        "/api/orders"
                                ).permitAll()

                                // Клиент может проверить заявку по номеру
                                .requestMatchers(
                                        org.springframework.http.HttpMethod.GET,
                                        "/api/orders/number/**"
                                ).permitAll()

                                // ========================================
                                // MENEGER
                                // ========================================

                                .requestMatchers("/api/reports/**")
                                .hasRole("MANAGER")

                                // ========================================
                                // ORDERS
                                // ========================================

                                // Мастер получает только свои заказы
                                .requestMatchers(
                                        org.springframework.http.HttpMethod.GET,
                                        "/api/orders/my"
                                ).hasRole("MASTER")

                                // Дополнительные работы
                                .requestMatchers(
                                        org.springframework.http.HttpMethod.POST,
                                        "/api/orders/*/additional-works"
                                ).hasRole("MASTER")

                                .requestMatchers(
                                        org.springframework.http.HttpMethod.GET,
                                        "/api/orders/*/additional-works"
                                ).hasAnyRole("ADMIN", "MASTER", "MANAGER")

                                .requestMatchers(
                                        org.springframework.http.HttpMethod.PUT,
                                        "/api/orders/additional-works/*/approval"
                                ).hasRole("ADMIN")

                                // Назначение мастера
                                .requestMatchers(
                                        org.springframework.http.HttpMethod.PUT,
                                        "/api/orders/*/assign"
                                ).hasRole("ADMIN")

                                .requestMatchers(
                                        org.springframework.http.HttpMethod.PUT,
                                        "/api/orders/*/diagnostics"
                                ).hasAnyRole("ADMIN", "MASTER")

                                // Изменение статуса
                                .requestMatchers(
                                        org.springframework.http.HttpMethod.PUT,
                                        "/api/orders/*/status"
                                ).hasAnyRole("ADMIN", "MASTER")


                                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/orders/*/items/*")
                                .hasAnyRole("ADMIN", "MASTER")

                                // Добавление услуги / запчасти
                                .requestMatchers(
                                        org.springframework.http.HttpMethod.POST,
                                        "/api/orders/*/items"
                                ).hasAnyRole("ADMIN", "MASTER")

                                 // Удаление услуги / запчасти
                                .requestMatchers(
                                        org.springframework.http.HttpMethod.DELETE,
                                        "/api/orders/*/items/*"
                                ).hasAnyRole("ADMIN", "MASTER")

                                // Оплата
                                .requestMatchers(
                                        org.springframework.http.HttpMethod.PUT,
                                        "/api/orders/*/pay"
                                ).hasAnyRole("ADMIN", "MANAGER")

                                // Закрытие
                                .requestMatchers(
                                        org.springframework.http.HttpMethod.PUT,
                                        "/api/orders/*/close"
                                ).hasAnyRole("ADMIN", "MANAGER")

                                // Получение конкретного заказа
                                .requestMatchers(
                                        org.springframework.http.HttpMethod.GET,
                                        "/api/orders/*"
                                ).hasAnyRole("ADMIN", "MASTER", "MANAGER")

                                // Получение списка заказов
                                .requestMatchers(
                                        org.springframework.http.HttpMethod.GET,
                                        "/api/orders"
                                ).hasAnyRole("ADMIN", "MASTER", "MANAGER")
                                // ========================================
                                // USERS
                                // ========================================

//                                .requestMatchers("/api/users/**")
//                                .hasRole("MANAGER")

                                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/users/**")
                                .hasAnyRole("ADMIN", "MANAGER")

                                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/users/**")
                                .hasRole("MANAGER")

                                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/users/**")
                                .hasRole("MANAGER")

                                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/users/**")
                                .hasRole("MANAGER")
                                // ========================================
                                // SERVICES / PARTS
                                // ========================================

//                              .requestMatchers("/api/services/**").hasRole("ADMIN")
//                              .requestMatchers("/api/parts/**").hasRole("ADMIN")

                                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/services/**").hasAnyRole("ADMIN", "MASTER")
                                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/services/**").hasRole("ADMIN")
                                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/services/**").hasRole("ADMIN")
                                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/services/**").hasRole("ADMIN")

                                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/parts/**").hasAnyRole("ADMIN", "MASTER")
                                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/parts/**").hasRole("ADMIN")
                                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/parts/**").hasRole("ADMIN")
                                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/parts/**").hasRole("ADMIN")


                                // ========================================
                                // EVERYTHING ELSE
                                // ========================================

                                .anyRequest().authenticated()
                )


                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
