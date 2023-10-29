package com.example.check_in_app.servicio;

import com.example.check_in_app.modelos.Mensaje;
import com.example.check_in_app.modelos.User;
import com.example.check_in_app.repositorios.RepositorioMensajes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioMensajes {
    @Autowired
    RepositorioMensajes repositorio;

    public List<Mensaje> findAll(){
        return repositorio.findAll();
    }

    public Mensaje findById(long id){
        return repositorio.findById(id);
    }

    public List<Mensaje> findByEmisorAndDestinatario(User emisor, User destinatario){
        return repositorio.findByEmisorAndDestinatario(emisor, destinatario);
    }

    public Mensaje save(Mensaje mensaje){
        return repositorio.save(mensaje);
    }

    public void delete(Mensaje mensaje){
        repositorio.delete(mensaje);
    }

}
