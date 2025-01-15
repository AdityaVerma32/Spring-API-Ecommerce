package com.project.e_commerce.Repo;


import com.project.e_commerce.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<Users, Integer> {

    Users findByEmail(String email);

    boolean existsByEmail(String email);
}
