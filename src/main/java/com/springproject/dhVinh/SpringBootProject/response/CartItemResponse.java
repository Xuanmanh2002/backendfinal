package com.springproject.dhVinh.SpringBootProject.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartItemResponse {
    private Long id;
    private Double totalPrice;
    private Long quantity;
    private Long totalValidityPeriod;
    private Long cartId;
    private Long serviceId;

    public CartItemResponse(Long id, Double totalPrice, Long quantity, Long totalValidityPeriod, Long cartId, Long serviceId) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
        this.totalValidityPeriod = totalValidityPeriod;
        this.cartId = cartId;
        this.serviceId = serviceId;
    }
}
