package com.guardianMed.patientRecordManagement.system.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @GetMapping("/home")
    public String homepage() {
        return "Welcome to the prescription homepage!";
    }

    @GetMapping("/dashboard")
    public String dash() {
        return "This is the dashboard";
    }

    @GetMapping("/manage")
    public String manage() {
        return "Hello ADMIN, this is the manage page ssss";
    }
}
