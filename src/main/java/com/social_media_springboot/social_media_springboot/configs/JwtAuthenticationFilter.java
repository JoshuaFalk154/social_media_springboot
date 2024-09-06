package com.social_media_springboot.social_media_springboot.configs;


import com.social_media_springboot.social_media_springboot.services.JwtService;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractUsername(jwt);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (userEmail != null && authentication == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    throw new AccessDeniedException("JWT token is invalid!");
                }
            }

            filterChain.doFilter(request, response);
        } catch (UsernameNotFoundException e) {
            // return HTTPNotFound
            generateHTTPResponse(response, 404, "Username does not exist.");
        } catch (AccessDeniedException e) {
            // return HTTPForbidden
            generateHTTPResponse(response, 403, "Access denied, please check your credentials.");
        } catch (SignatureException e) {
            generateHTTPResponse(response, 400, "JWT signature does not match locally computed signature, please check your JWT-Token.");
        } catch (Exception e) {
            // return HTTPInternalServerError
            generateHTTPResponse(response, 500, "An error occurred while processing your request.");
        }

    }

    private void generateHTTPResponse(HttpServletResponse response, int status, String text) throws IOException {
        response.setStatus(status);
        response.setContentType("text/plain");
        response.getWriter().write(text);
    }
}
