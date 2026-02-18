package br.com.judev.libraryapi.validator;

import br.com.judev.libraryapi.exceptions.RegistroDuplicadoException;
import br.com.judev.libraryapi.model.Autor;
import br.com.judev.libraryapi.repository.AutorRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AutorValidator {

    private final AutorRepository autorRepository;

    public AutorValidator(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    public void validar(Autor autor){
        if(existeAutorCadastrado(autor)){
            throw new RegistroDuplicadoException("Autor já cadastrado!");
        }
    }

    private boolean existeAutorCadastrado(Autor autor){
        Optional<Autor> autorEncontrado = autorRepository.findByNomeAndDataNascimentoAndNacionalidade(
                autor.getNome(), autor.getDataNascimento(), autor.getNacionalidade());

        if(autor.getId() == null){
            return autorEncontrado.isPresent() ;
        }
        return !autor.getId().equals(autorEncontrado.get().getId()) && autorEncontrado.isPresent();
    }
}
