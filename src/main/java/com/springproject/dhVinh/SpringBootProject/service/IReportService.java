package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Report;

import java.util.List;

public interface IReportService {
    Report addReport(Admin admin, String fullname, String email, String telephone, String letter, String address,Long jobId);

    void cancleReport(Long reportId);

    List<Report> getAllReport();

    Report updateStatus(Long reportId, Boolean status);
}
