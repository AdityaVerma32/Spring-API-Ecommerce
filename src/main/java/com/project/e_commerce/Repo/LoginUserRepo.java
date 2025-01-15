package com.project.e_commerce.Repo;

import com.project.e_commerce.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginUserRepo extends JpaRepository<Users, Integer> {
}
