package com.junkard.config;

import com.junkard.model.Permission;
import com.junkard.model.Role;
import com.junkard.model.User;
import com.junkard.repository.PermissionRepository;
import com.junkard.repository.RoleRepository;
import com.junkard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PermissionRepository permissionRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            System.out.println("No roles found, creating initial data with standardized permissions...");

            // 1. Criar Permissões com nomes padronizados
            Permission accessNewOrder = createPermission("ACCESS_NEW_ORDER");
            Permission accessNewClient = createPermission("ACCESS_NEW_CLIENT");
            Permission accessNewDriver = createPermission("ACCESS_NEW_DRIVER");
            Permission accessRouting = createPermission("ACCESS_ROUTING");
            Permission accessDashboard = createPermission("ACCESS_DASHBOARD");
            Permission accessUserManagement = createPermission("ACCESS_USER_MANAGEMENT");
            Permission accessRoleManagement = createPermission("ACCESS_ROLE_MANAGEMENT"); // <-- NOME PADRONIZADO

            // 2. Criar Perfis (Roles) e associar as permissões
            createRole("ADMIN", Set.of(accessNewOrder, accessNewClient, accessNewDriver, accessRouting, accessDashboard, accessUserManagement, accessRoleManagement));
            createRole("USER", Set.of(accessNewOrder, accessNewClient, accessNewDriver, accessRouting));
            createRole("PENDING_APPROVAL", Collections.emptySet());

            // 3. Criar o Usuário Administrador Padrão
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Default ADMIN role not found"));
            
            if (userRepository.findByDocument("999999999").isEmpty()) {
                User adminUser = new User();
                adminUser.setName("Main Admin");
                adminUser.setDocument("999999999");
                adminUser.setPhone("555-0199");
                adminUser.setPassword(passwordEncoder.encode("admin123"));
                adminUser.setRole(adminRole);
                adminUser.setActive(true);
                userRepository.save(adminUser);
            }
            
            System.out.println("Initial data created successfully!");
        }
    }

    private Permission createPermission(String name) {
        Permission permission = new Permission();
        permission.setName(name);
        return permissionRepository.save(permission);
    }

    private Role createRole(String name, Set<Permission> permissions) {
        Role role = new Role();
        role.setName(name);
        role.setPermissions(permissions);
        return roleRepository.save(role);
    }
}