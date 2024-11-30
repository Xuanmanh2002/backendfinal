package com.springproject.dhVinh.SpringBootProject.repository;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    boolean existsByEmail(String email);

    void deleteByEmail(String email);

    Optional<Admin> findByEmail(String email);

    @Query("SELECT a FROM Admin a JOIN a.roles r WHERE r.name = 'ROLE_ADMIN'")
    List<Admin> findByRoleAdmin();

    @Query("SELECT a FROM Admin a JOIN a.roles r WHERE r.name = 'ROLE_EMPLOYER'")
    List<Admin> findByRoleEmployer();

    @Query("SELECT a FROM Admin a JOIN a.roles r WHERE r.name = 'ROLE_CUSTOMER'")
    List<Admin> findByRoleCustomer();

    @Query("SELECT COUNT(a) FROM Admin a JOIN a.roles r WHERE r.name = 'ROLE_EMPLOYER'")
    Long countByRoleEmployer();

    @Query("SELECT COUNT(a) FROM Admin a JOIN a.roles r WHERE r.name = 'ROLE_CUSTOMER'")
    Long countByRoleCustomer();

    @Query("SELECT a FROM Admin a WHERE a.rank IN ('silver', 'gold', 'diamond')")
    List<Admin> findByRank();
}
