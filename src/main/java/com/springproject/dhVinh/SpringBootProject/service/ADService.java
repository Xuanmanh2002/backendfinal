package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.exception.ApplicationAlreadyExistsException;
import com.springproject.dhVinh.SpringBootProject.exception.JobAlreadyExistException;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.ApplicationDocuments;
import com.springproject.dhVinh.SpringBootProject.model.Job;
import com.springproject.dhVinh.SpringBootProject.repository.ApplicationDocumentsRepository;
import com.springproject.dhVinh.SpringBootProject.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ADService implements IADService {

    private final ApplicationDocumentsRepository applicationDocumentsRepository;

    private final IAdminService adminService;
    private final JobRepository jobRepository;

    @Override
    public ApplicationDocuments addAD(Admin admin, String fullname, String email, String telephone, MultipartFile cv, String letter, Long jobId) throws SQLException, IOException {
        ApplicationDocuments applicationDocuments = new ApplicationDocuments();

        if (admin == null){
            throw new RuntimeException("Admin not fount");
        }

        Optional<Job> optionalJob = jobRepository.findById(jobId);
        if(!optionalJob.isPresent()) {
            throw new JobAlreadyExistException("job not found");
        }

        if (!cv.isEmpty()){
            byte[] photoBytes = cv.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            applicationDocuments.setCv(photoBlob);
        }

        Job job = optionalJob.get();
        applicationDocuments.setAdmins(admin);
        applicationDocuments.setFullName(fullname);
        applicationDocuments.setEmail(email);
        applicationDocuments.setTelephone(telephone);
        applicationDocuments.setLetter(letter);
        applicationDocuments.setStatus("CV tiếp nhận");
        applicationDocuments.setJobs(job);
        return applicationDocumentsRepository.save(applicationDocuments);

    }

    @Override
    public void cancleAD(Long applicationDocumentsId) {
        Optional<ApplicationDocuments> applicationDocuments = applicationDocumentsRepository.findById(applicationDocumentsId);
        if (applicationDocuments.isPresent()){
            applicationDocumentsRepository.deleteById(applicationDocumentsId);
        } else {
            throw new ApplicationAlreadyExistsException("Application Documents with ID " + applicationDocumentsId+ " not found");
        }
    }

    @Override
    public List<ApplicationDocuments> getAllApplicationDocuments() {
        return List.of();
    }

    @Override
    public ApplicationDocuments updateAD(Admin admin, Long applicationDocumentsId, String fullname, String email, String telephone, String cv, String letter, Long jobId) {
        return null;
    }

    @Override
    public List<ApplicationDocuments> getAllApplicationDocumentByEmployerID(Long adminId) {
        return List.of();
    }
}
