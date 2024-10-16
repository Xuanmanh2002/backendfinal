package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Role;

import java.util.List;

public interface IRoleService {
    List<Role> getRoles();

    Role createRole(String name, String description);

    void deleteRole(Long roleId);

    Role findByName(String name);

    Admin removeAdminsFromRole(Long adminId, Long roleId);

    Admin assignRoleToAdmin(Long adminId, Long roleId);

    Role removeAllAdminsFromRole(Long roleId);
}
