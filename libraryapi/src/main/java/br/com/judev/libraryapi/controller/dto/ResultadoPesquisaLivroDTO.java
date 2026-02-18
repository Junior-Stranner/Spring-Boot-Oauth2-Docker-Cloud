package br.com.judev.libraryapi.controller.dto;

import br.com.judev.libraryapi.model.GeneroLivro;
import br.com.judev.libraryapi.model.Livro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ResultadoPesquisaLivroDTO(
        UUID id,
        String isbn,
        String titulo,
        LocalDate dataPublicacao,
        GeneroLivro genero,
        BigDecimal preco,
        AutorDTO autor
) {

    public static ResultadoPesquisaLivroDTO toDTO(Livro livro) {
        return new ResultadoPesquisaLivroDTO(
                livro.getId(),
                livro.getIsbn(),
                livro.getTitulo(),
                livro.getDataPublicacao(),
                livro.getGenero(),
                livro.getPreco(),
                livro.getAutor() == null ? null : new AutorDTO(
                        livro.getAutor().getId(),
                        livro.getAutor().getNome(),
                        livro.getAutor().getDataNascimento(),
                        livro.getAutor().getNacionalidade()
                )
        );
    }
}
