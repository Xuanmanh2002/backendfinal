package com.springproject.dhVinh.SpringBootProject.repository;

import com.springproject.dhVinh.SpringBootProject.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Query("SELECT od FROM OrderDetail od WHERE od.services.id = :serviceId")
    OrderDetail findByService(@Param("serviceId") Long serviceId);

    @Query("SELECT COALESCE(SUM(od.quantity), 0) FROM OrderDetail od WHERE od.orders.id = :orderId")
    long sumQuantityByOrder(@Param("orderId") Long orderId);

    @Query("SELECT COALESCE(SUM(od.totalAmounts), 0) FROM OrderDetail od WHERE od.orders.id = :orderId")
    long sumTotalAmountsByOrder(@Param("orderId") Long orderId);

    @Query("SELECT COALESCE(SUM(od.totalValidityPeriod), 0) FROM OrderDetail od WHERE od.orders.id = :orderId")
    long sumValidityPeriodByOrder(@Param("orderId") Long orderId);

    @Query("SELECT od FROM OrderDetail od WHERE od.orders.id = :orderId")
    List<OrderDetail> findByOrderId(@Param("orderId") Long orderId);
}
