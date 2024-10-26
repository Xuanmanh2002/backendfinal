package com.springproject.dhVinh.SpringBootProject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "note")
    private String note;
    @Column(name = "orderDate")
    private LocalDate orderDate;
    @Column(name = "orderStatus")
    private String orderStatus;
    @Column(name = "totalAmounts")
    private Double totalAmounts;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admins;

}
