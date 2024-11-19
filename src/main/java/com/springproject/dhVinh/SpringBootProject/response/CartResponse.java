package com.springproject.dhVinh.SpringBootProject.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartResponse {
    private Long id;
    private Double totalAmounts;
    private Long totalItems;
    private Long totalValidityPeriod;
    private EmployerResponse employerResponse;

    public CartResponse(Long id, Double totalAmounts, Long totalItems, Long totalValidityPeriod, EmployerResponse employerResponse) {
        this.id = id;
        this.totalAmounts = totalAmounts;
        this.totalItems = totalItems;
        this.totalValidityPeriod = totalValidityPeriod;
        this.employerResponse = employerResponse;
    }
}
