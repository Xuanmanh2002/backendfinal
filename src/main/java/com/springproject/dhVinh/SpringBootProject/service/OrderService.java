package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.*;
import com.springproject.dhVinh.SpringBootProject.repository.*;
import com.springproject.dhVinh.SpringBootProject.response.CartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{
    private final OrderRepository orderRepository;

    private final OrderDetailRepository orderDetailRepository;

    private final ServicePackRepository servicePackRepository;

    private final AdminRepository employerRepository;

    private final CartItemRepository cartItemRepository;

    private final CartRepository cartRepository;

    @Override
    public List<Order> getAllOrder() {
        return this.orderRepository.findAll();
    }

    @Override
    public Order createOrderAndUpdateService(Admin admin, Cart cart) {
        if (admin == null) {
            throw new RuntimeException("Admin not found");
        }
        admin.setSalaryRange("gold");
        employerRepository.save(admin);
        Order order = orderRepository.findByAdmin(admin);
        if (order == null) {
            order = new Order();
            order.setAdmins(admin);
            order.setOrderDate(LocalDate.now());
            order.setTotalAmounts(cart.getTotalAmounts() * 0.08);
            order.setTotalValidityPeriod(cart.getTotalValidityPeriod());
            order.setOrderStatus("Chuyển khoản thành công");
            order.setOrderDetails(new ArrayList<>());
        }
        List<OrderDetail> orderDetails = order.getOrderDetails() != null ? order.getOrderDetails() : new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            ServicePack servicePack = cartItem.getServices();
            OrderDetail orderDetail = orderDetails.stream()
                    .filter(detail -> detail.getServices().getId().equals(servicePack.getId()))
                    .findFirst()
                    .orElse(null);
            if (orderDetail == null) {
                orderDetail = new OrderDetail();
                orderDetail.setOrders(order);
                orderDetail.setServices(cartItem.getServices());
                orderDetail.setPrice(servicePack.getPrice());
                orderDetail.setQuantity(cartItem.getQuantity());
                orderDetail.setTotalAmounts(cartItem.getTotalPrice());
                orderDetail.setTotalValidityPeriod(cartItem.getTotalValidityPeriod());
                orderDetails.add(orderDetail);
                servicePack.setQuantity(servicePack.getQuantity() - orderDetail.getQuantity());
            } else {
                orderDetail.setQuantity(orderDetail.getQuantity() + cartItem.getQuantity());
                orderDetail.setTotalAmounts(orderDetail.getTotalAmounts() + cartItem.getTotalPrice());
                servicePack.setQuantity(servicePack.getQuantity() - cartItem.getQuantity());
            }
            servicePackRepository.save(servicePack);
        }
        order.setOrderDetails(orderDetails);
        orderRepository.save(order);
        cartItemRepository.deleteAll(cart.getCartItems());
        cartRepository.delete(cart);
        return order;
    }

    @Override
    public Order updateOrder(Admin admin, Cart cart) {
        if (admin == null) {
            throw new RuntimeException("Admin not found");
        }

        Order order = orderRepository.findByAdmin(admin);
        if (order == null) {
            throw new RuntimeException("Order not found for admin");
        }

        List<OrderDetail> orderDetails = order.getOrderDetails() != null ? order.getOrderDetails() : new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            ServicePack servicePack = cartItem.getServices();
            OrderDetail orderDetail = orderDetails.stream()
                    .filter(detail -> detail.getServices().getId().equals(servicePack.getId()))
                    .findFirst()
                    .orElse(null);
            if (orderDetail == null) {
                orderDetail = new OrderDetail();
                orderDetail.setOrders(order);
                orderDetail.setServices(cartItem.getServices());
                orderDetail.setPrice(servicePack.getPrice());
                orderDetail.setQuantity(cartItem.getQuantity());
                orderDetail.setTotalAmounts(cartItem.getTotalPrice());
                orderDetail.setTotalValidityPeriod(cartItem.getTotalValidityPeriod());
                orderDetails.add(orderDetail);
            } else {
                orderDetail.setQuantity(orderDetail.getQuantity() + cartItem.getQuantity());
                orderDetail.setTotalAmounts(orderDetail.getTotalAmounts() + cartItem.getTotalPrice());
            }
            servicePack.setQuantity(servicePack.getQuantity() - cartItem.getQuantity());
            servicePackRepository.save(servicePack);
        }

        order.setTotalAmounts(cart.getTotalAmounts() * 0.08);
        order.setTotalValidityPeriod(cart.getTotalValidityPeriod());
        order.setOrderDetails(orderDetails);
        orderRepository.save(order);
        cartItemRepository.deleteAll(cart.getCartItems());
        cartRepository.delete(cart);

        return order;
    }

    @Override
    public Order getOrderByEmployer(Admin admin) {
        if (admin == null) {
            throw new RuntimeException("Admin not found");
        }
        return orderRepository.findByAdmin(admin);
    }

    @Override
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        orderDetailRepository.deleteAll(order.getOrderDetails());
        orderRepository.delete(order);
    }
    @Override
    public Order findByEmployer(Admin admin) {
        return this.orderRepository.findByAdmin(admin);
    }

    @Override
    public List<OrderDetail> getAllOrderDetailByOrder(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

}
