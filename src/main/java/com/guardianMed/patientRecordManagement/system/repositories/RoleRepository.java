package com.guardianMed.patientRecordManagement.system.repositories;

import com.guardianMed.patientRecordManagement.system.models.Role;
import com.guardianMed.patientRecordManagement.system.models.ERole;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
     Optional<Role> findByName(ERole name);
}
