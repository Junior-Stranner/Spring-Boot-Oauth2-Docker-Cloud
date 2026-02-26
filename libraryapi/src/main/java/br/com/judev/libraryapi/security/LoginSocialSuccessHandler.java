package br.com.judev.libraryapi.security;

import br.com.judev.libraryapi.model.Usuario;
import br.com.judev.libraryapi.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LoginSocialSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    /*Esse handler padrão do Spring faz o “pós-login”,
    normalmente redirecionando o usuário para a URL que ele tentou acessar antes de logar (saved request).
*/

    private static final String SENHA_PADRAO = "321";

    private final UsuarioService usuarioService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {

//        Fluxo do onAuthenticationSuccess(...)
//        O Spring Security, após o OAuth2 login, entrega um Authentication do tipo OAuth2AuthenticationToken.
//        Dentro dele tem o principal (OAuth2User), com os atributos vindos do Google.

        OAuth2AuthenticationToken auth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = auth2AuthenticationToken.getPrincipal();

        //Esse atributo vem do Google, desde que você esteja pedindo o scope adequado (geralmente email).
        String email = oAuth2User.getAttribute("email");

        //A ideia é: se o usuário já existe na sua tabela usuario, você usa ele; se não existe, cria.
        Usuario usuario = usuarioService.obterPorEmail(email);

        //O método cadastrarUsuarioNaBase:
        //
        //cria um Usuario
        //email = email do Google
        //login = parte antes do @
        //senha = "321" (SENHA_PADRAO)
        //roles = ["OPERADOR"]
        //salva no banco
        //Observação importante: como seu UsuarioService.salvar usa PasswordEncoder,
        // essa senha "321" vai ser salva criptografada (bom). Mas em termos de segurança,
        // não é ideal depender de uma senha padrão; o ideal é marcar esse usuário como “login social”
        // e não permitir login por senha, ou gerar uma senha aleatória forte.
        if(usuario == null){
            usuario = cadastrarUsuarioNaBase(email);
        }
//Aqui é o ponto principal:
//
//Em vez de deixar o usuário autenticado como OAuth2AuthenticationToken (padrão do Spring),
//você cria um CustomAuthentication baseado no seu Usuario do banco, que contém roles e outras infos.
//Isso facilita @PreAuthorize, auditoria, e uso do usuário logado dentro da aplicação.
        authentication = new CustomAuthentication(usuario);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Assim o Spring continua o fluxo normal (redirecionar para a URL salva, etc.),
        // só que agora com o Authentication que você colocou.
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private Usuario cadastrarUsuarioNaBase(String email) {
        Usuario usuario;
        usuario = new Usuario();
        usuario.setEmail(email);

        usuario.setLogin(obterLoginApartirEmail(email));

        usuario.setSenha(SENHA_PADRAO);
        usuario.setRoles(List.of("OPERADOR"));

        usuarioService.salvar(usuario);
        return usuario;
    }

    private String obterLoginApartirEmail(String email) {
        return email.substring(0, email.indexOf("@"));
    }
}

/*
Essa classe é um handler de sucesso de login social (OAuth2).
Ela roda logo depois que o usuário autentica com o Google (ou outro provider OAuth2)
e serve para sincronizar/criar o usuário na sua base e trocar a autenticação padrão do Spring
por uma autenticação sua (CustomAuthentication).

 */
