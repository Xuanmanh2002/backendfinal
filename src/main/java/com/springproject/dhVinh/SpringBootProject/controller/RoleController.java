package com.springproject.dhVinh.SpringBootProject.controller;

import com.springproject.dhVinh.SpringBootProject.exception.RoleAlreadyExistException;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Role;
import com.springproject.dhVinh.SpringBootProject.service.IRoleService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/roles")
public class RoleController {
    private final IRoleService roleService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getRoles();
        if (roles.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> createRole(@RequestBody Map<String, String> roleData) {
        try {
            String name = roleData.get("name");
            String description = roleData.get("description");
            roleService.createRole(name, description);
            return ResponseEntity.ok("New role created successfully!");
        } catch (RoleAlreadyExistException re) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(re.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the role.");
        }
    }


    @DeleteMapping("/delete/{roleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteRole(@PathVariable("roleId") long roleId){
        roleService.deleteRole(roleId);
    }

    @DeleteMapping("/remove-all-admins")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Role removeAllAdminsFromRole(@PathVariable("roleId") long roleId){
        return roleService.removeAllAdminsFromRole(roleId);
    }

    @PostMapping("/assign-admin-to-role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Admin removeAdminFromRole(@RequestParam("adminId") long adminId,
                                     @RequestParam("roleId") long roleId) {
        return roleService.assignRoleToAdmin(adminId, roleId);
    }
}
