package com.springproject.dhVinh.SpringBootProject.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "categories")
public class Category {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "categoryName", unique = true, nullable = false)
    private String categoryName;
    @Column(name = "description")
    private String description;
    @Column(name = "creatAt")
    private LocalDate createAt;

    @OneToMany(mappedBy = "categories", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Job> jobs;


}
