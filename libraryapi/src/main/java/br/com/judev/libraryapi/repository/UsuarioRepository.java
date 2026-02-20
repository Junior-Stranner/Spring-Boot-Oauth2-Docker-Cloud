package br.com.judev.libraryapi.repository;

import br.com.judev.libraryapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID>{
    Usuario findByLogin(String login);

    Usuario findByEmail(String email);
}
