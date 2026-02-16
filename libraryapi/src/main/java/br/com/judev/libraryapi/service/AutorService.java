package br.com.judev.libraryapi.service;

import br.com.judev.libraryapi.model.Autor;
import br.com.judev.libraryapi.repository.AutorRepository;
import br.com.judev.libraryapi.repository.LivroRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AutorService {
    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;

    public AutorService(LivroRepository livroRepository, AutorRepository autorRepository) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

    public Autor salvar(Autor autor){
        return autorRepository.save(autor);
    }

    public Optional<Autor> obterPorId(UUID id){
        return autorRepository.findById(id);
    }
    public void deletar(UUID id) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Autor não encontrado."));

        boolean temLivros = livroRepository.existsByAutorId(id);

        if (temLivros) {
            throw new RuntimeException("Não é permitido excluir um Autor que possui livros cadastrados!");
        }

        autorRepository.delete(autor);
    }
}
