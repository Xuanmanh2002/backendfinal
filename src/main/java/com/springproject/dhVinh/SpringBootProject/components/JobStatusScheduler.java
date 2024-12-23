package com.springproject.dhVinh.SpringBootProject.components;

import com.springproject.dhVinh.SpringBootProject.model.Job;
import com.springproject.dhVinh.SpringBootProject.model.OrderDetail;
import com.springproject.dhVinh.SpringBootProject.repository.JobRepository;
import com.springproject.dhVinh.SpringBootProject.repository.OrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JobStatusScheduler {

    private final JobRepository jobRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateStatuses() {
        LocalDate today = LocalDate.now();
        List<Job> jobs = jobRepository.findAll();
        for (Job job : jobs) {
            boolean updated = false;
            if (job.getApplicationDeadline() != null && job.getApplicationDeadline().toLocalDate().isBefore(today)) {
                job.setStatus(false);
                updated = true;
            }
            if (job.getActivationDate() != null && job.getTotalValidityPeriod() != null) {
                LocalDate expiryDate = job.getActivationDate().plusDays(job.getTotalValidityPeriod());
                if (today.isAfter(expiryDate)) {
                    job.setStatus(false);
                    updated = true;
                } else if (!updated) {
                    job.setStatus(true);
                }
            }

            if (updated) {
                jobRepository.save(job);
            }
        }

        List<OrderDetail> orderDetails = orderDetailRepository.findAll();
        for (OrderDetail orderDetail : orderDetails) {
            boolean updated = false;

            if (orderDetail.getActivationDate() != null && orderDetail.getTotalValidityPeriod() != null) {
                LocalDate expiryDate = orderDetail.getActivationDate().plusDays(orderDetail.getTotalValidityPeriod());
                if (today.isAfter(expiryDate)) {
                    orderDetail.setStatus(false);
                    updated = true;
                } else if (!updated) {
                    orderDetail.setStatus(true);
                }
            }
            if (updated) {
                orderDetailRepository.save(orderDetail);
            }
        }
    }
}
