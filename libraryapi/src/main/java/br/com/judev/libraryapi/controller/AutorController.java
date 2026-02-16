package br.com.judev.libraryapi.controller;

import br.com.judev.libraryapi.controller.dto.AutorDTO;
import br.com.judev.libraryapi.model.Autor;
import br.com.judev.libraryapi.service.AutorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("api/v1/autores")
public class AutorController {

    private final AutorService autorService;

    public AutorController(AutorService autorService) {
        this.autorService = autorService;
    }

    @PostMapping
    public ResponseEntity<Void> salvar(@RequestBody @Valid AutorDTO dto) {
        Autor autor = dto.toEntity(); // usa o método do próprio DTO
        autorService.salvar(autor);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()     // ex: http://localhost:8080/autores
                .path("/{id}")            // importante: com a "/" antes do {id}
                    .buildAndExpand(autor.getId())
                    .toUri();
        return ResponseEntity.created(location).build();
    }
}

/*
A partir do URI location, o código está seguindo o padrão REST para criação de recurso: quando você faz POST e cria algo, a resposta deve ser 201 Created e idealmente trazer no header Location a URL onde o novo recurso pode ser encontrado.

O que é URI location
location é a URL do recurso recém-criado (ex.: o Autor que acabou de ser salvo). Essa URL vai no header HTTP Location.

Como esse trecho monta a URL

URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("{id}")
        .buildAndExpand(autor.getId())
        .toUri();
Passo a passo:

.fromCurrentRequest()
Pega a URL da requisição atual (o endpoint do POST).

Se o POST foi em /autores, a base vira:
http://localhost:8080/autores
.path("{id}")
Acrescenta um “pedaço” no final do caminho para incluir o id do novo recurso.

Atenção: normalmente aqui se usa "/{id}" (com barra).
Sem a barra, pode ficar algo como /autores123 em vez de /autores/123.
O mais comum/ideal:

.path("/{id}")
.buildAndExpand(autor.getId())
Substitui {id} pelo valor real do id gerado no banco.

Se autor.getId() = 123, o resultado vira /autores/123.
.toUri()
Converte o builder para um objeto URI.

O que a resposta faz

return ResponseEntity.created(location).build();
Retorna HTTP 201 Created
Coloca o header:

Location: http://localhost:8080/autores/123
E sem body (Void), porque você usou .build().
Observações importantes
Para autor.getId() estar preenchido aqui, o autorService.salvar(autor) precisa persistir e o ID precisa ser gerado e atribuído (ex.: @GeneratedValue) antes do retorno.
Ajuste recomendado:

.path("/{id}")
 */