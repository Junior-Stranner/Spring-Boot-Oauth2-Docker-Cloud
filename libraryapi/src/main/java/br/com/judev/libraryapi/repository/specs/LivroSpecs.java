package br.com.judev.libraryapi.repository.specs;

import br.com.judev.libraryapi.model.GeneroLivro;
import br.com.judev.libraryapi.model.Livro;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class LivroSpecs {
    /*
     * Filtra livros por ISBN exato.
     * SQL aproximado: where livro.isbn = :isbn
     */
    public static Specification<Livro> isbnEqual(String isbn){
        return (root, query, cb) -> cb.equal(root.get("isbn"), isbn);
    }
    /*
     * Filtra livros cujo título contém o texto informado (case-insensitive).
     * Usa UPPER para comparar sem diferenciar maiúsculas/minúsculas.
     *
     * SQL aproximado:
     * where upper(livro.titulo) like '%TEXTO%'
     */
    public static Specification<Livro> tituloLike(String titulo){
        // upper(livro.titulo) like (%:param%)
        return (root, query, cb) ->
                cb.like( cb.upper(root.get("titulo")), "%" + titulo.toUpperCase() + "%");
    }
    /*
     * Filtra por gênero exato.
     * SQL aproximado: where livro.genero = :genero
     */
    public static Specification<Livro> generoEqual(GeneroLivro genero){
        return (root, query, cb) -> cb.equal(root.get("genero"), genero);
    }

    /*
     * Filtra pelo nome do autor (case-insensitive), fazendo JOIN com a tabela/entidade Autor.
     *
     * root.join("autor", JoinType.INNER) cria um INNER JOIN entre Livro e Autor,
     * permitindo filtrar por campos do autor.
     *
     * SQL aproximado:
     * from livro
     * join autor on autor.id = livro.id_autor
     * where upper(autor.nome) like '%NOME%'
     */
    public static Specification<Livro> anoPublicacaoEqual(Integer anoPublicacao){
        // and to_char(data_publicacao, 'YYYY') = :anoPublicacao
        return (root, query, cb) ->
                cb.equal( cb.function("to_char", String.class,
                        root.get("dataPublicacao"), cb.literal("YYYY")),anoPublicacao.toString());
    }

    /*
     * Filtra pelo nome do autor (case-insensitive), fazendo JOIN com a tabela/entidade Autor.
     *
     * root.join("autor", JoinType.INNER) cria um INNER JOIN entre Livro e Autor,
     * permitindo filtrar por campos do autor.
     *
     * SQL aproximado:
     * from livro
     * join autor on autor.id = livro.id_autor
     * where upper(autor.nome) like '%NOME%'
     */
    public static Specification<Livro> nomeAutorLike(String nome){
        return (root, query, cb) -> {
            Join<Object, Object> joinAutor = root.join("autor", JoinType.INNER);
            return cb.like( cb.upper(joinAutor.get("nome")), "%" + nome.toUpperCase() + "%" );
//            return cb.like( cb.upper(root.get("autor").get("nome")), "%" + nome.toUpperCase() + "%" );
        };
    }
}

///*
// * LivroSpecs é uma classe utilitária que cria "pedaços" de filtros (Specifications)
// * para consultas dinâmicas no Spring Data JPA.
// *
// * A ideia é: no service você vai combinando essas specs com .and() / .or()
// * e no final chama livroRepository.findAll(specs, pageable).
// *
// * Cada método abaixo devolve um Specification<Livro>, que nada mais é do que
// * uma função que recebe (root, query, cb) e retorna um Predicate (condição do WHERE).
// *
// * - root: representa a entidade principal consultada (Livro)
// * - query: representa a consulta que está sendo montada
// * - cb: CriteriaBuilder (ajuda a montar as expressões: equal, like, upper, function etc)
// */