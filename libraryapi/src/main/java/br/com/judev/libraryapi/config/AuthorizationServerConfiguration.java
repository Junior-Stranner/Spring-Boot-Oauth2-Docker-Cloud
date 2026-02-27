package br.com.judev.libraryapi.config;

import br.com.judev.libraryapi.security.CustomAuthentication;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class AuthorizationServerConfiguration {

    @Bean
    @Order(1)
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {

        // endpoints padrões do Authorization Server (/oauth2/token, /oauth2/authorize, /oauth2/jwks, etc.)
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        // habilita OpenID Connect (/.well-known/openid-configuration, etc.)
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());

        // habilita validação de JWT para endpoints protegidos (resource server)
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        // login form para o fluxo de authorization_code (redireciona para /login)
        http.formLogin(form -> form.loginPage("/login"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public TokenSettings tokenSettings(){
        return TokenSettings.builder()
                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                .accessTokenTimeToLive(Duration.ofMinutes(60))
                .build();
    }

    @Bean
    public ClientSettings clientSettings(){
        return ClientSettings.builder()
                .requireAuthorizationConsent(false)
                .build();
    }

    // Gera par de chaves RSA em memória (toda vez que reiniciar o app, muda a chave)
    private RSAKey gerarChaveRSA() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        RSAPublicKey chavePublica = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey chavePrivada = (RSAPrivateKey) keyPair.getPrivate();

        return new RSAKey
                .Builder(chavePublica)
                .privateKey(chavePrivada)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    // Decoder de JWT usado pelo resource server / auth server
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource){
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    // Bean que fornece as chaves (JWK) usadas para assinar os JWTs
    @Bean
    public JWKSource<SecurityContext> jwkSource() throws Exception {
        RSAKey rsaKey = gerarChaveRSA();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings(){
        return AuthorizationServerSettings.builder()
                // obter token
                .tokenEndpoint("/oauth2/token")
                // para consultar status do token
                .tokenIntrospectionEndpoint("/oauth2/introspect")
                // revogar
                .tokenRevocationEndpoint("/oauth2/revoke")
                // authorization endpoint
                .authorizationEndpoint("/oauth2/authorize")
                // informacoes do usuario OPEN ID CONNECT
                .oidcUserInfoEndpoint("/oauth2/iserinfo")
                // obter a chave publica pra verificar a asstinatura do token
                .jwkSetEndpoint("/oauth2/jwks")
                // logout
                .oidcLogoutEndpoint("/oauth2/logout")
                .build();
    }
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(){
        return context -> {
            var principal = context.getPrincipal();

            if(principal instanceof CustomAuthentication authentication){
                OAuth2TokenType tipoToken = context.getTokenType();

                if(OAuth2TokenType.ACCESS_TOKEN.equals(tipoToken)){
                    Collection<GrantedAuthority> authorities = authentication.getAuthorities();
                    List<String> authoritiesList =
                            authorities.stream().map(GrantedAuthority::getAuthority).toList();

                    context
                            .getClaims()
                            .claim("authorities", authoritiesList)
                            .claim("email", authentication.getUsuario().getEmail());
                }
            }

        };
    }
}
