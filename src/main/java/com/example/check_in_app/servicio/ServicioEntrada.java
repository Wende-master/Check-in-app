package com.example.check_in_app.servicio;

import com.example.check_in_app.modelos.Entrada;
import com.example.check_in_app.repositorios.Repositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioEntrada {
    @Autowired
    Repositorio repositorio;

    public Entrada findById(long id ){
        return  repositorio.findById(id);
    }
    public Entrada save(Entrada entrada){
        return repositorio.save(entrada);
    }
    public List<Entrada>  findAll(){
        return  repositorio.findAll();
    }
    public void deleteById(long id){
        repositorio.deleteById(id);
    }
    public void delete(Entrada entrada) {
        repositorio.delete(entrada);
    }
}
