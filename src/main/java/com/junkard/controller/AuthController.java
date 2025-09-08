package com.junkard.controller;

import com.junkard.dto.LoginRequest;
import com.junkard.dto.LoginResponseDTO;
import com.junkard.model.User;
import com.junkard.repository.UserRepository;
import com.junkard.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        
        // 1. Spring Security ainda valida se a senha está correta
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getDocument(),
                        loginRequest.getPassword()
                )
        );

        // 2. Buscamos o usuário completo no banco de dados
        final User user = userRepository.findByDocument(loginRequest.getDocument())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));
        
        // 3. A NOVA TRAVA DE SEGURANÇA!
        // Verificamos o nome do perfil (Role) do usuário.
        if ("PENDING_APPROVAL".equals(user.getRole().getName())) {
            // Se for pendente, retornamos um erro 403 (Forbidden) com uma mensagem clara.
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Access Denied");
            errorResponse.put("message", "Your account is pending approval. You cannot log in at this time.");
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
        
        // 4. Se o usuário NÃO for pendente, o fluxo continua normalmente e o token é gerado.
        final String jwt = jwtService.generateToken(user);

        return ResponseEntity.ok(new LoginResponseDTO(jwt));
    }
}