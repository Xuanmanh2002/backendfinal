package com.springproject.dhVinh.SpringBootProject.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "totalAmounts")
    private Double totalAmounts;
    @Column(name = "totalItems")
    private Long totalItems;
    @Column(name = "totalValidityPeriod")
    private Long totalValidityPeriod;
    @Column(name = "totalBenefit")
    private Long totalBenefit;

    @OneToMany(mappedBy = "carts", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<CartItem> cartItems = new ArrayList<>();;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "admin_id")
    private Admin admins;

}
