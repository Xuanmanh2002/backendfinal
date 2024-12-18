package com.springproject.dhVinh.SpringBootProject.repository;

import com.springproject.dhVinh.SpringBootProject.model.ApplicationDocuments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationDocumentsRepository extends JpaRepository<ApplicationDocuments, Long> {

    @Query("SELECT ad FROM ApplicationDocuments ad " +
            "JOIN ad.jobs j " +
            "JOIN j.admins a " +
            "WHERE a.id = :adminId")
    List<ApplicationDocuments> getAllJobByAdmin(@Param("adminId") Long adminId);

    @Query("SELECT ad FROM ApplicationDocuments ad " +
            "JOIN FETCH ad.jobs j " +
            "JOIN FETCH j.admins e " +
            "WHERE ad.admins.id = :adminId")
    List<ApplicationDocuments> findByAdmin(Long adminId);

    @Query("SELECT ad FROM ApplicationDocuments ad " +
            "JOIN ad.jobs j " +
            "JOIN j.admins a " +
            "WHERE ad.status = :status AND a.id = :adminId")
    List<ApplicationDocuments> findByStatusAndAdminId(@Param("status") String status, @Param("adminId") Long adminId);
}
