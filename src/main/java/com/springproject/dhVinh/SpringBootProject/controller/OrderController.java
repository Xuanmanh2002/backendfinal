package com.springproject.dhVinh.SpringBootProject.controller;

import com.springproject.dhVinh.SpringBootProject.exception.PhotoRetrievalException;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Cart;
import com.springproject.dhVinh.SpringBootProject.model.Order;
import com.springproject.dhVinh.SpringBootProject.model.OrderDetail;
import com.springproject.dhVinh.SpringBootProject.repository.CartRepository;
import com.springproject.dhVinh.SpringBootProject.response.CartResponse;
import com.springproject.dhVinh.SpringBootProject.response.EmployerResponse;
import com.springproject.dhVinh.SpringBootProject.response.OrderDetailResponse;
import com.springproject.dhVinh.SpringBootProject.response.OrderResponse;
import com.springproject.dhVinh.SpringBootProject.security.payment.VNPayConfig;
import com.springproject.dhVinh.SpringBootProject.service.ICartService;
import com.springproject.dhVinh.SpringBootProject.service.IEmployerService;
import com.springproject.dhVinh.SpringBootProject.service.IOrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final IOrderService orderService;

    private final IEmployerService employerService;

    private final ICartService cartService;

    private final CartRepository cartRepository;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createOrderAndUpdateService(
            @RequestParam Long cartId,
            Principal principal,
            HttpServletRequest request) {
        try {
            String email = principal.getName();
            Admin admin = employerService.getEmployer(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Admin not found for email: " + email);
            }
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new RuntimeException("Cart not found for ID: " + cartId));
            Order order = orderService.createOrderAndUpdateService(admin, cartId);
            String bankCode = "NCB";
            String vnp_IpAddr = VNPayConfig.getIpAddress(request);
            Double amount = cart.getTotalAmounts();
            String paymentUrl = createPayment(amount, bankCode, vnp_IpAddr);

            Map<String, Object> response = new HashMap<>();
            response.put("order", order);
            response.put("paymentUrl", paymentUrl);

            return ResponseEntity.ok(response);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Encoding error: " + ex.getMessage());
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest().body("Error occurred: " + ex.getMessage());
        }
    }

    public String createPayment(Double amount, String bankCode, String vnp_IpAddr) throws UnsupportedEncodingException {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";

        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        Long amountInVND = Math.round(amount * 100);

        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amountInVND));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;
        return paymentUrl;
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

    @DeleteMapping("/delete/{orderId}")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok("Order deleted successfully");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<Order> orders = orderService.getAllOrder();
        List<OrderResponse> orderResponses = orders.stream().map(order -> {
            EmployerResponse employerResponse = getEmployerResponse(order.getAdmins());
            return new OrderResponse(
                    order.getId(),
                    order.getOrderDate(),
                    order.getOrderStatus(),
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
                        orderDetail.getTotalBenefit(),
                        orderDetail.getActivationDate(),
                        orderDetail.getStatus(),
                        orderDetail.getServices().getId(),
                        orderDetail.getOrders().getId()
                ))
                .toList();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/total-amounts")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Long> getTotalAmounts() {
        try {
            long totalAmounts = orderService.countTotalAmounts();
            return ResponseEntity.ok(totalAmounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/order-details")
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<?> getOrderDetailsByAdmin(Principal principal) {
        String email = principal.getName();
        Admin admin = employerService.getEmployer(email);
        List<OrderDetail> orderDetails = orderService.getAllOrderDetailByAdmin(admin.getId());
        List<OrderDetailResponse> response = orderDetails.stream()
                .map(orderDetail -> new OrderDetailResponse(
                        orderDetail.getId(),
                        orderDetail.getQuantity(),
                        orderDetail.getPrice(),
                        orderDetail.getTotalAmounts(),
                        orderDetail.getTotalValidityPeriod(),
                        orderDetail.getTotalBenefit(),
                        orderDetail.getActivationDate(),
                        orderDetail.getStatus(),
                        orderDetail.getServices().getId(),
                        orderDetail.getOrders().getId()
                ))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order-by-employer")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<OrderResponse> getOrderByEmployer(Principal principal) {
        String email = principal.getName();
        Admin admin = employerService.getEmployer(email);
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Order order = orderService.getOrderByEmployer(admin);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        EmployerResponse employerResponse = getEmployerResponse(admin);
        Long totalItems = (long) order.getOrderDetails().size();
        Double totalAmounts = order.getOrderDetails()
                .stream()
                .mapToDouble(detail -> detail.getServices().getPrice())
                .sum();
        Long totalValidityPeriod = order.getOrderDetails()
                .stream()
                .mapToLong(detail -> detail.getServices().getValidityPeriod())
                .sum();
        OrderResponse orderResponse = new OrderResponse(
                order.getId(),
                order.getOrderDate(),
                order.getOrderStatus(),
                order.getTotalAmounts(),
                order.getTotalValidityPeriod(),
                employerResponse
        );
        return ResponseEntity.ok(orderResponse);
    }

    @DeleteMapping("/delete-order-details")
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<?> deleteOrderDetail(@RequestParam Long serviceId, Principal principal) {
        String email = principal.getName();
        Admin admin = employerService.getEmployer(email);
        orderService.deleteOrderDetailToOrder(serviceId, admin);
        return ResponseEntity.ok("Chi tiết đơn hàng đã được xóa thành công.");
    }

    @PutMapping("/update-status/{orderId}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestParam String orderStatus) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(orderId, orderStatus);
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update order status");
        }
    }

    @GetMapping("/total-amounts-by-month")
    public Map<Integer, Double> getTotalAmountsByMonth() {
        return orderService.calculateTotalAmountsByMonth();
    }

    @GetMapping("/total-amounts-by-quarter/{year}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<Integer, Double>> getTotalAmountsByQuarter(@PathVariable int year) {
        Map<Integer, Double> quarterAmounts = orderService.calculateTotalAmountsByQuarter(year);
        return ResponseEntity.ok(quarterAmounts);
    }

    @GetMapping("/total-amounts-by-year")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<Integer, Double>> getTotalAmountsByYear() {
        Map<Integer, Double> yearAmounts = orderService.calculateTotalAmountsByYear();
        return ResponseEntity.ok(yearAmounts);
    }

    @GetMapping("/top-spending-employer")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<EmployerResponse> getTopSpendingEmployer() {
        Admin topEmployer = orderService.getTopSpendingEmployer();
        if (topEmployer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        EmployerResponse employerResponse = getEmployerResponse(topEmployer);
        return ResponseEntity.ok(employerResponse);
    }

    @GetMapping("/total-amounts-by-employer")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<EmployerResponse, Double>> getTotalAmountsByEmployer() {
        Map<Admin, Double> employerAmounts = orderService.calculateTotalAmountsByEmployer();
        Map<EmployerResponse, Double> response = employerAmounts.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> getEmployerResponse(entry.getKey()),
                        Map.Entry::getValue
                ));
        return ResponseEntity.ok(response);
    }
}
