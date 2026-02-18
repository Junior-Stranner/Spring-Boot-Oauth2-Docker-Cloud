package br.com.judev.libraryapi.controller;

import br.com.judev.libraryapi.controller.dto.CadastroLivroDTO;
import br.com.judev.libraryapi.controller.dto.ErroResposta;
import br.com.judev.libraryapi.exceptions.RegistroDuplicadoException;
import br.com.judev.libraryapi.model.Livro;
import br.com.judev.libraryapi.service.LivroService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("livros")
public class LivroController {

    private final LivroService livroService;

    public LivroController(LivroService livroService){
        this.livroService = livroService;
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody @Valid CadastroLivroDTO dto) {
        try {
            Livro livro = dto.toEntity();
            livroService.salvar(livro);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()   // ex: http://localhost:8080/livros
                    .path("/{id}")
                    .buildAndExpand(livro.getId())
                    .toUri();

            return ResponseEntity.created(location).build();

        } catch (RegistroDuplicadoException e) {
            var erroDTO = ErroResposta.conflito(e.getMessage());
            return ResponseEntity.status(erroDTO.status()).body(erroDTO);
        }
    }
}
