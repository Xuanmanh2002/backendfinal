package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.exception.ApplicationAlreadyExistsException;
import com.springproject.dhVinh.SpringBootProject.exception.JobAlreadyExistException;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Job;
import com.springproject.dhVinh.SpringBootProject.model.Report;
import com.springproject.dhVinh.SpringBootProject.repository.JobRepository;
import com.springproject.dhVinh.SpringBootProject.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService implements IReportService{

    private final JobRepository jobRepository;

    private final ReportRepository reportRepository;

    private final JavaMailSender mailSender;

    @Override
    public Report addReport(Admin admin, String fullname, String email, String telephone, String letter, String address, Long jobId) {
        Report report = new Report();

        if (admin == null) {
            throw new RuntimeException("Admin not found");
        }

        Optional<Job> optionalJob = jobRepository.findById(jobId);

        if (!optionalJob.isPresent()) {
            throw new JobAlreadyExistException("Job not found");
        }

        Job job = optionalJob.get();
        report.setAdmins(admin);
        report.setFullName(fullname);
        report.setEmail(email);
        report.setTelephone(telephone);
        report.setLetter(letter);
        report.setStatus(false);
        report.setJobs(job);
        report.setAddress(address);
        LocalDate createdDate = LocalDate.now();
        report.setCreateAt(createdDate);
        return reportRepository.save(report);
    }

    @Override
    public void cancleReport(Long reportId) {
        Optional<Report> report = reportRepository.findById(reportId);
        if (report.isPresent()) {
            reportRepository.deleteById(reportId);
        } else {
            throw new RuntimeException("Report with ID " + reportId + " not found");
        }
    }

    @Override
    public List<Report> getAllReport() {
        return reportRepository.findAll();
    }

    @Override
    public Report updateStatus(Long reportId, Boolean status) {
        return null;
    }


}
