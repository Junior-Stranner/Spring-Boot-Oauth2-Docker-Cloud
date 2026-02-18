package br.com.judev.libraryapi.service;

import br.com.judev.libraryapi.exceptions.RegistroDuplicadoException;
import br.com.judev.libraryapi.model.Autor;
import br.com.judev.libraryapi.model.Livro;
import br.com.judev.libraryapi.repository.AutorRepository;
import br.com.judev.libraryapi.repository.LivroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class LivroService {

    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;

    public LivroService(LivroRepository livroRepository, AutorRepository autorRepository) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

    @Transactional
    public Livro salvar(Livro livro) {
        if (livroRepository.existsByIsbn(livro.getIsbn())) {
            throw new RegistroDuplicadoException("Já existe livro cadastrado com o ISBN: " + livro.getIsbn());
        }
        if (livro.getAutor() == null || livro.getAutor().getId() == null) {
            throw new RuntimeException("Autor é obrigatório.");
        }

        UUID idAutor = livro.getAutor().getId();
        Autor autor = autorRepository.findById(idAutor)
                .orElseThrow(() -> new RuntimeException("Autor não encontrado para o id: " + idAutor));

        // garante que o livro aponte para um Autor gerenciado pelo JPA
        livro.setAutor(autor);
        return livroRepository.save(livro);
    }
}
