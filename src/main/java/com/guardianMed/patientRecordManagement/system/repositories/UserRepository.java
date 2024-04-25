package com.guardianMed.patientRecordManagement.system.repositories;

import com.guardianMed.patientRecordManagement.system.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByOtp(String otp);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    List<User> findAllByRoles_Name(String roleName);

}
