package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.exception.AdminAlreadyExistsException;
import com.springproject.dhVinh.SpringBootProject.exception.RoleAlreadyExistException;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Role;
import com.springproject.dhVinh.SpringBootProject.repository.AdminRepository;
import com.springproject.dhVinh.SpringBootProject.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {

    private final RoleRepository roleRepository;
    private final AdminRepository adminRepository;

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role createRole(String name, String description) {
        String roleName = "ROLE_" + name.toUpperCase();
        Role newRole = new Role(roleName);
        LocalDate createdDate = LocalDate.now();
        newRole.setCreateAt(createdDate);
        newRole.setDescription(description);
        if (roleRepository.existsByName(roleName)) {
            throw new RoleAlreadyExistException(name + " role already exists");
        }
        return roleRepository.save(newRole);
    }


    @Override
    public void deleteRole(Long roleId) {
        this.removeAllAdminsFromRole(roleId);
        roleRepository.deleteById(roleId);
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name).get();
    }

    @Override
    public Admin removeAdminsFromRole(Long adminId, Long roleId) {
        Optional<Admin> admins = adminRepository.findById(roleId);
        Optional<Role> role = roleRepository.findById(roleId);
        if (role.isPresent() && role.get().getAdmins().contains(admins.get())) {
            role.get().removeAdminsToRole(admins.get());
            roleRepository.save(role.get());
            return admins.get();
        }
        throw new UsernameNotFoundException("Admin not found");
    }

    @Override
    public Admin assignRoleToAdmin(Long adminId, Long roleId) {
        Optional<Admin> admins = adminRepository.findById(adminId);
        Optional<Role> role = roleRepository.findById(roleId);
        if (admins.isPresent() && admins.get().getRoles().contains(role.get())) {
            throw new AdminAlreadyExistsException(
                    admins.get().getFirstName() + " has been assigned to" + role.get().getName() + " role");
        }
        if (role.isPresent()) {
            role.get().assignRoleToAdmin(admins.get());
            roleRepository.save(role.get());
        }
        return admins.get();
    }

    @Override
    public Role removeAllAdminsFromRole(Long roleId) {
        Optional<Role> role = roleRepository.findById(roleId);
        role.ifPresent(Role::removeAllAdminsFromRole);
        return roleRepository.save(role.get());
    }

}