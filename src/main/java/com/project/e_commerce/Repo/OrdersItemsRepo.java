package com.project.e_commerce.Repo;

import com.project.e_commerce.Model.OrdersTable;
import com.project.e_commerce.Model.OrdersItems;
import com.project.e_commerce.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdersItemsRepo extends JpaRepository<OrdersItems, Integer> {
    List<OrdersItems> findByOrders(OrdersTable orders);

    OrdersItems findByOrdersAndProduct(OrdersTable existingOrder, Product product);
}
