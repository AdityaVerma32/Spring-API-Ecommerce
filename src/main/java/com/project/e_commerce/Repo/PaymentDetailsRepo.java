package com.project.e_commerce.Repo;

import com.project.e_commerce.Model.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentDetailsRepo extends JpaRepository<PaymentDetails,Integer> {
}
