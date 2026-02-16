package br.com.judev.libraryapi.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    @Value("${spring.datasource.url}")
    String url;
    @Value("${spring.datasource.username}")
    String username;
    @Value("${spring.datasource.password}")
    String password;
    @Value("${spring.datasource.driver-class-name}")
    String driver;

    //    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName(driver);
        return ds;
    }

    /**
     * configuracao Hikary
     * https://github.com/brettwooldridge/HikariCP
     * @return
     */
    @Bean
    public DataSource hikariDataSource(){

        HikariConfig config = new HikariConfig();
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driver);
        config.setJdbcUrl(url);

        config.setMaximumPoolSize(10); // maximo de conexões liberadas
        config.setMinimumIdle(1); // tamanho inicial do pool
        config.setPoolName("library-db-pool");
        config.setMaxLifetime(600000); // 600 mil ms (10 minutos)
        config.setConnectionTimeout(100000); // timeout para conseguir uma conexão
        config.setConnectionTestQuery("select 1"); // query de teste

        return new HikariDataSource(config);
    }
}
/*
 * DataSource é o “provedor de conexões” do JDBC dentro da aplicação.
 *
 * Para que serve:
 * - Centraliza as configurações de acesso ao banco (URL, usuário, senha, driver).
 * - Entrega Connections para o Spring/JPA/JdbcTemplate sempre que o app precisa acessar o BD.
 * - Pode gerenciar um pool de conexões (reutilização), evitando abrir/fechar conexão a cada request,
 *   o que melhora desempenho e estabilidade.
 *
 * Neste arquivo existem duas opções:
 * 1) DriverManagerDataSource (comentado): simples, normalmente sem pool; mais usado em testes/demos.
 * 2) HikariDataSource (ativo): usa HikariCP, um pool de conexões rápido e recomendado pelo Spring Boot.
 *
 * O pool mantém um conjunto de conexões abertas:
 * - maximumPoolSize: limite máximo de conexões simultâneas no pool
 * - minimumIdle: mínimo de conexões ociosas prontas para uso
 * - connectionTimeout: tempo máximo esperando uma conexão livre
 * - maxLifetime: tempo máximo de vida de uma conexão antes de ser reciclada
 * - connectionTestQuery: query simples para validar se a conexão está ok
 */