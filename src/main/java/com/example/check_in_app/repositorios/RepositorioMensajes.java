package com.example.check_in_app.repositorios;

import com.example.check_in_app.modelos.Mensaje;
import com.example.check_in_app.modelos.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RepositorioMensajes extends JpaRepository<Mensaje, Long> {
    public Mensaje findById(long id);
    public List<Mensaje> findByEmisorAndDestinatario(User emisor, User destinatario);

    @Query(
            value = "SELECT * FROM USERS u WHERE u.status = 1",
            nativeQuery = true)
    Collection<User> findAllActiveUsersNative();

    List<Mensaje> findByEmisorOrDestinatario(User user1, User user2);

}
