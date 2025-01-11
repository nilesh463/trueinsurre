package com.trueinsurre.user.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.trueinsurre.modal.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmail(String email);
	User findByIdAndStatusFalse(Long id);

    Page<User> findAllByOrderByIdDesc(Pageable pageable);

	User findByEmailAndStatus(String email, boolean b);
	
	@Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.status = false ORDER BY u.id DESC")
	Page<User> findByRoleName(@Param("roleName") String roleName, Pageable pageable);
	
	@Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.status = false ORDER BY u.id DESC")
	List<User> findByRoleName(@Param("roleName") String roleName);


}
