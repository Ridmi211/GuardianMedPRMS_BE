package com.guardianMed.patientRecordManagement.system.payload.response;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Data
public class OtpResponse {

    private String otp;
    private Date expiryTime;

}
