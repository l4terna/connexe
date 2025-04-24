package com.laterna.connexemain.v1._shared.security;

import com.laterna.connexemain.v1.user.User;
import com.laterna.connexemain.v1.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class UserIdAuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String userIdHeader = request.getHeader("X-User-Id");

        if (userIdHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Long userId = Long.parseLong(userIdHeader);
            User userDetails = userService.findUserById(userId);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (NumberFormatException e) {
            authenticationEntryPoint.commence(request, response,
                    new BadCredentialsException("Invalid user ID format"));
        } catch (EntityNotFoundException e) {
            authenticationEntryPoint.commence(request, response,
                    new BadCredentialsException("User not found"));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}