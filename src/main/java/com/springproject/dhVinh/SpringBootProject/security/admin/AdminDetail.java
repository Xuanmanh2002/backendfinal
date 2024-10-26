package com.springproject.dhVinh.SpringBootProject.security.admin;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Blob;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminDetail implements UserDetails {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Blob avatar; // Change from String to Blob
    private Collection<GrantedAuthority> authorities;

    public static AdminDetail adminDetail(Admin admin) {
        List<GrantedAuthority> authorities = admin.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        return new AdminDetail(
                admin.getId(),
                admin.getEmail(),
                admin.getPassword(),
                admin.getFirstName(),
                admin.getLastName(),
                admin.getAvatar(), // No change needed here
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}