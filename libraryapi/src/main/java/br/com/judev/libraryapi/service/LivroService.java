package br.com.judev.libraryapi.service;

import br.com.judev.libraryapi.model.Autor;
import br.com.judev.libraryapi.model.GeneroLivro;
import br.com.judev.libraryapi.model.Livro;
import br.com.judev.libraryapi.repository.AutorRepository;
import br.com.judev.libraryapi.repository.LivroRepository;
import br.com.judev.libraryapi.validator.LivroValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static br.com.judev.libraryapi.repository.specs.LivroSpecs.*;

import java.util.Optional;
import java.util.UUID;

@Service
public class LivroService {

    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;
    private final LivroValidator livroValidator;

    public LivroService(LivroRepository livroRepository,
                        AutorRepository autorRepository,
                        LivroValidator livroValidator) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
        this.livroValidator = livroValidator;
    }

    @Transactional
    public Livro salvar(Livro livro) {
        // valida ISBN duplicado e regra do preço (>=2020)
        livroValidator.validar(livro);

        if (livro.getAutor() == null || livro.getAutor().getId() == null) {
            throw new IllegalArgumentException("Autor é obrigatório.");
        }

        UUID idAutor = livro.getAutor().getId();
        Autor autor = autorRepository.findById(idAutor)
                .orElseThrow(() -> new IllegalArgumentException("Autor não encontrado para o id: " + idAutor));

        livro.setAutor(autor);
        return livroRepository.save(livro);
    }

    @Transactional(readOnly = true)
    public Optional<Livro> obterPorId(UUID id) {
        return livroRepository.findById(id);
    }

    @Transactional
    public void deletar(Livro livro) {
        livroRepository.delete(livro);
    }

    // isbn, titulo, nome autor, genero, ano de publicação
    @Transactional(readOnly = true)
    public Page<Livro> pesquisa(
            String isbn,
            String titulo,
            String nomeAutor,
            GeneroLivro genero,
            Integer anoPublicacao,
            Integer pagina,
            Integer tamanhoPagina
    ) {
        Specification<Livro> specs = Specification.where((root, query, cb) -> cb.conjunction());

        if (isbn != null && !isbn.isBlank()) {
            specs = specs.and(isbnEqual(isbn));
        }
        if (titulo != null && !titulo.isBlank()) {
            specs = specs.and(tituloLike(titulo));
        }
        if (genero != null) {
            specs = specs.and(generoEqual(genero));
        }
        if (anoPublicacao != null) {
            specs = specs.and(anoPublicacaoEqual(anoPublicacao));
        }
        if (nomeAutor != null && !nomeAutor.isBlank()) {
            specs = specs.and(nomeAutorLike(nomeAutor));
        }

        Pageable pageRequest = PageRequest.of(
                pagina != null ? pagina : 0,
                tamanhoPagina != null ? tamanhoPagina : 10
        );

        return livroRepository.findAll(specs, pageRequest);
    }

    @Transactional
    public void atualizar(Livro livro) {
        if (livro.getId() == null) {
            throw new IllegalArgumentException("Para atualizar, é necessário que o livro já esteja salvo na base.");
        }
        livroValidator.validar(livro);
        livroRepository.save(livro);
    }
}