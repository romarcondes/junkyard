package com.junkard.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.junkard.dto.RoleDTO;
import com.junkard.dto.RolePermissionUpdateDTO;
import com.junkard.model.Permission;
import com.junkard.model.Role;
import com.junkard.repository.PermissionRepository;
import com.junkard.repository.RoleRepository;

@RestController
@RequestMapping("/api/management")
@PreAuthorize("hasAuthority('ACCESS_ROLE_MANAGEMENT')")
public class RoleManagementController {

    @Autowired private RoleRepository roleRepository;
    @Autowired private PermissionRepository permissionRepository;

    @PostMapping("/roles")
    public ResponseEntity<?> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        if (roleRepository.findByName(roleDTO.getName()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Role name is already in use!");
        }
        Role role = new Role();
        role.setName(roleDTO.getName());
        role.setPermissions(new HashSet<>()); // Começa sem permissões
        roleRepository.save(role);
        return ResponseEntity.ok(Map.of("message", "Role created successfully."));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleRepository.findAll());
    }

    @PutMapping("/roles/{roleId}")
    public ResponseEntity<?> updateRolePermissions(@PathVariable Long roleId, 
                                                   @Valid @RequestBody RolePermissionUpdateDTO updateDTO) {
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
        
        Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(updateDTO.getPermissionIds()));
        role.setPermissions(permissions);
        roleRepository.save(role);
        
        return ResponseEntity.ok(Map.of("message", "Role permissions updated successfully."));
    }
    
    @GetMapping("/permissions")
    public ResponseEntity<List<Permission>> getAllPermissions() {
        return ResponseEntity.ok(permissionRepository.findAll());
    }
}