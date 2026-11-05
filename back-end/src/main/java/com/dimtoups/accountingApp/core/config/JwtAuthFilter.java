package com.dimtoups.accountingApp.core.config;

import com.dimtoups.accountingApp.core.helper.JwtHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private final UserDetailsService userDetailsService;
  private final ObjectMapper objectMapper;

  public JwtAuthFilter(UserDetailsService userDetailsService, ObjectMapper objectMapper) {
    this.userDetailsService = userDetailsService;
    this.objectMapper = objectMapper;
  }

  @Override
  @NullMarked
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String authHeader = request.getHeader("Authorization");
      String token = null;
      String username = null;
      if (authHeader != null && authHeader.startsWith("Bearer ")) {
        token = authHeader.substring(7);
        username = JwtHelper.extractUsername(token);
      }
      // If the accessToken is null. It will pass the request to next filter in the chain.
      // Any login and signup requests will not have jwt token in their header, therefore they will be passed to next filter chain.
      if (token == null) {
        filterChain.doFilter(request, response);
        return;
      }
      // If any accessToken is present, then it will validate the token and then authenticate the request in security context
      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (JwtHelper.validateToken(token, userDetails)) {
          UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, null);
          authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
      }

      filterChain.doFilter(request, response);
    } catch (AccessDeniedException e) {
      ErrorResponse errorResponse = new ErrorResponseException(HttpStatusCode.valueOf(403), e);
      response.getWriter().write(toJson(errorResponse));
    }
  }

  private String toJson(ErrorResponse response) {
    try {
      return objectMapper.writeValueAsString(response);
    } catch (Exception e) {
      return ""; // Return an empty string if serialization fails
    }
  }
}
