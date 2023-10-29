package com.example.check_in_app.authcontroller;

import com.example.check_in_app.modelos.Mensaje;
import com.example.check_in_app.modelos.User;
import com.example.check_in_app.modelos.userdto.UserDto;
import com.example.check_in_app.servicio.ServicioEntrada;
import com.example.check_in_app.servicio.ServicioMensajes;
import com.example.check_in_app.servicio.UserService;
import com.example.check_in_app.storage.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Controller
public class AuthController {
    @Autowired
    UserService servicioUsuarios;
    @Autowired
    ServicioMensajes servicioMensajes;
    @Autowired
    ServicioEntrada servicioEntrada;
    @Autowired
    StorageService storageService;


    public AuthController(UserService userService) {
        this.servicioUsuarios = userService;

    }

    @GetMapping("/buscarUsuario")
    @ResponseBody
    public List<UserDto> buscarUsuario(@RequestParam("nombre") String nombre) {
        List<User> usuarios = servicioUsuarios.searchUsers(nombre);

        // Convertir los usuarios a DTOs
        List<UserDto> dtos = new ArrayList<>();
        for (User usuario : usuarios) {
            UserDto dto = new UserDto();
            dto.setId(usuario.getId());
            dto.setFirstName(usuario.getName());
            dto.setEmail(usuario.getEmail());
            dtos.add(dto);
        }
        return dtos;
    }


    @GetMapping({"/", "/index"})
    public String inicio(Model model, Authentication authentication, HttpSession session, User user) {

        model.addAttribute("lista", servicioUsuarios.findAll());

        User actual = servicioUsuarios.findUserByEmail(authentication.getName());
        model.addAttribute("list", servicioEntrada.findAll());

        model.addAttribute("actual", actual);

        model.addAttribute("totalHorasTrabajadas", actual.getTotalHorasTrabajadas());

        return "index";
    }

