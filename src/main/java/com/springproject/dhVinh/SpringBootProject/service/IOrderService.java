package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Cart;
import com.springproject.dhVinh.SpringBootProject.model.Order;
import com.springproject.dhVinh.SpringBootProject.model.OrderDetail;

import java.util.List;

public interface IOrderService {

    List<Order> getAllOrder();

    Order createOrderAndUpdateService(Admin admin, Cart cart);

    Order updateOrder(Admin admin, Cart cart);

    Order getOrderByEmployer(Admin admin);

    void deleteOrder(Long orderId);

    Order findByEmployer(Admin admin);

    List<OrderDetail> getAllOrderDetailByOrder(Long orderId);
}
