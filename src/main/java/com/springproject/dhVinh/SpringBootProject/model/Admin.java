package com.springproject.dhVinh.SpringBootProject.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "admins")
public class Admin {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "firstName")
    private String firstName;
    @Column(name = "lastName")
    private String lastName;
    @Column(name = "birthDate")
    private Date birthDate;
    @Column(name = "avatar")
    private String avatar;
    @Column(name = "gender")
    private String gender;
    @Column(name = "registrationDate")
    private LocalDate registrationDate;
    @Column(name = "telephone")
    private String telephone;
    @Column(name = "status")
    private Boolean status;
    @Column(name = "address")
    private String address;
    @Column(name = "companyName")
    private String companyName;
    @Column(name = "salaryRange")
    private String salaryRange;
    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST,
                    CascadeType.MERGE, CascadeType.DETACH})
    @JoinTable(name = "admin_roles",
            joinColumns = @JoinColumn(name = "admin_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "admins" , fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Job> jobs;

    @OneToMany(mappedBy = "admins" , fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ApplicationDocuments> applicationDocuments;

}
