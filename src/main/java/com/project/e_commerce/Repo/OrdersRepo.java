package com.project.e_commerce.Repo;

import com.project.e_commerce.Model.OrdersTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepo extends JpaRepository<OrdersTable, Integer> {
}
