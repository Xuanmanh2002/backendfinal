package com.springproject.dhVinh.SpringBootProject.controller;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Notification;
import com.springproject.dhVinh.SpringBootProject.service.IAdminService;
import com.springproject.dhVinh.SpringBootProject.service.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final INotificationService notificationService;

    private final IAdminService adminService;

    @GetMapping("/by-role")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_CUSTOMER')")
    public List<Notification> getNotificationsByRole(Principal principal) {
        String email = principal.getName();
        Admin admin = adminService.getAdmin(email);
        return notificationService.findNotificationsByRole(admin);
    }
}
