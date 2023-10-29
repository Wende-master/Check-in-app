package com.example.check_in_app;
import com.example.check_in_app.modelos.User;
import com.example.check_in_app.servicio.UserService;
import com.example.check_in_app.storage.StorageProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.sql.Time;
import java.time.LocalTime;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class Check_in_app {
    public static void main(String[] args) {
        SpringApplication.run(Check_in_app.class, args);
    }

    //Para generar usuarios aleatorios en la base de datos   // La contraseña es 1234
    /*@Bean
    CommandLineRunner commandLineRunner(UserService servicioUsuarios) {
        return args -> {
            for (int i = 0; i < 6; i++) {
                String correo = "john" + i + "@gmail.com";
                servicioUsuarios.save(new User("John Doe " + i, correo, "https://i.pravatar.cc/150?u=" + correo, "$2a$12$QO8HqfpzA7cUGlyDFQ5/FeKfH.laaMRIFsQiQX8oCVStWX0HavrTW", Time.valueOf(LocalTime.now()), Time.valueOf(LocalTime.now())));
            }
        };

    }
*/
}
