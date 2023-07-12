package io.lucasprojects.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.lucasprojects.dscatalog.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
