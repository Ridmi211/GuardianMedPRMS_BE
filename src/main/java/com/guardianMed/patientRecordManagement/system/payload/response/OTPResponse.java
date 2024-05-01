package com.guardianMed.patientRecordManagement.system.payload.response;

import lombok.Data;
import org.springframework.stereotype.Component;


@Data
public class OTPResponse {

    private boolean otpSent;
    private String message;
    public OTPResponse(boolean otpSent, String message) {
        this.otpSent = otpSent;
        this.message = message;
    }
}
