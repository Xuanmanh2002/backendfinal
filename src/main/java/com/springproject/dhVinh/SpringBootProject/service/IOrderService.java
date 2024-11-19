package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Cart;
import com.springproject.dhVinh.SpringBootProject.model.Order;

import java.util.List;

public interface IOrderService {

    List<Order> getAllOrder();

    Order createOrderAndUpdateService(Long serviceId, Admin admin, Cart cart);

    Order updateOrder(Long serviceId, Admin admin);

    Order getOrderByEmployer(Admin admin);

    void delelteOrder(Long serviceId, Admin admin);

    Order findByEmployer(Admin admin);
}
