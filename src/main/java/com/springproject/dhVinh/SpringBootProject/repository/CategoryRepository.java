package com.springproject.dhVinh.SpringBootProject.repository;

import com.springproject.dhVinh.SpringBootProject.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCategoryName(String categoryName);

    boolean existsByCategoryName(String categoryName);

    @Query("SELECT c FROM Category c JOIN c.jobs j GROUP BY c HAVING COUNT(j) > 1")
    List<Category> findCategoriesWithJobs();
}
