package com.springproject.dhVinh.SpringBootProject.repository;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

   @Query("SELECT j FROM Job j WHERE j.admins.id = :adminId")
   List<Job> findByAdminId(@Param("adminId") Long adminId);

    List<Job> findByAdmins(Admin admin);

    List<Job> findByStatusTrue();
    List<Job> findByStatusFalse();

    @Query("SELECT j FROM Job j WHERE j.admins.rank IN (:rank)")
    List<Job> findJobsByAdminRank(@Param("rank") List<String> rank);


}
