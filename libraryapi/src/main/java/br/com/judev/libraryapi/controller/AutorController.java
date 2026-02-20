package br.com.judev.libraryapi.controller;

import br.com.judev.libraryapi.controller.dto.AutorDTO;
import br.com.judev.libraryapi.controller.dto.ErroResposta;
import br.com.judev.libraryapi.exceptions.RegistroDuplicadoException;
import br.com.judev.libraryapi.model.Autor;
import br.com.judev.libraryapi.model.Usuario;
import br.com.judev.libraryapi.security.SecurityService;
import br.com.judev.libraryapi.service.AutorService;
import br.com.judev.libraryapi.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/autores")
public class AutorController implements GenericController{

    private final AutorService autorService;
    private final UsuarioService usuarioService;
    private final SecurityService securityService;

    public AutorController(AutorService autorService, UsuarioService usuarioService, SecurityService securityService) {
        this.autorService = autorService;
        this.usuarioService = usuarioService;
        this.securityService = securityService;
    }

    @PostMapping
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Void> salvar(@RequestBody @Valid AutorDTO dto) {
//        UserDetails userLogado = (UserDetails) authentication.getPrincipal();
//        Usuario usuario = usuarioService.obterPorLogin(userLogado.getUsername());
        Usuario usuario = securityService.obterUsuarioLogado();
        Autor autor = dto.toEntity();
        autorService.salvar(autor);
        URI location = gerarHeaderLocation(autor.getId());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<?> atualizar(@PathVariable("id") String id, @RequestBody @Valid AutorDTO dto) {
     try{
        var idAutor = UUID.fromString(id);
        Optional<Autor> autorOptional = autorService.obterPorId(idAutor);

        if (autorOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var autor = autorOptional.get();
        autor.setNome(dto.nome());
        autor.setNacionalidade(dto.nacionalidade());
        autor.setDataNascimento(dto.dataNascimento());

        autorService.atualizar(autor);

        return ResponseEntity.noContent().build();
     }catch (RegistroDuplicadoException e){
         var erroDTO = ErroResposta.conflito(e.getMessage());
         return ResponseEntity.status(erroDTO.status()).body(erroDTO);
      }
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    public ResponseEntity<AutorDTO> obterDetalhes(@PathVariable("id") String id) {
        var idAutor = UUID.fromString(id);
        Optional<Autor> autorOptional = autorService.obterPorId(idAutor);
        if (autorOptional.isPresent()) {
            Autor autor = autorOptional.get();
            AutorDTO dto = new AutorDTO(
                    autor.getId(),
                    autor.getNome(),
                    autor.getDataNascimento(), autor.getNacionalidade());
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    public ResponseEntity<List<AutorDTO>> pesquisar(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "nacionalidade", required = false) String nacionalidade) {
        List<Autor> resultado = autorService.pesquisaByExample(nome, nacionalidade);
        List<AutorDTO> lista = resultado
                .stream()
                .map(autor -> new AutorDTO(
                        autor.getId(),
                        autor.getNome(),
                        autor.getDataNascimento(), autor.getNacionalidade())
                ).collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Void> deletar(@PathVariable("id") String id) {
        var idAutor = UUID.fromString(id);
        Optional<Autor> autorOptional = autorService.obterPorId(idAutor);

        if (autorOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        autorService.deletar(autorOptional.get().getId());
        return ResponseEntity.noContent().build();
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