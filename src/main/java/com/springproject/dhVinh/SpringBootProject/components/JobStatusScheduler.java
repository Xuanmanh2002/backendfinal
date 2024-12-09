package com.springproject.dhVinh.SpringBootProject.components;

import com.springproject.dhVinh.SpringBootProject.model.Job;
import com.springproject.dhVinh.SpringBootProject.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JobStatusScheduler {

    private final JobRepository jobRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateJobStatuses() {
        List<Job> jobs = jobRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Job job : jobs) {
            if (job.getActivationDate() != null && job.getTotalValidityPeriod() != null) {
                LocalDate expiryDate = job.getActivationDate().plusDays(job.getTotalValidityPeriod());
                if (today.isAfter(expiryDate)) {
                    job.setStatus(false);
                } else {
                    job.setStatus(true);
                }
                jobRepository.save(job);
            }
        }
    }
}