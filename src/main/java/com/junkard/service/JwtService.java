package com.junkard.service;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.junkard.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration.ms}")
    private long jwtExpiration;

    /**
     * Extrai o "username" (que no nosso caso é o documento) do token JWT.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Função genérica para extrair uma informação (claim) específica do token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * O MÉTODO PRINCIPAL CORRIGIDO
     * Gera um token JWT para um usuário específico, incluindo seu nome e uma LISTA de permissões.
     * @param user O objeto User completo, vindo do banco de dados.
     * @return Uma string com o Token JWT.
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", user.getName());

        // --- INÍCIO DA CORREÇÃO ---

        // 1. Cria uma lista de Strings para as permissões.
        List<String> authorities = new ArrayList<>();

        // 2. Adiciona o PAPEL (ROLE) à lista (ex: "ROLE_ADMIN").
        authorities.add("ROLE_" + user.getRole().getName());
        
        // 3. Adiciona todas as PERMISSÕES (PERMISSIONS) do papel à lista.
        user.getRole().getPermissions().forEach(permission -> {
            authorities.add(permission.getName());
        });

        // 4. Adiciona a LISTA completa ao token.
        claims.put("authorities", authorities);

        // --- FIM DA CORREÇÃO ---

        return buildToken(claims, user, jwtExpiration);
    }
    
    private String buildToken(Map<String, Object> extraClaims, User user, long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getDocument()) // O 'subject' do token é o documento do usuário
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}