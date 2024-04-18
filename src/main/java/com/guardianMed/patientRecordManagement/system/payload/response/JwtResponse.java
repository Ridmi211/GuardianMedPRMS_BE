package com.guardianMed.patientRecordManagement.system.payload.response;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JwtResponse {

    private String token;

    private String type = "Bearer";

    private String id;

    private String username;

    private String email;

    private List<String> roles;

    private String successMessage;

    public JwtResponse(String token, String id, String username, String email, List<String> roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
