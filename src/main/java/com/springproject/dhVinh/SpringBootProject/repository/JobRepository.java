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

    @Query("SELECT j FROM Job j WHERE j.admins.rank IN (:rank)")
    List<Job> findJobsByAdminRankDiamond(@Param("rank") List<String> rank);


    @Query("SELECT j FROM Job j WHERE j.admins.id = :adminId AND j.status = true")
    List<Job> findByAdminIdAndStatusTrue(@Param("adminId") Long adminId);

    @Query("SELECT COUNT(j) FROM Job j WHERE j.admins.id = :adminId AND j.status = true")
    int countActiveJobsByAdminId(@Param("adminId") Long adminId);

    @Query("SELECT COUNT(j) FROM Job j WHERE j.categories.id = :categoryId AND j.status = true")
    int countActiveJobsByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT j FROM Job j WHERE j.categories.id = :categoryId AND j.status = true")
    List<Job> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT j FROM Job j " +
            "JOIN FETCH j.admins a " +
            "JOIN FETCH a.address ad " +
            "WHERE j.status = true AND ad.id = :addressId")
    List<Job> findJobsByAddressId(@Param("addressId") Long addressId);

    @Query("SELECT j FROM Job j " +
            "JOIN FETCH j.categories c " +
            "JOIN FETCH j.admins a " +
            "JOIN FETCH a.address ad " +
            "WHERE c.id = :categoryId AND ad.id = :addressId AND j.status = true")
    List<Job> findByCategoryIdAndAddressId(@Param("categoryId") Long categoryId,
                                           @Param("addressId") Long addressId);

    @Query("SELECT j FROM Job j WHERE LOWER(j.jobName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(j.recruitmentDetails) LIKE LOWER(CONCAT('%', '%', :keyword , '%', '%'))")
    List<Job> searchByKeyword(String keyword);
}
