package com.devsuperior.dscommerce.users.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Define como as senhas são "hasheadas" (Argon2id).
 *
 * <p>Agora vive no mesmo pacote do {@link Argon2UserPasswordHasher}, que é quem usa este bean: tudo que é
 * detalhe de criptografia fica junto em {@code infrastructure.security}. O domínio só conhece a interface
 * {@code UserPasswordHasher}, nunca Argon2.
 */
@Configuration
class Argon2PasswordEncoderConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        int saltLength = 16;       // 16 bytes
        int hashLength = 32;       // 32 bytes
        int parallelism = 1;       // 1 thread
        int memory = 1 << 14;      // 16384 KB (16 MB)
        int iterations = 2;        // 2 iterações

        return new Argon2PasswordEncoder(
                saltLength,
                hashLength,
                parallelism,
                memory,
                iterations
        );
    }
}
