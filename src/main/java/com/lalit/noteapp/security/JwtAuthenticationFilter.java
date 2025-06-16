package com.lalit.noteapp.security;

import com.lalit.noteapp.config.JwtConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final JwtConfig jwtConfig;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, JwtConfig jwtConfig) {
        this.jwtUtils = jwtUtils;
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String token = getJwtFromRequest(request);
            if(StringUtils.hasText(token) && jwtUtils.validateJwtToken(token))
            {
                Authentication  authentication = jwtUtils.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }catch (Exception ex){
            logger.error("Could not set user authentication in context holder "+ex);
        }
        filterChain.doFilter(request , response);
    }
    private String getJwtFromRequest(HttpServletRequest request ){
        String bearerToken = request.getHeader(jwtConfig.getHeader());
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtConfig.getPrefix() + " ")){
            return bearerToken.substring(jwtConfig.getPrefix().length() + 1);
        }
        return null;
    }
}
