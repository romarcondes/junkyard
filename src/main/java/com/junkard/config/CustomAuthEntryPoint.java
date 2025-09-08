package com.junkard.config;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        
        // Define o status da resposta para 401 Unauthorized, que é o correto para falha de login
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        // Cria um corpo de resposta JSON customizado
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Unauthorized");
        body.put("message", "Authentication failed: Bad credentials"); // Mensagem amigável
        body.put("path", request.getServletPath());

        // Escreve o JSON na resposta
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}