package com.project.e_commerce.Repo;

import com.project.e_commerce.Model.OrdersTable;
import com.project.e_commerce.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepo extends JpaRepository<OrdersTable, Integer> {

    // Fetch orders by a specific user using the "user" field
    List<OrdersTable> findByUser(Users user);
}
