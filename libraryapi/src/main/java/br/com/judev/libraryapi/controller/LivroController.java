package br.com.judev.libraryapi.controller;

import br.com.judev.libraryapi.controller.dto.CadastroLivroDTO;
import br.com.judev.libraryapi.controller.dto.ErroResposta;
import br.com.judev.libraryapi.controller.dto.ResultadoPesquisaLivroDTO;
import br.com.judev.libraryapi.exceptions.RegistroDuplicadoException;
import br.com.judev.libraryapi.model.Livro;
import br.com.judev.libraryapi.service.LivroService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("livros")
public class LivroController implements GenericController{

    private final LivroService livroService;

    public LivroController(LivroService livroService){
        this.livroService = livroService;
    }

    @PostMapping
    public ResponseEntity<Void> salvar(@RequestBody @Valid CadastroLivroDTO dto) {
        Livro livro = dto.toEntity();
        livroService.salvar(livro);
        var url = gerarHeaderLocation(livro.getId());
        return ResponseEntity.created(url).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultadoPesquisaLivroDTO> obterDetalhes(@PathVariable UUID id) {

        return livroService.obterPorId(id)
                .map(livro -> ResponseEntity.ok(ResultadoPesquisaLivroDTO.toDTO(livro)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable("id") String id){
        return livroService.obterPorId(UUID.fromString(id))
                .map(livro -> {
                    livroService.deletar(livro);
                    return ResponseEntity.noContent().build();
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
