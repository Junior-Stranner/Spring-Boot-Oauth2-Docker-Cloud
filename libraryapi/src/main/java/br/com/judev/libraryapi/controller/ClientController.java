package br.com.judev.libraryapi.controller;

import br.com.judev.libraryapi.controller.dto.UsuarioDTO;
import br.com.judev.libraryapi.controller.mappers.UsuarioMapper;
import br.com.judev.libraryapi.model.Client;
import br.com.judev.libraryapi.service.ClientService;
import br.com.judev.libraryapi.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('GERENTE')")
    public void salvar(@RequestBody Client client){
        service.salvar(client);
    }
}