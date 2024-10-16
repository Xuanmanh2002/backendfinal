package com.springproject.dhVinh.SpringBootProject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", unique = true, nullable = false)
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "createAt")
    private LocalDate createAt;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Collection<Admin> admins = new HashSet<>();

    public Role(String name) {
        this.name = name;
    }

    public void assignRoleToAdmin(Admin admin){
        admin.getRoles().add(this);
        this.getAdmins().add(admin);
    }

    public void removeAdminsToRole(Admin admin){
        admin.getRoles().remove(this);
        this.getAdmins().remove(admin);
    }

    public void removeAllAdminsFromRole(){
        if (this.getAdmins() != null){
            List<Admin> roleUsers = this.getAdmins().stream().toList();
            roleUsers.forEach(this :: removeAdminsToRole);
        }
    }
    public  String getName(){
        return name != null? name : "";
    }
}
