package com.example.check_in_app.servicio.userimpl;

import com.example.check_in_app.modelos.Mensaje;
import com.example.check_in_app.modelos.Role;
import com.example.check_in_app.modelos.User;
import com.example.check_in_app.modelos.userdto.UserDto;
import com.example.check_in_app.repositorios.RepositorioMensajes;
import com.example.check_in_app.repositorios.RoleRepository;
import com.example.check_in_app.repositorios.UserRepository;
import com.example.check_in_app.servicio.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    private RepositorioMensajes repositorioMensajes;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           RepositorioMensajes repositorioMensajes,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.repositorioMensajes = repositorioMensajes;

    }

    @Override
    public List<User> searchUsers(String name) {
        return userRepository.findByNameContaining(name);
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setAvatar(userDto.getAvatar());

        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role role = roleRepository.findByName("ROLE_USER");
        if (role == null) {
            role = checkRoleExist();
        }

        if (user.getEmail().equalsIgnoreCase("wende@gmail.com") || user.getEmail().equalsIgnoreCase("admin@gmail.com")) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN");
            if (adminRole == null) {
                adminRole = new Role();
                adminRole.setName("ROLE_ADMIN");
                roleRepository.save(adminRole);
            }
            user.setRoles(Arrays.asList(adminRole));
        } else {
            user.setRoles(Arrays.asList(role));
        }

        userRepository.save(user);
    }


    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());
    }

    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId() != null ? user.getId() : 0L); // se utiliza 0L en lugar de null
        String[] str = user.getName().split(" ");
        userDto.setFirstName(str[0]);
        userDto.setLastName(str[1]);
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    private Role checkRoleExist() {
        Role role = new Role();
        role.setName("ROLE_USER");
        return roleRepository.save(role);
    }


    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(long id) {
        return userRepository.findById(id);
    }

    public User findByUsername(String username) {
        return userRepository.findByName(username);
    }

    public void findByHoraEntrada(User actual) {
        userRepository.findByHoraEntrada(Time.valueOf(LocalTime.now()));
        updateTotalHorasTrabajadas(actual);
    }

    public void findByHoraSalida(User actual) {
        userRepository.findByHoraSalida(Time.valueOf(LocalTime.now()));
        updateTotalHorasTrabajadas(actual);
    }

    public String updateTotalHorasTrabajadas(User user) {
        if (user.getHoraEntrada() != null && user.getHoraSalida() != null) {
            long diffMillis = user.getHoraSalida().getTime() - user.getHoraEntrada().getTime();
            if (diffMillis < 0) {
                user.setHorasTrabajadas(null);
            } else {

                long diffMinutes = diffMillis / (60 * 1000);
                long hours = diffMinutes / 60;
                long minutes = diffMinutes % 60;

                double totalHours = hours + ((double) minutes / 60);
                user.setHorasTrabajadas(totalHours);

                String totalHoursFormatted = String.format("%d:%02d", hours, minutes);
                return totalHoursFormatted;
            }
        } else {
            user.setHorasTrabajadas(null);
        }
        userRepository.save(user);
        return null;
    }
    @Transactional
    @Override
    public void updateUser(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        existingUser.setName(userDto.getFirstName() + " " + userDto.getLastName());
        existingUser.setEmail(userDto.getEmail());
        // Verificar si la contraseña está encriptada o no
        String password = userDto.getPassword();
        if (!password.startsWith("$2a$")) { // si la contraseña no está encriptada
            password = new BCryptPasswordEncoder().encode(password); // encriptarla
        }
        existingUser.setPassword(password);

        existingUser.setAvatar(userDto.getAvatar());
        userRepository.save(existingUser);
    }

        public User save (User usuario){
            userRepository.save(usuario);
            return usuario;
        }

        @Transactional
        public void deleteById ( long id){
            User user = userRepository.findById(id);
            user.getRoles().clear(); // elimino los roles asociados a cada usuario

            //Borrar los mensajes entre emisor y receptor de la tabla mensaje usando @Transactional
            List<Mensaje> mensajes = repositorioMensajes.findByEmisorOrDestinatario(user, user);
            for (Mensaje mensaje : mensajes) {
                repositorioMensajes.delete(mensaje);
            }
            userRepository.delete(user);
        }

        public void delete (User usuario){
            userRepository.delete(usuario);
        }

    }