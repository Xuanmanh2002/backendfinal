package com.springproject.dhVinh.SpringBootProject.repository;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.admins = :admin")
    Order findByAdmin(@Param("admin") Admin admin);

    @Query(value = "SELECT COALESCE(SUM(o.totalAmounts), 0) FROM Order o")
    long countTotalAmounts();

    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM Order o WHERE o.admins.id = :adminId")
    boolean existsByAdminId(@Param("adminId") Long adminId);

    @Query("SELECT o FROM Order o WHERE o.admins.id = :adminId")
    Optional<Order> findByAdminId(@Param("adminId") Long adminId);
}
