package com.junkard.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.junkard.model.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByDocument(String document);
	long countByRoleName(String roleName);
	List<User> findByNameContainingIgnoreCaseOrDocumentContainingIgnoreCase(String name, String document);

}