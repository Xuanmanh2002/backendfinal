package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.ApplicationDocuments;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IApplicationDocumentsService {
    ApplicationDocuments addAD(Admin admin, String fullname, String email, String telephone, MultipartFile cv, String letter, Long jobId) throws SQLException, IOException;

    void cancleAD(Long applicationDocumentsId);

    List<ApplicationDocuments> getAllApplicationDocuments();

    ApplicationDocuments updateAD(Admin admin, Long applicationDocumentsId, String fullname, String email, String telephone, String cv, String letter, Long jobId);

    List<ApplicationDocuments> getAllJobByAdmin(Long adminId);

    List<ApplicationDocuments> getAllJobByCustomer(Long adminId);

    ApplicationDocuments updateStatus(Long applicationDocumentsId, String status);

    List<ApplicationDocuments> getApplicationDocumentsByStatus(String status,  Long adminId);
}
