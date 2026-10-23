package com.devsuperior.dscommerce.shared.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de segurança HTTP da aplicação inteira.
 *
 * <p>Fica em {@code shared} (e não em {@code users}) porque a regra vale para <b>todos</b> os módulos: ela
 * não pertence ao domínio de usuários, apesar de, no futuro, depender dele para autenticar.
 */
@Configuration
public class SecurityConfig {

    /*
    * nenhuma autenticação é exigida no momento.
    * */

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers ->
                        headers.frameOptions(
                                HeadersConfigurer.FrameOptionsConfig::disable
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}
