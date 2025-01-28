package com.project.e_commerce.Repo;

import com.project.e_commerce.Model.ExpiredSessionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpiredSessionIdRepo extends JpaRepository<ExpiredSessionId,Integer> {
    ExpiredSessionId findBySessionId(String sessionId);
}
