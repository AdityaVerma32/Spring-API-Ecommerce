package com.project.e_commerce.Repo;

import com.project.e_commerce.Model.Cart;
import com.project.e_commerce.Model.CartItems;
import com.project.e_commerce.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemsRepo extends JpaRepository<CartItems, Integer> {

    CartItems findByProduct(Product prodId);

    CartItems findByCartAndProduct(Cart cart, Product product);

    List<CartItems> findByCart(Cart cart);
}
