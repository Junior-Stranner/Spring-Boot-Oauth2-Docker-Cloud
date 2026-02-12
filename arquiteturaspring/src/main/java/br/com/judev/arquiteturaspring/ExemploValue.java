package br.com.judev.arquiteturaspring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExemploValue {
    // RESUMO GERAL:
    // Esta classe mostra como o Spring injeta valores de configuração (application.properties/yml)
    // dentro de um bean gerenciado, usando @Value. Assim você evita "hardcode" e centraliza configs.

    @Value("${app.config.variavel}")
    private String variavel;
    // O que faz:
    // - Diz ao Spring: "pegue o valor da propriedade app.config.variavel e coloque aqui".
    // Por quê:
    // - Para ler configurações externas (ambiente, arquivos de config, secrets etc.) sem fixar no código.
    // Observação:
    // - Se a chave não existir e não houver valor padrão, a aplicação pode falhar ao subir.

    public void imprimirVarivel(){
        // Imprime no console o valor que foi injetado pelo Spring.
        // Isso só funciona se esta classe estiver sendo criada pelo Spring (bean) e o contexto estiver rodando.
        System.out.println(variavel);
    }
}