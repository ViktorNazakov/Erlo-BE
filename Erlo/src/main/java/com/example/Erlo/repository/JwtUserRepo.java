package com.example.Erlo.repository;

import com.example.Erlo.model.JwtUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JwtUserRepo extends JpaRepository<JwtUser, Long> {
    Optional<JwtUser> findJwtUserByUsername(String username);

    Optional<JwtUser> findJwtUserByEmail(String email);

    JwtUser findByVerificationCode(String verificationCode);

}
