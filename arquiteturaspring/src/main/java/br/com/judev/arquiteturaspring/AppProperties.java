package br.com.judev.arquiteturaspring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
// RESUMO: diz ao Spring que esta classe é uma configuração/bean gerenciado.
// Com isso, ela entra no ApplicationContext e pode ser injetada em outros lugares.

@ConfigurationProperties(prefix = "app.config")
// RESUMO: habilita o "bind" (mapeamento) automático de propriedades de configuração para campos da classe.
// Tudo que estiver no application.yml/properties começando com "app.config" vai ser mapeado aqui.
// Exemplo em YAML:
// app:
//   config:
//     variavel: "abc"
//     valor1: 10
public class AppProperties {

    private String variavel;
    private Integer valor1;
    // Esses campos representam as propriedades que você espera receber da configuração.
    // O Spring vai preencher com base nos nomes (variavel, valor1) dentro do prefixo app.config.

    public String getVariavel() {
        // Getter usado pelo seu código para ler o valor já carregado da configuração.
        return variavel;
    }

    public void setVariavel(String variavel) {
        // Setter usado pelo Spring durante o binding para atribuir o valor vindo do application.yml/properties.
        this.variavel = variavel;
    }

    public Integer getValor1() {
        // Getter do segundo valor configurável.
        return valor1;
    }

    public void setValor1(Integer valor1) {
        // Setter para o Spring conseguir injetar/bindar o valor.
        this.valor1 = valor1;
    }
}