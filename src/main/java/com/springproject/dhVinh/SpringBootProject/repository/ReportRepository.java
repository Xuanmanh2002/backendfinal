package com.springproject.dhVinh.SpringBootProject.repository;

import com.springproject.dhVinh.SpringBootProject.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
