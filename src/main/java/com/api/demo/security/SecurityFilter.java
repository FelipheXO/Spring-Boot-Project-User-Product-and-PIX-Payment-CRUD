package com.api.demo.security;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.api.demo.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;
    @Autowired
    UserService userService;

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain)
            throws ServletException, IOException {

        final var anAuthToken = request.getHeader("Authorization");

        if (anAuthToken != null) {

            final var aToken = anAuthToken.replace("Bearer ", "");

            final var anUsername = this.tokenService.validateToken(aToken);

            if (anUsername == null || anUsername == "") {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                PrintWriter out = response.getWriter();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                String jsonResponse = "{\"message\":\"session expired\"}";
                out.print(jsonResponse);
                out.flush();
                return;
            }

            final var anUser = this.userService.findById(anUsername);

            final var auth = new UsernamePasswordAuthenticationToken(anUser,
                    null,
                    anUser.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

}