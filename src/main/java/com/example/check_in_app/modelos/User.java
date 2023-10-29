package com.example.check_in_app.modelos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private Time horaEntrada;

    private Time horaSalida;

    private Double horasTrabajadas;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_roles",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")})

    private List<Role> roles = new ArrayList<>();

    private String avatar;

    @OneToMany(mappedBy = "emisor")
    List<Mensaje> enviados;
    @OneToMany(mappedBy = "destinatario")
    List<Mensaje> recibidos;


    public User(String name, String email, String avatar, String password, Time horaEntrada, Time horaSalida) {
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.password = password;

        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
    }

    public String getTotalHorasTrabajadas() {
        if (horaEntrada == null || horaSalida == null) {
            return "Faltan datos de entrada o salida";
        }

        long diffMillis = horaSalida.getTime() - horaEntrada.getTime();
        if (diffMillis < 0) {
            return "La hora de salida es anterior a la hora de entrada";
        }

        long diffMinutes = diffMillis / (60 * 1000);
        long hours = diffMinutes / 60;
        long minutes = diffMinutes % 60;

        return String.format("%d:%02d", hours, minutes);
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}
