package com.springproject.dhVinh.SpringBootProject.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private LocalDate orderDate;
    private String orderStatus;
    private Double totalAmounts;
    private Long totalValidityPeriod;
    private EmployerResponse employerResponse;

    public OrderResponse(Long id, LocalDate orderDate, String orderStatus, Double totalAmounts, Long totalValidityPeriod, EmployerResponse employerResponse) {
        this.id = id;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.totalAmounts = totalAmounts;
        this.totalValidityPeriod = totalValidityPeriod;
        this.employerResponse = employerResponse;
    }
}
