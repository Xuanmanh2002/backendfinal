package com.springproject.dhVinh.SpringBootProject.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "address")
public class Address {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name",  unique = true, nullable = false)
    private String name;
    @Column(name = "createAt")
    private LocalDate createAt;

    @OneToMany(mappedBy = "address" , fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Admin> admins ;
}
