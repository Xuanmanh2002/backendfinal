package com.springproject.dhVinh.SpringBootProject.repository;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value = "SELECT o FROM Order o WHERE o.orderStatus = ''")
    Order findByAdmin(Admin admin);

//    @Query(value = "SELECT COALESCE(SUM(o.totalAmounts), 0) FROM Order o WHERE o.orderStatus = 'Giao hàng thành công'")
//    long countTotalAmounts();
}
