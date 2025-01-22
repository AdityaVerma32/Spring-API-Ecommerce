package com.project.e_commerce.Repo;

import com.project.e_commerce.Model.OrdersTable;
import com.project.e_commerce.Model.OrdersItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersItemsRepo extends JpaRepository<OrdersItems, Integer> {
    List<OrdersItems> findByOrders(OrdersTable orders);
}
