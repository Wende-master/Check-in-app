package com.example.check_in_app.controlador;

import com.example.check_in_app.modelos.Entrada;
import com.example.check_in_app.modelos.User;
import com.example.check_in_app.modelos.userdto.UserDto;
import com.example.check_in_app.repositorios.UserRepository;
import com.example.check_in_app.servicio.ServicioEntrada;
import com.example.check_in_app.servicio.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1")
@CrossOrigin("*")
public class RestPrincipal {
    @Autowired
    ServicioEntrada servicioEntrada;
    @Autowired
    UserService userService;

    @GetMapping("entradas")
    public List<Entrada> verTodas(Model model){
        return servicioEntrada.findAll();
    }

    @GetMapping("usuarios")
    public List<UserDto> verUsuarios(Model model){
        return userService.findAllUsers();
    }
}
