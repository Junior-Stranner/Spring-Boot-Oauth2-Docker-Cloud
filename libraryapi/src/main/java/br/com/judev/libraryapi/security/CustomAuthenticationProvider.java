package br.com.judev.libraryapi.security;

import br.com.judev.libraryapi.model.Usuario;
import br.com.judev.libraryapi.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    // Service que busca o usuário no banco (tabela usuario) pelo login
    private final UsuarioService usuarioService;

    // Encoder usado para comparar a senha digitada (texto) com a senha criptografada salva no banco (BCrypt)
    private final PasswordEncoder encoder;

    /**
     * Esse método é chamado pelo Spring Security durante o processo de autenticação.
     * Ex.: quando chega um request com Basic Auth (Authorization: Basic ...) ou formLogin,
     * o Spring monta um Authentication (geralmente UsernamePasswordAuthenticationToken)
     * e passa para os AuthenticationProviders registrados tentarem autenticar.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        // login informado na autenticação (username)
        String login = authentication.getName();

        // senha informada na autenticação (password)
        String senhaDigitada = authentication.getCredentials().toString();

        // busca o usuário na base pelo login
        Usuario usuarioEncontrado = usuarioService.obterPorLogin(login);

        // se não achou usuário, falha a autenticação lançando exceção
        // (o Spring converte isso em 401 Unauthorized normalmente)
        if (usuarioEncontrado == null) {
            throw getErroUsuarioNaoEncontrado();
        }

        // senha criptografada que está salva no banco
        String senhaCriptografada = usuarioEncontrado.getSenha();

        // compara a senha digitada com a criptografada (BCryptPasswordEncoder.matches)
        boolean senhasBatem = encoder.matches(senhaDigitada, senhaCriptografada);

        // se a senha bate, retorna um Authentication "autenticado"
        // aqui você retorna o seu CustomAuthentication, que deve conter as authorities/roles do usuário
        // isso é o que vai parar no SecurityContext e será usado pelo @PreAuthorize / hasRole / etc.
        if (senhasBatem) {
            return new CustomAuthentication(usuarioEncontrado);
        }

        // se usuário existe mas senha não bate, falha a autenticação
        throw getErroUsuarioNaoEncontrado();
    }

    /**
     * Mensagem genérica de erro para não revelar se foi usuário ou senha que errou (boa prática).
     */
    private UsernameNotFoundException getErroUsuarioNaoEncontrado() {
        return new UsernameNotFoundException("Usuário e/ou senha incorretos!");
    }

    /**
     * Diz para o Spring Security quais tipos de Authentication este provider consegue autenticar.
     * Aqui: UsernamePasswordAuthenticationToken (que é o token padrão de username+password,
     * usado pelo Basic Auth e pelo form login).
     *
     * Se esse supports estiver errado, o provider pode ser ignorado e nunca ser chamado.
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}