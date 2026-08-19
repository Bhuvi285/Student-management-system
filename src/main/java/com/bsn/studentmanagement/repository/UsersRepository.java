package com.bsn.studentmanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bsn.studentmanagement.model.Users;

public interface UsersRepository extends JpaRepository<Users, Long> {
	boolean existsByUsername(String username);
	
	Optional<Users> findByUsername(String username);
}
