package com.junkard.service;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.DisabledException;
import com.junkard.model.User;
import com.junkard.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String document) throws UsernameNotFoundException {
        User user = userRepository.findByDocument(document)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with document: " + document));
        
        if (!user.isActive()) {
            throw new DisabledException("User account is inactive.");
        }

        // Coleta todas as permissões do perfil do usuário
        Collection<GrantedAuthority> authorities = user.getRole().getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toList());

        // Retorna o UserDetails do Spring com as permissões como "authorities"
        return new org.springframework.security.core.userdetails.User(
                user.getDocument(),
                user.getPassword(),
                authorities
        );
    }
}