package com.project.e_commerce.Repo;

import com.project.e_commerce.Model.Cart;
import com.project.e_commerce.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepo extends JpaRepository<Cart, Integer> {

    Cart findByUser(Users users);
}
