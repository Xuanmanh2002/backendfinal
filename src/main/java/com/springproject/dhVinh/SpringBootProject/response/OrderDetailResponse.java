package com.springproject.dhVinh.SpringBootProject.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class OrderDetailResponse {
    private Long id;
    private Long quantity;
    private Double price;
    private Double totalAmounts;
    private Long totalValidityPeriod;
    private Long totalBenefit;
    private LocalDate activationDate;
    private Boolean status;
    private Long serviceId;
    private Long orderId;

    public OrderDetailResponse(Long id, Long quantity, Double price, Double totalAmounts, Long totalValidityPeriod, Long totalBenefit, LocalDate activationDate, Boolean status, Long serviceId, Long orderId) {
        this.id = id;
        this.quantity = quantity;
        this.price = price;
        this.totalAmounts = totalAmounts;
        this.totalValidityPeriod = totalValidityPeriod;
        this.totalBenefit = totalBenefit;
        this.activationDate = activationDate;
        this.status = status;
        this.serviceId = serviceId;
        this.orderId = orderId;
    }
}
