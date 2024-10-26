package com.springproject.dhVinh.SpringBootProject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orderDetails")
public class OrderDetail {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "quantity")
    private Long quantity;
    @Column(name = "price")
    private Double price;
    @Column(name = "totalAmounts")
    private Double totalAmounts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicePack_id")
    private ServicePack services;

}
