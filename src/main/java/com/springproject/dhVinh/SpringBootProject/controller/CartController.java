package com.springproject.dhVinh.SpringBootProject.controller;

import com.springproject.dhVinh.SpringBootProject.exception.PhotoRetrievalException;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Cart;
import com.springproject.dhVinh.SpringBootProject.model.CartItem;
import com.springproject.dhVinh.SpringBootProject.response.CartItemResponse;
import com.springproject.dhVinh.SpringBootProject.response.CartResponse;
import com.springproject.dhVinh.SpringBootProject.response.EmployerResponse;
import com.springproject.dhVinh.SpringBootProject.response.ServicePackResponse;
import com.springproject.dhVinh.SpringBootProject.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
@RequestMapping("/api/cart/")
public class CartController {

    private final IEmployerService employerService;

    private final ICartItemService cartItemService;

    private final ICartService cartService;

    private final IServicePackService service;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createItemToCart(
            @RequestParam Long serviceId,
            @RequestParam Long quantity,
            Principal principal,
            HttpSession httpSession
    ) {
        try {
            String email = principal.getName();
            Admin admin = employerService.getEmployer(email);
            Cart cart = cartService.createItemToCart(serviceId, quantity, admin);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateItemToCart(
            @RequestParam Long serviceId,
            @RequestParam Long quantity,
            Principal principal,
            HttpSession httpSession
    ) {
        try {
            String email = principal.getName();
            Admin admin = employerService.getEmployer(email);
            Cart cart = cartService.updateItemToCart(serviceId, quantity, admin);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteItemToCart(
            @RequestParam Long serviceId,
            Principal principal,
            HttpSession httpSession
    ) {
        try {
            String email = principal.getName();
            Admin admin = employerService.getEmployer(email);
            cartService.deleteItemToCart(serviceId, admin);
            return ResponseEntity.ok("Item deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all-item")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllItemsInCart(Principal principal, HttpSession httpSession) {
        try {
            String email = principal.getName();
            Admin admin = employerService.getEmployer(email);
            List<CartItem> cartItems = cartItemService.getAllCartItemByCustomer(admin.getId());
            List<CartItemResponse> cartItemResponses = cartItems.stream().map(cartItem ->
                    new CartItemResponse(
                            cartItem.getId(),
                            cartItem.getTotalPrice(),
                            cartItem.getQuantity(),
                            cartItem.getTotalValidityPeriod(),
                            cartItem.getCarts().getId(),
                            cartItem.getServices().getId()
                    )
            ).collect(Collectors.toList());

            return ResponseEntity.ok(cartItemResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/cart-by-employer")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<CartResponse> getCartByEmployer(Principal principal) {
        String email = principal.getName();
        Admin admin = employerService.getEmployer(email);
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Cart cart = cartService.getCartByEmployer(admin);
        if (cart == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        EmployerResponse employerResponse = getEmployerResponse(admin);
        Long totalItems = (long) cart.getCartItems().size();
        Double totalAmounts = cart.getCartItems()
                .stream()
                .mapToDouble(item -> item.getServices().getPrice())
                .sum();
        Long totalValidityPeriod = cart.getCartItems()
                .stream()
                .mapToLong(item -> item.getServices().getValidityPeriod())
                .sum();
        CartResponse cartResponse = new CartResponse(
                cart.getId(),
                totalAmounts,
                totalItems,
                totalValidityPeriod,
                employerResponse
        );
        return ResponseEntity.ok(cartResponse);
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

        Blob photoBlob = admin.getAvatar();
        if (photoBlob != null) {
            try {
                byte[] photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
                String avatarBase64 = Base64.getEncoder().encodeToString(photoBytes);
                employerResponse.setAvatar(avatarBase64);
            } catch (SQLException e) {
                throw new PhotoRetrievalException("Error retrieving photo");
            }
        }

        return employerResponse;
    }

}
