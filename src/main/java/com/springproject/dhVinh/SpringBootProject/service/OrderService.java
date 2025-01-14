package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.*;
import com.springproject.dhVinh.SpringBootProject.repository.*;
import com.springproject.dhVinh.SpringBootProject.response.CartResponse;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{
    private final OrderRepository orderRepository;

    private final OrderDetailRepository orderDetailRepository;

    private final ServicePackRepository servicePackRepository;

    private final AdminRepository employerRepository;

    private final CartItemRepository cartItemRepository;

    private final CartRepository cartRepository;

    private final JobRepository jobRepository;

    private final NotificationRepository notificationRepository;

    @Override
    public List<Order> getAllOrder() {
        return this.orderRepository.findAll();
    }

    @Override
    public Order createOrderAndUpdateService(Admin admin, Long cartId) {
        if (admin == null) {
            throw new RuntimeException("Admin not found");
        }
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        Order order = orderRepository.findByAdmin(admin);
        if (order == null) {
            order = new Order();
            order.setAdmins(admin);
            order.setOrderDate(LocalDate.now());
            order.setTotalAmounts(cart.getTotalAmounts() + (cart.getTotalAmounts() * 0.08));
            order.setTotalValidityPeriod(cart.getTotalValidityPeriod());
            order.setOrderStatus("Chờ thanh toán");
            order.setOrderDetails(new ArrayList<>());
            order.setTotalBenefit(cart.getTotalBenefit());
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
                orderDetail.setTotalBenefit(cartItem.getTotalBenefit());
                orderDetails.add(orderDetail);
            } else {
                orderDetail.setQuantity(orderDetail.getQuantity() + cartItem.getQuantity());
                orderDetail.setTotalAmounts(orderDetail.getTotalAmounts() + cartItem.getTotalPrice());
            }
        }
        double totalAmounts = cart.getTotalAmounts() + (cart.getTotalAmounts() * 0.08);
        order.setTotalAmounts(totalAmounts);
        order.setOrderDetails(orderDetails);
        orderRepository.save(order);
        cartItemRepository.deleteAll(cart.getCartItems());
        cartRepository.delete(cart);
        if ("Thanh toán thành công".equals(order.getOrderStatus())) {
            Notification notification = new Notification();
            notification.setTitle(admin.getFirstName() + " đã mua hàng");
            notification.setStatus(false);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setAdmins(admin);
            notificationRepository.save(notification);
        }
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

    @Override
    public long countTotalAmounts() {
        return orderRepository.countTotalAmounts();
    }

    @Override
    public List<OrderDetail> getAllOrderDetailByAdmin(Long adminId) {
        return orderDetailRepository.findByAdmin(adminId);
    }

    @Override
    public void deleteOrderDetailToOrder(Long serviceId, Admin admin) {
        if (admin == null) {
            throw new RuntimeException("Admin not found");
        }
        Order order = orderRepository.findByAdmin(admin);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        OrderDetail orderDetail = order.getOrderDetails()
                .stream()
                .filter(detail -> detail.getServices().getId().equals(serviceId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("OrderDetail not found"));
        order.getOrderDetails().remove(orderDetail);
        orderDetailRepository.delete(orderDetail);
        if (order.getOrderDetails().isEmpty()) {
            orderRepository.delete(order);
        } else {
            double totalPrice = orderDetailRepository.sumTotalAmountsByOrder(order.getId());
            long totalValidityPeriod = orderDetailRepository.sumValidityPeriodByOrder(order.getId());
            order.setTotalValidityPeriod(totalValidityPeriod);
            order.setTotalAmounts(totalPrice);
            orderRepository.save(order);
        }
    }

    @Override
    public Order updateOrderStatus(Long orderId, String orderStatus) {
        if (orderId == null || orderStatus == null || orderStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID hoặc trạng thái không hợp lệ.");
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order không tồn tại với ID: " + orderId));
        order.setOrderStatus(orderStatus);
        if ("Thanh toán thành công".equals(orderStatus)) {
            Admin admin = order.getAdmins();
            if (admin == null) {
                throw new RuntimeException("Không tìm thấy Admin liên quan đến Order.");
            }
            List<Job> jobs = jobRepository.findByAdmins(admin);
            long activatedJobsCount = jobs.stream()
                    .filter(Job::getStatus)
                    .count();
            long jobsToActivate = order.getTotalBenefit() - activatedJobsCount;
            for (Job job : jobs) {
                if (!job.getStatus() && jobsToActivate > 0) {
                    job.setStatus(true);
                    job.setActivationDate(LocalDate.now());
                    job.setTotalValidityPeriod(order.getTotalValidityPeriod());
                    jobRepository.save(job);
                    jobsToActivate--;
                }
            }
            for (OrderDetail orderDetail : order.getOrderDetails()) {
                orderDetail.setActivationDate(LocalDate.now());
                orderDetail.setStatus(true);
                orderDetailRepository.save(orderDetail);
            }
            Notification notification = new Notification();
            notification.setTitle(admin.getFirstName() + " " + admin.getLastName() + " đã mua hàng thành công");
            notification.setStatus(false);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setAdmins(admin);
            notificationRepository.save(notification);
            double totalAmounts = order.getTotalAmounts();
            if (totalAmounts >= 20000000) {
                admin.setRank("diamond");
            } else if (totalAmounts >= 10000000) {
                admin.setRank("gold");
            } else if (totalAmounts >= 2000000) {
                admin.setRank("silver");
            } else {
                admin.setRank("default");
            }
            employerRepository.save(admin);
        }

        orderRepository.save(order);
        return order;
    }

    @Override
    public Map<Integer, Double> calculateTotalAmountsByMonth() {
        Map<Integer, Double> totalAmountsByMonth = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            totalAmountsByMonth.put(i, 0.0);
        }
        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            int month = order.getOrderDate().getMonthValue();
            double totalAmount = order.getTotalAmounts();
            totalAmountsByMonth.put(month, totalAmountsByMonth.get(month) + totalAmount);
        }
        return totalAmountsByMonth;
    }

    @Override
    public Map<Integer, Double> calculateTotalAmountsByQuarter(int year) {
        List<Order> orders = orderRepository.findAll();
        Map<Integer, Double> quarterAmounts = new HashMap<>();

        for (Order order : orders) {
            if (order.getOrderDate().getYear() + 1900 == year) {
                int month = order.getOrderDate().getMonthValue();
                int quarter = (month - 1) / 3 + 1;
                double amount = order.getTotalAmounts();

                quarterAmounts.put(quarter, quarterAmounts.getOrDefault(quarter, 0.0) + amount);
            }
        }

        return quarterAmounts;
    }

    @Override
    public Map<Integer, Double> calculateTotalAmountsByYear() {
        List<Order> orders = orderRepository.findAll();
        Map<Integer, Double> yearAmounts = new HashMap<>();
        for (Order order : orders) {
            int year = order.getOrderDate().getYear() + 1900;
            double amount = order.getTotalAmounts();

            yearAmounts.put(year, yearAmounts.getOrDefault(year, 0.0) + amount);
        }
        return yearAmounts;
    }

    @Override
    public Admin getTopSpendingEmployer() {
        List<Order> orders = orderRepository.findAll();
        Map<Admin, Double> employerSpending = new HashMap<>();

        for (Order order : orders) {
            Admin employer = order.getAdmins();
            double amount = order.getTotalAmounts();

            employerSpending.put(employer, employerSpending.getOrDefault(employer, 0.0) + amount);
        }
        return employerSpending.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    @Override
    public Map<Admin, Double> calculateTotalAmountsByEmployer() {
        List<Order> orders = orderRepository.findAll();
        Map<Admin, Double> employerAmounts = new HashMap<>();

        for (Order order : orders) {
            Admin employer = order.getAdmins();
            double amount = order.getTotalAmounts();

            employerAmounts.put(employer, employerAmounts.getOrDefault(employer, 0.0) + amount);
        }
        return employerAmounts;
    }


}
