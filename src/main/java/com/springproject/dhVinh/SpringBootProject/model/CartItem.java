package com.springproject.dhVinh.SpringBootProject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cartItems")
public class CartItem {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "totalPrice")
    private Double totalPrice;
    @Column(name = "quantity")
    private Long quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_pack_id")
    private ServicePack services;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carts_id")
    private Cart carts;

}
