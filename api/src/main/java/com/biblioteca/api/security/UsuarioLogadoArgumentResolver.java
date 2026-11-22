package com.biblioteca.api.security;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/** Resolve parâmetros anotados com @UsuarioLogado. */
public class UsuarioLogadoArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(UsuarioLogado.class)
				&& Long.class.equals(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(
			MethodParameter parameter,
			ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) {
		Object id = webRequest.getAttribute(
				AutenticacaoInterceptor.ATRIBUTO_USUARIO_ID, RequestAttributes.SCOPE_REQUEST);
		if (id instanceof Long usuarioId) {
			return usuarioId;
		}
		throw new NaoAutenticadoException();
	}
}