package br.com.judev.arquiteturaspring;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

@SpringBootApplication
// RESUMO: Marca esta classe como a "entrada" do Spring Boot.
// O Spring Boot vai usar isso para: fazer component scan, habilitar auto-configurações
// e subir todo o contexto (beans, configs, etc.).

@EnableConfigurationProperties
// RESUMO: Habilita o uso de classes de configuração com @ConfigurationProperties (ex.: AppProperties),
// permitindo que valores do application.yml/properties sejam mapeados para objetos Java.
public class ArquiteturaspringApplication {

    public static void main(String[] args) {
        // SpringApplication.run(ArquiteturaspringApplication.class, args);
        // Essa seria a forma "padrão" de subir o Spring Boot.
        // Aqui você optou por usar SpringApplicationBuilder para ter mais controle da inicialização.

        SpringApplicationBuilder builder =
                new SpringApplicationBuilder(ArquiteturaspringApplication.class);
        // Cria um "builder" para configurar como a aplicação Spring Boot vai iniciar
        // (perfis, banner, lazy init, propriedades, etc.).

        builder.bannerMode(Banner.Mode.OFF);
        // Desliga o banner do Spring Boot no console (apenas estética/limpeza de log).

        builder.profiles("producao", "homologacao");
        // Define perfis ativos (profiles).
        // Isso faz o Spring carregar configurações específicas, por exemplo:
        // application-producao.yml e application-homologacao.yml (se existirem),
        // além do application.yml padrão.
        // Observação: a ordem e como as propriedades se sobrepõem depende do Spring Boot,
        // mas em geral profiles influenciam quais arquivos e beans condicionais são carregados.

        // builder.lazyInitialization(true);
        // Se ativar, o Spring tenta criar beans apenas quando forem usados (lazy),
        // o que pode acelerar startup, mas pode "jogar erros" para mais tarde (quando o bean for acessado).

        builder.run(args);
        // Inicia a aplicação: cria o contexto do Spring, carrega configurações,
        // registra beans, sobe auto-configurações, etc.
        // A partir daqui, a aplicação está "rodando" com o container ativo.

        // contexto da aplicação já iniciada:
        ConfigurableApplicationContext applicationContext = builder.context();
        // Pega o ApplicationContext (o "container" do Spring já inicializado),
        // que é onde ficam registrados e gerenciados todos os beans.

        // var produtoRepository = applicationContext.getBean("produtoRepository");
        // Exemplo de como buscar um bean pelo nome manualmente (não recomendado no dia a dia),
        // mas útil para testes/demonstrações/inspeção.

        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        // Pega o Environment: camada do Spring que acessa propriedades/configurações
        // vindas de application.yml, variáveis de ambiente, argumentos de linha de comando, etc.

        String applicationName = environment.getProperty("spring.application.name");
        // Lê uma propriedade específica de configuração.

        System.out.println("Nome da aplicação: " + applicationName);
        // Mostra no console o nome configurado.

        ExemploValue value = applicationContext.getBean(ExemploValue.class);
        // Pega um bean do tipo ExemploValue que foi registrado via @Component.

        value.imprimirVarivel();
        // Executa um método que imprime uma variável injetada com @Value.

        AppProperties properties = applicationContext.getBean(AppProperties.class);
        // Pega o bean de propriedades (mapeado via @ConfigurationProperties, assumindo que AppProperties é isso).

        System.out.println(properties.getValor1());
        // Mostra um valor carregado do arquivo de configuração e mapeado para o objeto AppProperties.
    }
}


/*
Resumo geral: essa main está inicializando o Spring Boot de forma customizada
 (via SpringApplicationBuilder), ativando perfis, subindo o contexto e depois acessando
 o container para ler propriedades do ambiente e buscar beans (ExemploValue, AppProperties)
 para demonstrar injeção/configuração.

Se você colar sua classe AppProperties, eu comento ela no mesmo estilo e confirmo se o
@EnableConfigurationProperties está do jeito mais adequado (às vezes dá pra simplificar).
 */