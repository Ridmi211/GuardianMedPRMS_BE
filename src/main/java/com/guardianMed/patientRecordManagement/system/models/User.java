package com.guardianMed.patientRecordManagement.system.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter@Setter

public class User {

    @Id
    private String id;

    private String username;

    private String email;

    private String password;

    private Set<Role> roles = new HashSet<>();

    private String otp;

    private Date otpExpiryTime;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
