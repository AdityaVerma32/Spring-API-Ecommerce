package com.project.e_commerce.Repo;

import com.project.e_commerce.Model.Cart;
import com.project.e_commerce.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepo extends JpaRepository<Cart, Integer> {

    @Query(value = "SELECT * FROM cart WHERE order_id = :orderId", nativeQuery = true)
    Cart findByOrderId(@Param("orderId") Integer orderId);

    Cart findByUser(Users users);
}
