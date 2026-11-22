package com.biblioteca.api.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.biblioteca.api.security.AutenticacaoInterceptor;
import com.biblioteca.api.security.TokenService;
import com.biblioteca.api.security.UsuarioLogadoArgumentResolver;

/** CORS para o front-end, autenticação nas rotas /api e o resolver de @UsuarioLogado. */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final TokenService tokenService;
	private final String[] origensPermitidas;

	public WebConfig(TokenService tokenService, @Value("${app.frontend-url:http://localhost:3000}") String frontendUrl) {
		this.tokenService = tokenService;
		this.origensPermitidas = frontendUrl.split("\\s*,\\s*");
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
				.allowedOrigins(origensPermitidas)
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
				.allowedHeaders(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE)
				.exposedHeaders(HttpHeaders.CONTENT_DISPOSITION)
				.allowCredentials(true)
				.maxAge(3600);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new AutenticacaoInterceptor(tokenService))
				.addPathPatterns("/api/**")
				.excludePathPatterns("/api/v1/auth/**");
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new UsuarioLogadoArgumentResolver());
	}
}