package com.springproject.dhVinh.SpringBootProject.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "jobName")
    private String jobName;
    @Column(name = "experience")
    private String experience;
    @Column(name = "applicationDeadline")
    private Date applicationDeadline;
    @Column(name = "recruitmentDetails")
    private String recruitmentDetails;
    @Column(name = "status")
    private Boolean status;
    @Column(name = "createAt")
    private LocalDate createAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "admin_id")
    private Admin admins;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "category_id")
    private Category categories;

}
