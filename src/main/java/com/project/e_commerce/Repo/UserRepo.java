package com.project.e_commerce.Repo;


import com.project.e_commerce.Model.Users;
import com.project.e_commerce.Projections.UsersProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users, Integer> {

    Users findByEmail(String email);

    Optional<UsersProjection> findProjectionById(Integer id);

    boolean existsByEmail(String email);
}
