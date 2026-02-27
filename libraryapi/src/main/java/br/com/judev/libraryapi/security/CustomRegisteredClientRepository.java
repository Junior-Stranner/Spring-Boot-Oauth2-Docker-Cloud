package br.com.judev.libraryapi.security;

import br.com.judev.libraryapi.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;


@Component
@RequiredArgsConstructor
public class CustomRegisteredClientRepository implements RegisteredClientRepository {

    /*
     * Esse repositório é usado pelo Spring Authorization Server para localizar um client
     * (aplicação consumidora) durante o fluxo OAuth2.
     *
     * Ex.: quando alguém chama /oauth2/token com client_id/client_secret, o Authorization Server
     * precisa buscar esse client em algum lugar. Por padrão, ele usa um repositório em memória
     * ou JDBC. Aqui você está criando uma implementação custom que busca do seu banco via ClientService.
     */

    private final ClientService clientService;

    /*
     * Configurações que serão aplicadas ao RegisteredClient montado:
     * - tokenSettings: formato do token (JWT self-contained), TTL etc.
     * - clientSettings: consentimento, PKCE obrigatório ou não, etc.
     */
    private final TokenSettings tokenSettings;
    private final ClientSettings clientSettings;

    /*
     * save(): deveria persistir o RegisteredClient (criar/atualizar no banco).
     * No seu código está vazio, então:
     * - nada que o Authorization Server tentar salvar será gravado
     * - você só consegue "ler" clients existentes pelo findByClientId
     */
    @Override
    public void save(RegisteredClient registeredClient) {}

    /*
     * findById(): deveria localizar um client pelo ID interno.
     * Está retornando null, então qualquer busca por id vai falhar.
     * Dependendo do fluxo, isso pode quebrar algumas funcionalidades internas.
     */
    @Override
    public RegisteredClient findById(String id) {
        return null;
    }

    /*
     * findByClientId(): esse é o método mais importante no OAuth2, porque o token endpoint
     * valida o client_id chamando esse método.
     *
     * Fluxo:
     * 1) Recebe clientId (string)
     * 2) Busca no banco: clientService.obterPorClientID(clientId)
     * 3) Se não existir, retorna null (o Authorization Server rejeita o client)
     * 4) Se existir, converte sua entidade/tabela Client em um RegisteredClient do Spring:
     *    - id, clientId, clientSecret, redirectUri, scope
     *    - método de autenticação do client: CLIENT_SECRET_BASIC
     *    - grant types permitidos: authorization_code e client_credentials
     *    - aplica tokenSettings e clientSettings
     */
    @Override
    public RegisteredClient findByClientId(String clientId) {
        var client = clientService.obterPorClientID(clientId);

        if(client == null){
            return null;
        }

        return RegisteredClient
                .withId(client.getId().toString())
                .clientId(client.getClientId())
                .clientSecret(client.getClientSecret())
                .redirectUri(client.getRedirectURI())
                .scope(client.getScope())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .tokenSettings(tokenSettings)
                .clientSettings(clientSettings)
                .build();
    }
}
