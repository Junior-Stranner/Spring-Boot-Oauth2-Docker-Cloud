package br.com.judev.libraryapi.controller;

import br.com.judev.libraryapi.model.Autor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/autores")
@RequiredArgsConstructor
public class AutorController {

    @PostMapping
    public Object salvar(Autor autor){

    }
}
