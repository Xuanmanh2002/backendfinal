package com.springproject.dhVinh.SpringBootProject.controller;

import com.springproject.dhVinh.SpringBootProject.exception.CategoryAlreadyExistsException;
import com.springproject.dhVinh.SpringBootProject.exception.RoleAlreadyExistException;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Role;
import com.springproject.dhVinh.SpringBootProject.response.RoleResponse;
import com.springproject.dhVinh.SpringBootProject.service.IRoleService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    public ResponseEntity<Map<String, Object>>createRole(@RequestBody RoleResponse response) {
        Map<String, Object> result = new HashMap<>();
        try{
            Role role = roleService.createRole(response.getName(), response.getDescription());
            result.put("status", "success");
            result.put("message", "Role created successfully");
            result.put("role", role);
            return ResponseEntity.ok(result);
        } catch (CategoryAlreadyExistsException cae) {
            result.put("status", "error");
            result.put("message", cae.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
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
