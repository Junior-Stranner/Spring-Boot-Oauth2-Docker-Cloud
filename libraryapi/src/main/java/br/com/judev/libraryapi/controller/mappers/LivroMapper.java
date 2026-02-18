//package br.com.judev.libraryapi.controller.mappers;
//
//import br.com.judev.libraryapi.controller.dto.CadastroLivroDTO;
//import br.com.judev.libraryapi.controller.dto.ResultadoPesquisaLivroDTO;
//import br.com.judev.libraryapi.model.Livro;
//import br.com.judev.libraryapi.repository.AutorRepository;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.springframework.beans.factory.annotation.Autowired;
//
//@Mapper(componentModel = "spring", uses = AutorMapper.class )
//public abstract class LivroMapper {
//
//    @Autowired
//    AutorRepository autorRepository;
//
//    @Mapping(target = "autor", expression = "java( autorRepository.findById(dto.idAutor()).orElse(null) )")
//    public abstract Livro toEntity(CadastroLivroDTO dto);
//
//    public abstract ResultadoPesquisaLivroDTO toDTO(Livro livro);
//}
