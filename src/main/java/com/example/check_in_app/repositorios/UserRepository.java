package com.example.check_in_app.repositorios;

import com.example.check_in_app.modelos.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Time;
import java.util.List;
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    User findById(long id);

    User findByName(String name);

    List<User> findByNameContaining(String name);

    void findByHoraEntrada(Time fechaEntrada);

    void findByHoraSalida(Time fechaSalida);

    //void updateUser(Long id, UserDto userDto);

    /*@Transactional
    @Modifying
    @Query("UPDATE User u SET u.name = :#{#user.firstName + ' ' + #user.lastName}, u.email = :#{#user.email}, u.avatar = :#{#user.avatar} WHERE u.id = :id")
    void updateUser(@Param("id") Long id, @Param("user") UserDto user);*/


}