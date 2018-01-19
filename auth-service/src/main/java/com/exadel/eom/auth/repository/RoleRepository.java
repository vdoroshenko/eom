package com.exadel.eom.auth.repository;

import com.exadel.eom.auth.domain.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {
}
