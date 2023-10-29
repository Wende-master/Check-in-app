package com.example.check_in_app.controlador;

import com.example.check_in_app.modelos.Entrada;
import com.example.check_in_app.servicio.ServicioEntrada;
import com.example.check_in_app.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class Principal {

    @Autowired
    ServicioEntrada servicioEntrada;
    @Autowired
    StorageService storageService;

    public Principal() {

    }

    @GetMapping({"admin"})
    public String admin(Model model) {
        model.addAttribute("list", servicioEntrada.findAll());
        return "admin";
    }

    @GetMapping({"save/{id}"})
    public String save(@PathVariable("id") Long id, Model model) {
        if (id != 0 && id != null) {
            model.addAttribute("entrada", servicioEntrada.findById(id));
        } else {
            model.addAttribute("entrada", new Entrada());
        }
        return "save";
    }

    @PostMapping("/save")
    public String save2(Entrada entrada, Model model, @RequestParam("file") MultipartFile file) {
        String nombreFichero = storageService.store(file);
        if (nombreFichero != null) {
            entrada.setImagen(nombreFichero);
        }
        servicioEntrada.save(entrada);
        return "redirect:/";
    }

    @GetMapping("/blog/{id}")
    public String mostrarEntrada(@PathVariable("id") Long id, Model model) {
        Entrada entrada = servicioEntrada.findById(id);
        if (entrada != null) {
            model.addAttribute("entrada", entrada);
        }
        return "blog";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Model model) {
        servicioEntrada.deleteById(id);

        return "redirect:/";
    }

}

