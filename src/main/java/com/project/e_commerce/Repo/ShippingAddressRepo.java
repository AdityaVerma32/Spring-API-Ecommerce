package com.project.e_commerce.Repo;

import com.project.e_commerce.Model.ShippingAddress;
import com.project.e_commerce.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingAddressRepo extends JpaRepository<ShippingAddress, Integer> {

    List<ShippingAddress> findByUser(Users users);
}
