package br.com.judev.libraryapi.service;

import br.com.judev.libraryapi.controller.dto.AutorDTO;
import br.com.judev.libraryapi.model.Autor;
import br.com.judev.libraryapi.repository.AutorRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class AutorService {

    private final AutorRepository autorRepository;

    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    public Autor salvar(Autor autor){
        return autorRepository.save(autor);
    }
}
