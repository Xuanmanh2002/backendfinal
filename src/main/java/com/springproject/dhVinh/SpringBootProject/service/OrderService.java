package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.*;
import com.springproject.dhVinh.SpringBootProject.repository.OrderDetailRepository;
import com.springproject.dhVinh.SpringBootProject.repository.OrderRepository;
import com.springproject.dhVinh.SpringBootProject.repository.ServicePackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{
    private final OrderRepository orderRepository;

    private final OrderDetailRepository orderDetailRepository;

    private final ServicePackRepository servicePackRepository;

    @Override
    public List<Order> getAllOrder() {
        return this.orderRepository.findAll();
    }

    @Override
    public Order createOrderAndUpdateService(Long serviceId, Admin admin, Cart cart) {
        if (admin == null) {
            throw new RuntimeException("Admin not found");
        }

        Order order = orderRepository.findByAdmin(admin);
        if(order == null){
            order = new Order();
            order.setAdmins(admin);
            LocalDate localDate = LocalDate.now();
            order.setOrderDate(localDate);
            order.setTotalAmounts(cart.getTotalAmounts());
            order.setTotalValidityPeriod(cart.getTotalValidityPeriod());
        }

        Optional<ServicePack> optionalServicePack = servicePackRepository.findById(serviceId);
        if (!optionalServicePack.isPresent()) {
            throw new RuntimeException("Service not found");
        }

        ServicePack servicePack = optionalServicePack.get();
        Order finalOrder = order;
        List<OrderDetail> orderDetails = finalOrder.getOrderDetails() !=
    }

    @Override
    public Order updateOrder(Long serviceId, Admin admin) {
        return null;
    }

    @Override
    public Order getOrderByEmployer(Admin admin) {
        return null;
    }

    @Override
    public void delelteOrder(Long serviceId, Admin admin) {

    }

    @Override
    public Order findByEmployer(Admin admin) {
        return this.orderRepository.findByAdmin(admin);
    }
}
