package com.junkard.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.junkard.dto.UserRoleUpdateDTO;
import com.junkard.model.Role;
import com.junkard.model.User;
import com.junkard.repository.RoleRepository;
import com.junkard.repository.UserRepository;

/**
 * Controller para gerenciar usuários.
 * Todas as operações nesta classe exigem que o usuário autenticado
 * tenha a permissão 'ACCESS_USER_MANAGEMENT'.
 */
@RestController
@RequestMapping("/api/management/users")
@PreAuthorize("hasAuthority('ACCESS_USER_MANAGEMENT')")
public class UserManagementController {

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;

    /**
     * Retorna a contagem de usuários com o perfil 'PENDING_APPROVAL'.
     * Usado para a notificação (sininho) no front-end.
     */
    @GetMapping("/pending/count")
    public ResponseEntity<Map<String, Long>> getPendingUsersCount() {
        long count = userRepository.countByRoleName("PENDING_APPROVAL");
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Retorna uma lista de todos os usuários no sistema.
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * Busca usuários por nome ou documento que contenham o termo fornecido.
     * @param term O termo de busca.
     * @return Uma lista de usuários que correspondem à busca.
     */
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam("term") String term) {
        List<User> users = userRepository.findByNameContainingIgnoreCaseOrDocumentContainingIgnoreCase(term, term);
        return ResponseEntity.ok(users);
    }

    /**
     * Atualiza o perfil (Role) de um usuário específico.
     * Contém regras de segurança para impedir atribuições indevidas.
     */
    @PutMapping("/{userId}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody UserRoleUpdateDTO dto,
            Authentication authentication) { // Spring injeta o usuário autenticado

        Role newRole = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + dto.getRoleId()));
        
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // REGRA 1: Apenas um Admin pode promover outro usuário para Admin.
        if ("ADMIN".equalsIgnoreCase(newRole.getName())) {
            String currentUserDocument = authentication.getName();
            User currentUser = userRepository.findByDocument(currentUserDocument)
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found in database"));

            // Verificamos o NOME do perfil do usuário que está fazendo a ação.
            if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Only administrators can assign the ADMIN role."));
            }
        }

        // REGRA 2: Um admin não pode rebaixar a si mesmo.
        String currentUserDocument = authentication.getName();
        if ("ADMIN".equalsIgnoreCase(userToUpdate.getRole().getName()) && 
            currentUserDocument.equals(userToUpdate.getDocument()) && 
            !"ADMIN".equalsIgnoreCase(newRole.getName())) {
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Administrators cannot remove their own ADMIN role."));
        }
        
        userToUpdate.setRole(newRole);
        userRepository.save(userToUpdate);

        return ResponseEntity.ok(Map.of("message", "User role updated successfully."));
    }

    /**
     * Inativa (bloqueia) a conta de um usuário, setando seu status para 'active = false'.
     */
    @PutMapping("/{userId}/inactivate")
    public ResponseEntity<?> inactivateUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setActive(false);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User inactivated successfully."));
    }

    /**
     * Reativa a conta de um usuário, setando seu status para 'active = true'.
     */
    @PutMapping("/{userId}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setActive(true);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User activated successfully."));
    }
}