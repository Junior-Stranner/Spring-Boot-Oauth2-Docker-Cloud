package br.com.judev.libraryapi.config;

import br.com.judev.libraryapi.security.CustomAuthenticationProvider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final CustomAuthenticationProvider customAuthenticationProvider;

    public SecurityConfiguration(CustomAuthenticationProvider customAuthenticationProvider) {
        this.customAuthenticationProvider = customAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(customAuthenticationProvider)
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers("/login/**").permitAll();
                    authorize.requestMatchers(HttpMethod.POST, "/api/v1/usuarios/**").permitAll();
                    authorize.anyRequest().authenticated();
                })
                .build();
    }
}
  /*  @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}

    // CONFIGURA O PREFIXO ROLE
    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults(){
        return new GrantedAuthorityDefaults("");
    }
}
/*
A estrutura de um SecurityFilterChain no Spring Security é, por dentro, a configuração de uma cadeia (pipeline) de filtros HTTP que intercepta toda requisição antes de chegar nos seus controllers. Você configura essa cadeia usando o objeto HttpSecurity.

A “anatomia” interna é assim:

1) SecurityFilterChain (o que é)
É literalmente uma lista ordenada de filtros (Servlet Filters) que o Spring Security executa para cada request, por exemplo:

carregar/atualizar o SecurityContext (usuário autenticado)
validar credenciais (Basic, form login, JWT, etc.)
decidir se o endpoint pode ser acessado (autorização)
bloquear por CSRF, CORS, etc.
O Spring registra isso no container via springSecurityFilterChain e o DispatcherServlet só chama seu controller depois que essa cadeia é processada.

2) @Bean public SecurityFilterChain securityFilterChain(HttpSecurity http)
Esse método cria e registra a chain no Spring.

HttpSecurity é um builder (um configurador fluente) que você usa para dizer:
quais regras de autenticação vão existir
quais regras de autorização
quais proteções ficam ligadas/desligadas
e quais filtros entram na cadeia e em qual ordem (o framework cuida disso)
3) Partes principais dentro do HttpSecurity
a) csrf(...)
java
Copiar código
http.csrf(csrf -> csrf.disable())
CSRF protege contra ataques em aplicações com sessão e cookies (browser).
Em API REST stateless (Postman/Token), geralmente desabilitam.
Quando ligado, requests POST/PUT/DELETE precisam de token CSRF.
b) authorizeHttpRequests(...) (autorização)
java
Copiar código
http.authorizeHttpRequests(auth -> auth
    .requestMatchers("/public/**").permitAll()
    .anyRequest().authenticated()
)
Aqui você define quem pode acessar o quê.

requestMatchers(...): aplica regras por rota/método
permitAll(): libera sem login
authenticated(): precisa estar autenticado
também existe hasRole("ADMIN"), hasAuthority(...), etc.
O resultado disso vira um filtro de autorização (hoje baseado em AuthorizationManager) que decide antes de chegar no controller.

c) httpBasic(...) (autenticação Basic)
java
Copiar código
http.httpBasic(basic -> {})
Habilita o filtro de Basic Auth:

o cliente manda Authorization: Basic base64(user:pass)
o Spring tenta autenticar e, se der certo, coloca o usuário no SecurityContext.
d) formLogin(...) (login via formulário)
java
Copiar código
http.formLogin(form -> {})
Habilita login “de browser”:

se o usuário não estiver logado e acessar uma página protegida, ele é redirecionado para uma tela de login
isso faz sentido para apps web tradicionais (não para API)
e) sessionManagement(...)
java
Copiar código
http.sessionManagement(sm ->
    sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
Define como o Spring usa sessão HTTP:

STATELESS: não cria sessão (comum para API com token/basic)
IF_REQUIRED: cria se precisar (comum em form login)
f) exceptionHandling(...)
Configura o que acontece quando:

não autenticado (401)
não autorizado (403) Você consegue customizar o JSON de erro aqui.
4) @Order e múltiplas chains
Se você tiver mais de um SecurityFilterChain, o Spring escolhe qual aplicar assim:

Ele tenta a chain 1, depois a 2, etc (por ordem)
A primeira cujo matcher “combinar” com a request é usada
Por isso @Order só importa com múltiplas chains.

Exemplo mental:

Chain 1: protege /api/** com JWT
Chain 2: protege o resto com formLogin
5) http.build()
java
Copiar código
return http.build();
Aqui o builder “fecha” a configuração:

ele monta os filtros com base no que você habilitou
define a ordem correta
devolve o SecurityFilterChain pronto para o Spring registrar

 */