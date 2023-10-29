package com.example.check_in_app.servicio;

import com.example.check_in_app.modelos.User;
import com.example.check_in_app.modelos.userdto.UserDto;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);
    User findUserByEmail(String email);
    List<UserDto> findAllUsers();
    User findByEmail(String email);
    User findById(long id);
    List<User> findAll();
    public void deleteById(long id);
    User save(User usuario);
    List<User> searchUsers(String name);
    void findByHoraEntrada(User user);
    void findByHoraSalida(User user);
    String updateTotalHorasTrabajadas(User user);
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.name = :#{#user.firstName + ' ' + #user.lastName}, u.email = :#{#user.email}, u.avatar = :#{#user.avatar} WHERE u.id = :id")
    void updateUser(@Param("id") Long id, @Param("user") UserDto user);

}