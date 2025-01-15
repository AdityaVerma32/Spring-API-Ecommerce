package com.project.e_commerce.Repo;

import com.project.e_commerce.Model.BlacklistToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenBlacklistedRepo extends JpaRepository<BlacklistToken, Integer> {

    boolean existsByToken(String token);
}
