package com.laterna.connexeauth.v1._shared.security;


import com.laterna.connexeauth.v1.user.User;
import com.laterna.connexeauth.v1.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class UserIdAuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Пропускаем OPTIONS запросы и публичные эндпоинты
        String path = request.getRequestURI();
        if (request.getMethod().equals("OPTIONS") ||
                path.contains("/api/v1/auth/register") ||
                path.contains("/api/v1/auth/login")) {

            filterChain.doFilter(request, response);
            return; // Важно: сразу возвращаемся и не выполняем дальнейший код
        }

        // Проверяем заголовок X-User-Id
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null) {
            try {
                Long userId = Long.parseLong(userIdHeader);
                User userDetails = userService.findUserById(userId);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Продолжаем обработку с установленной аутентификацией
                filterChain.doFilter(request, response);

                // Очищаем контекст безопасности после обработки запроса
                SecurityContextHolder.clearContext();
                return; // Важно: сразу возвращаемся

            } catch (Exception e) {
                // Ошибка аутентификации - очищаем контекст и продолжаем как неаутентифицированный
                SecurityContextHolder.clearContext();
            }
        }

        // Если нет заголовка или произошла ошибка, продолжаем без аутентификации
        filterChain.doFilter(request, response);
    }
}