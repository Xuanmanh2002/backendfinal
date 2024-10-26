package com.springproject.dhVinh.SpringBootProject.repository;

import com.springproject.dhVinh.SpringBootProject.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
