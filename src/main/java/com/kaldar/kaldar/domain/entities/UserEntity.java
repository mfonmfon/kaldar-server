package com.kaldar.kaldar.domain.entities;
import com.kaldar.kaldar.contants.Role;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "user")
public abstract class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
    @Column(nullable = false)
   private String firstName;
    @Column(nullable = false)
   private String lastName;
    @Column(unique = true, nullable = false)
   private String email;
    @Column(unique = true, nullable = false)
   private String phoneNumber;
    @Column(nullable = false)
   private String password;

    @Column(nullable = false)
    private boolean verifiedUser = false;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
   private Set<Role> roles = new HashSet<>();




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isVerifiedUser() {
        return verifiedUser;
    }

    public void setVerifiedUser(boolean verifiedUser) {
        this.verifiedUser = verifiedUser;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }


}