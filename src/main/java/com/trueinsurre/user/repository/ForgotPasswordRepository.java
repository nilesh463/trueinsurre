package com.trueinsurre.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trueinsurre.modal.ForgotPassword;
@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Long>{

	ForgotPassword findByToken(String Token);
}
