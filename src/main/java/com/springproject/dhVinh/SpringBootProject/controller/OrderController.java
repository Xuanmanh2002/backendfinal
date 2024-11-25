package com.springproject.dhVinh.SpringBootProject.controller;
import com.springproject.dhVinh.SpringBootProject.exception.PhotoRetrievalException;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Cart;
import com.springproject.dhVinh.SpringBootProject.model.Order;
import com.springproject.dhVinh.SpringBootProject.model.OrderDetail;
import com.springproject.dhVinh.SpringBootProject.response.EmployerResponse;
import com.springproject.dhVinh.SpringBootProject.response.OrderDetailResponse;
import com.springproject.dhVinh.SpringBootProject.response.OrderResponse;
import com.springproject.dhVinh.SpringBootProject.service.ICartService;
import com.springproject.dhVinh.SpringBootProject.service.IEmployerService;
import com.springproject.dhVinh.SpringBootProject.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final IOrderService orderService;

    private final IEmployerService employerService;

    private final ICartService cartService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createOrderAndUpdateService(
            @RequestParam Cart cart,
            Principal principal) {
        try {
            String email = principal.getName();
            Admin admin = employerService.getEmployer(email);

            if (admin == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Admin not found for email: " + email);
            }
            Order order = orderService.createOrderAndUpdateService(admin, cart);
            return ResponseEntity.ok(order);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateOrder(@RequestParam Cart cart, Principal principal) {
        try {
            String email = principal.getName();
            Admin admin = employerService.getEmployer(email);

            if (admin == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Admin not found for email: " + email);
            }
            Order updatedOrder = orderService.updateOrder(admin, cart);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/get-by-employer")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getOrderByEmployer(Principal principal) {
        try {
            String email = principal.getName();
            Admin admin = employerService.getEmployer(email);

            if (admin == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Admin not found for email: " + email);
            }

            Order order = orderService.getOrderByEmployer(admin);
            return ResponseEntity.ok(order);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/delete/{orderId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok("Order deleted successfully");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<Order> orders = orderService.getAllOrder();
        if (orders.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        List<OrderResponse> orderResponses = orders.stream().map(order -> {
            EmployerResponse employerResponse = getEmployerResponse(order.getAdmins());
            return new OrderResponse(
                order.getId(),
                order.getOrderDate(),
                order.getTotalAmounts(),
                order.getTotalValidityPeriod(),
                employerResponse
            );
        }).collect(Collectors.toList());
        return ResponseEntity.ok(orderResponses);
    }

    private EmployerResponse getEmployerResponse(Admin admin) {
        EmployerResponse employerResponse = new EmployerResponse();
        employerResponse.setId(admin.getId());
        employerResponse.setEmail(admin.getEmail());
        employerResponse.setFirstName(admin.getFirstName());
        employerResponse.setLastName(admin.getLastName());
        employerResponse.setBirthDate(admin.getBirthDate());
        employerResponse.setGender(admin.getGender());
        employerResponse.setTelephone(admin.getTelephone());
        employerResponse.setAddressId(admin.getAddress().getId());
        employerResponse.setCompanyName(admin.getCompanyName());

        byte[] photoBytes = null;
        Blob photoBlob = admin.getAvatar();
        if (photoBlob != null) {
            try {
                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
                String avatarBase64 = Base64.getEncoder().encodeToString(photoBytes);
                employerResponse.setAvatar(avatarBase64);
            } catch (SQLException e) {
                throw new PhotoRetrievalException("Error retrieving photo");
            }
        }

        return employerResponse;
    }

    @GetMapping("/get-employer")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getEmployerDetails(Principal principal) {
        try {
            String email = principal.getName();
            Admin admin = employerService.getEmployer(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found");
            }
            return ResponseEntity.ok(admin);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/all-order-detail/{orderId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<OrderDetailResponse>> getAllOrderDetail(@PathVariable Long orderId) {
        List<OrderDetail> details = orderService.getAllOrderDetailByOrder(orderId);
        List<OrderDetailResponse> responseList = details.stream()
                .map(orderDetail -> new OrderDetailResponse(
                        orderDetail.getId(),
                        orderDetail.getQuantity(),
                        orderDetail.getPrice(),
                        orderDetail.getTotalAmounts(),
                        orderDetail.getTotalValidityPeriod(),
                        orderDetail.getServices().getId(),
                        orderDetail.getOrders().getId()
                ))
                .toList();
        return ResponseEntity.ok(responseList);
    }
}
