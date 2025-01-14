package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.exception.ApplicationAlreadyExistsException;
import com.springproject.dhVinh.SpringBootProject.exception.JobAlreadyExistException;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.ApplicationDocuments;
import com.springproject.dhVinh.SpringBootProject.model.Job;
import com.springproject.dhVinh.SpringBootProject.model.Notification;
import com.springproject.dhVinh.SpringBootProject.repository.ApplicationDocumentsRepository;
import com.springproject.dhVinh.SpringBootProject.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationDocumentsService implements IApplicationDocumentsService {

    private final ApplicationDocumentsRepository applicationDocumentsRepository;
    private final IAdminService adminService;
    private final JobRepository jobRepository;
    private final JavaMailSender mailSender;

    @Override
    public ApplicationDocuments addAD(Admin admin, String fullname, String email, String telephone, MultipartFile cv, String letter, Long jobId) throws SQLException, IOException {
        ApplicationDocuments applicationDocuments = new ApplicationDocuments();

        if (admin == null) {
            throw new RuntimeException("Admin not found");
        }

        Optional<Job> optionalJob = jobRepository.findById(jobId);
        if (!optionalJob.isPresent()) {
            throw new JobAlreadyExistException("Job not found");
        }

        if (!cv.isEmpty()) {
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
        applicationDocuments.setStatus("Pending");
        applicationDocuments.setJobs(job);
        LocalDate createdDate = LocalDate.now();
        applicationDocuments.setCreateAt(createdDate);
        return applicationDocumentsRepository.save(applicationDocuments);
    }

    @Override
    public void cancleAD(Long applicationDocumentsId) {
        Optional<ApplicationDocuments> applicationDocuments = applicationDocumentsRepository.findById(applicationDocumentsId);
        if (applicationDocuments.isPresent()) {
            applicationDocumentsRepository.deleteById(applicationDocumentsId);
        } else {
            throw new ApplicationAlreadyExistsException("Application Documents with ID " + applicationDocumentsId + " not found");
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
    public List<ApplicationDocuments> getAllJobByAdmin(Long adminId) {
        return applicationDocumentsRepository.getAllJobByAdmin(adminId);
    }

    @Override
    public List<ApplicationDocuments> getAllJobByCustomer(Long adminId) {
        return applicationDocumentsRepository.findByAdmin(adminId);
    }

    @Override
    public ApplicationDocuments updateStatus(Long applicationDocumentsId, String status) {
        Optional<ApplicationDocuments> applicationDocumentsOptional = applicationDocumentsRepository.findById(applicationDocumentsId);

        if (applicationDocumentsOptional.isPresent()) {
            ApplicationDocuments applicationDocuments = applicationDocumentsOptional.get();
            applicationDocuments.setStatus(status);
            applicationDocumentsRepository.save(applicationDocuments);
            String jobName = applicationDocuments.getJobs().getJobName();
            String companyName = applicationDocuments.getJobs().getAdmins().getCompanyName();
            if ("Received".equalsIgnoreCase(status) || "Reject".equalsIgnoreCase(status)) {
                try {
                    sendEmail(applicationDocuments.getEmail(), status, jobName, companyName);
                } catch (Exception e) {
                    System.err.println("Không thể gửi email: " + e.getMessage());
                }
            }
//            Notification notification = new Notification();
//            notification.setTitle();
            return applicationDocuments;
        } else {
            throw new ApplicationAlreadyExistsException("Application Documents với ID " + applicationDocumentsId + " không tồn tại");
        }
    }

    @Override
    public List<ApplicationDocuments> getApplicationDocumentsByStatus(String status,  Long adminId) {
        return applicationDocumentsRepository.findByStatusAndAdminId(status, adminId);
    }

    @Override
    public long countApplicationDocuments(Long adminId) {
        return applicationDocumentsRepository.countReceivedApplicationDocuments(adminId);
    }

    @Override
    public long countApplicationDocumentsPending(Long adminId) {
        return applicationDocumentsRepository.countPendingApplicationDocuments(adminId);
    }

    private void sendEmail(String toEmail, String status, String jobName, String companyName) {
        if (mailSender == null) {
            throw new IllegalStateException("MailSender chưa được cấu hình.");
        }

        System.out.println("Đang gửi email tới: " + toEmail);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("nguyenxuanmanh2k2@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Thông báo về hồ sơ xin việc");

        String messageText;
        if ("Received".equalsIgnoreCase(status)) {
            messageText = "Hồ sơ xin việc của bạn đã được chấp nhận, hẹn bạn sau 7 ngày nữa đến phỏng vấn vị trí "
                    + jobName + ".\n\nTrân trọng,\n" + companyName + ".";
        } else if ("Reject".equalsIgnoreCase(status)) {
            messageText = "Hồ sơ xin việc của bạn đã bị từ chối.\n\nTrân trọng,\n" + companyName + ".";
        } else {
            messageText = "Trạng thái hồ sơ của bạn không xác định.\n\nTrân trọng,\n" + companyName + ".";
        }

        message.setText(messageText);
        try {
            mailSender.send(message);
            System.out.println("Email đã được gửi thành công.");
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi email: " + e.getMessage());
        }
    }
}
