package com.example.check_in_app.repositorios;

import com.example.check_in_app.modelos.Entrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Repositorio  extends JpaRepository<Entrada, Long> {
    public Entrada findById(long id);
    public List<Entrada> findAll();

    public Entrada save(Entrada entrada);

    public void delete(Entrada entrada);
    public void deleteById(long id);

}
