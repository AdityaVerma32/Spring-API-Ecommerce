package com.project.e_commerce.Repo;

import com.project.e_commerce.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product,Integer> {
}
