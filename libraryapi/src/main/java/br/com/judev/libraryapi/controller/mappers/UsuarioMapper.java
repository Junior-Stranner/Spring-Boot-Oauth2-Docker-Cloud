package br.com.judev.libraryapi.controller.mappers;

import br.com.judev.libraryapi.controller.dto.UsuarioDTO;
import br.com.judev.libraryapi.model.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    Usuario toEntity(UsuarioDTO dto);
}
