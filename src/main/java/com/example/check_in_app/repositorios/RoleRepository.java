package com.example.check_in_app.repositorios;

import com.example.check_in_app.modelos.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);


    //Role save(Role role, Role role2);
}