    @GetMapping("/userstable")
    public String users(Model model) {
        List<UserDto> users = servicioUsuarios.findAllUsers();
        model.addAttribute("users", users);

        return "userstable";
    }

    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable Long id, Model model) {

        servicioUsuarios.deleteById(id);

        return "redirect:/userstable?deleteSuccess=true";
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String showRegistrationForm(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("user", user);

        return "registro";
    }

    @PostMapping("/registro/guardar")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result,
                               Model model, @RequestParam("file") MultipartFile file) {
        User existingUser = servicioUsuarios.findUserByEmail(userDto.getEmail());

        if (file != null && !file.isEmpty()) {
            String nombreFichero = storageService.store(file);
            userDto.setAvatar("http://localhost:9500/files/" + nombreFichero);
            //http://localhost:9500/files/ -> Con este formato las imágenes se podrán leer en la web, siempre y cuando los datos sean correctos

        } else {
            // Si no se seleccionó ninguna imagen, establecer una URL de imagen por defecto en el objeto userDto
            userDto.setAvatar("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS-4j0Oe5cma88GR-7QnLGS1IHpvKZiKuWy8g&usqp=CAU");
        }

        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", null,
                    "Ya existe una cuenta con este email");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "redirect:/registro?error";
        }

        servicioUsuarios.saveUser(userDto);
        return "redirect:/registro?success";
    }

    @GetMapping("/editar/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, Authentication authentication) {
        User user = servicioUsuarios.findById(id);

        User actual = servicioUsuarios.findUserByEmail(authentication.getName());
        model.addAttribute("actual", actual);

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        String[] nameParts = user.getName().split(" ");
        userDto.setFirstName(nameParts[0]);
        userDto.setLastName(nameParts[1]);
        userDto.setEmail(user.getEmail());
        model.addAttribute("user", userDto);
        return "editar";
    }

    @PostMapping("/editar/{id}")
    public String editUser(@PathVariable("id") Long id, @Valid @ModelAttribute("user") UserDto userDto,
                           BindingResult result,
                           Model model, @RequestParam("file") MultipartFile file) {
        User existingUser = servicioUsuarios.findById(id);
        if (existingUser == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        existingUser.setName(userDto.getFirstName() + " " + userDto.getLastName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setPassword(userDto.getPassword());

        if (file != null && !file.isEmpty()) {
            String nombreFichero = storageService.store(file);
            userDto.setAvatar("http://localhost:9500/files/" + nombreFichero);
            existingUser.setAvatar(userDto.getAvatar());
        }

        User userByEmail = servicioUsuarios.findUserByEmail(userDto.getEmail());
        if (userByEmail != null && !userByEmail.getId().equals(id)) {
            result.rejectValue("email", null, "Ya existe una cuenta con este email");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "editar";
        }

        servicioUsuarios.updateUser(id, userDto);
        return "redirect:/editar/{id}?success";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }


    @GetMapping("/chat/{id}")
    public String chat(@PathVariable long id, Model model, HttpSession request, Authentication authentication) {

        //Envío a la vista el usuario actual (variable de sesión) y el receptor al que le quiere enviar mensajes
        User actual = servicioUsuarios.findByEmail(authentication.getName());
        User destinatario = servicioUsuarios.findById(id);
        model.addAttribute("actual", actual);  //Esto después lo "cogeremos" de Spring Security
        model.addAttribute("receptor", destinatario);

        //Debo enviar a la vista la lista de mensajes de "actual" a "destinatario" y viceversa
        List<Mensaje> lista1 = servicioMensajes.findByEmisorAndDestinatario(actual, destinatario);
        List<Mensaje> lista2 = servicioMensajes.findByEmisorAndDestinatario(destinatario, actual);

        //Mezclo las dos listas en una y la ordeno por fecha
        List<Mensaje> mezcla = new ArrayList<>();
        mezcla.addAll(lista1);
        mezcla.addAll(lista2);
        Collections.sort(mezcla, new Comparator<Mensaje>() {
            @Override
            public int compare(Mensaje m1, Mensaje m2) {
                return m1.getFecha().compareTo(m2.getFecha());
            }
        });
        model.addAttribute("listaMensajes", mezcla);

        //Envío un mensaje vacío que es el que después nos devolverá en PostMapping si escriben uno
        Mensaje mensaje = new Mensaje();
        mensaje.setEmisor(actual);
        mensaje.setDestinatario(destinatario);
        model.addAttribute("mensaje", new Mensaje());
        return "chat";
    }

    @PostMapping("/enviar")
    public String guardarMensaje(@ModelAttribute("mensaje") Mensaje mensaje,
                                 HttpServletRequest request, //Esto es para volver a la página desde la que nos han "llamado"
                                 @RequestParam("emisor") Long emisorid, //Es el id de quien envía el mensaje
                                 @RequestParam("destinatario") Long destinatarioid) // Es el id de quien recibe el mensaje
    {
        mensaje.setFecha(LocalDateTime.now());
        mensaje.setEmisor(servicioUsuarios.findById(emisorid));
        mensaje.setDestinatario(servicioUsuarios.findById(destinatarioid));
        servicioMensajes.save(mensaje);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @PostMapping("/ficharEntrada")
    public String ficharEntrada(Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        User actual = servicioUsuarios.findByEmail(authentication.getName()); // obtenemos el usuario actual
        servicioUsuarios.updateTotalHorasTrabajadas(actual);

        LocalTime horaActual = LocalTime.now();
        actual.setHoraEntrada(Time.valueOf(horaActual)); // actualizamos la fecha de entrada

        servicioUsuarios.save(actual); // guardamos el usuario en la base de datos

        // Agregamos un mensaje de éxito al objeto RedirectAttributes
        redirectAttributes.addFlashAttribute("mensaje", "Entrada registrada a las " + Time.valueOf(horaActual));

        return "redirect:/";
    }

    @PostMapping("/ficharSalida")
    public String ficharSalida(Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        User actual = servicioUsuarios.findByEmail(authentication.getName()); // obtenemos el usuario actual
        servicioUsuarios.updateTotalHorasTrabajadas(actual);

        LocalTime horaActual = LocalTime.now();
        actual.setHoraSalida(Time.valueOf(horaActual)); // actualizamos la fecha de salida

        servicioUsuarios.save(actual); // guardamos el usuario en la base de datos

        // Agregamos un mensaje de éxito al objeto RedirectAttributes
        redirectAttributes.addFlashAttribute("mensaje", "Salida registrada a las " + Time.valueOf(horaActual));

        return "redirect:/";
    }

}