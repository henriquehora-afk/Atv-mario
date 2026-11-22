package com.biblioteca.api.config;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.biblioteca.api.security.JwtTokenService;
import com.biblioteca.api.security.TokenService;

/** Implementações concretas das abstrações de segurança. */
@Configuration
public class SegurancaConfig {

	private static final Logger log = LoggerFactory.getLogger(SegurancaConfig.class);

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public Clock clock() {
		return Clock.systemUTC();
	}

	@Bean
	public TokenService tokenService(
			@Value("${app.jwt.secret:}") String segredo,
			@Value("${app.jwt.access-minutes:15}") long minutosAccess,
			@Value("${app.jwt.refresh-days:7}") long diasRefresh,
			Clock clock) {
		byte[] chave;
		if (segredo == null || segredo.isBlank()) {
			log.warn("JWT_SECRET não definido: usando chave aleatória (sessões caem ao reiniciar a API).");
			chave = new byte[32];
			new SecureRandom().nextBytes(chave);
		} else {
			chave = segredo.getBytes(StandardCharsets.UTF_8);
		}
		return new JwtTokenService(chave, Duration.ofMinutes(minutosAccess), Duration.ofDays(diasRefresh), clock);
	}
}