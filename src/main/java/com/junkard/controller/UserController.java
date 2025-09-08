package com.junkard.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.junkard.dto.UserCreateDTO;
import com.junkard.model.Role;
import com.junkard.model.User;
import com.junkard.repository.RoleRepository;
import com.junkard.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepository roleRepository;
	

	@PostMapping("/public/register")
	public ResponseEntity<?> publicRegisterUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
		if (userRepository.findByDocument(userCreateDTO.getDocument()).isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Document is already in use!");
		}

		Role pendingRole = roleRepository.findByName("PENDING_APPROVAL")
				.orElseThrow(() -> new RuntimeException("Error: Role PENDING_APPROVAL is not found."));

		User user = new User();
		user.setName(userCreateDTO.getName());
		user.setDocument(userCreateDTO.getDocument());
		user.setPhone(userCreateDTO.getPhone());
		user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));

		user.setRole(pendingRole);
		userRepository.save(user);

		Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully! Awaiting approval.");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

}