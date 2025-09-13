package com.junkard.service;

import com.junkard.model.User;
import com.junkard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String document) throws UsernameNotFoundException {
        // Busca o usuário pelo documento (que está sendo usado como username)
        User user = userRepository.findByDocument(document)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with document: " + document));

        // Verifica se a conta do usuário está ativa
        if (!user.isActive()) {
            throw new DisabledException("User account is inactive.");
        }

        // 1. Cria uma lista para armazenar todas as permissões (authorities)
        List<GrantedAuthority> authorities = new ArrayList<>();

        // 2. Adiciona o PAPEL (ROLE) do usuário, prefixado com "ROLE_" (padrão do Spring Security)
        // Exemplo: "ROLE_ADMIN"
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));

        // 3. Adiciona todas as PERMISSÕES (PERMISSIONS) detalhadas do papel
        // Exemplo: "ACCESS_USER_MANAGEMENT"
        user.getRole().getPermissions().forEach(permission -> {
            authorities.add(new SimpleGrantedAuthority(permission.getName()));
        });

        // Retorna o objeto UserDetails que o Spring Security usa,
        // agora com a lista completa de papéis e permissões.
        return new org.springframework.security.core.userdetails.User(
                user.getDocument(),
                user.getPassword(),
                authorities
        );
    }
}