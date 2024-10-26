package com.springproject.dhVinh.SpringBootProject.repository;

import com.springproject.dhVinh.SpringBootProject.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
