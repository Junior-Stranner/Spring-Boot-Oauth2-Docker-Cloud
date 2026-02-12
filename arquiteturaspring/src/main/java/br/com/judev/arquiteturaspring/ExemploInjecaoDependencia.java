package br.com.judev.arquiteturaspring;

import br.com.judev.arquiteturaspring.todos.MailSender;
import br.com.judev.arquiteturaspring.todos.TodoRepository;
import br.com.judev.arquiteturaspring.todos.TodoService;
import br.com.judev.arquiteturaspring.todos.TodoValidator;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * RESUMO GERAL:
 * Esta classe existe para demonstrar o conceito de "injeção de dependência" sem usar o Spring rodando.
 * Aqui você mesmo cria os objetos (new ...) e passa as dependências pelo construtor, simulando o papel do Spring.
 * Importante: do jeito que está, várias dependências estão como null, então é um exemplo didático e não um fluxo real funcionando.
 */
public class ExemploInjecaoDependencia {

    public static void main(String[] args) throws SQLException {

        // Cria um DataSource "simples" (fonte de conexões) manualmente.
        // Em aplicações Spring reais, isso normalmente vem configurado via application.yml e gerenciado pelo container.
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("url");          // Placeholder: aqui deveria ser uma URL JDBC real.
        dataSource.setUsername("user");    // Placeholder: usuário do banco.
        dataSource.setPassword("password");// Placeholder: senha do banco.

        // Abre uma conexão JDBC usando o DataSource.
        // Neste exemplo a conexão é criada apenas para ilustrar como seria, mas não é usada depois.
        Connection connection = dataSource.getConnection();

        // Declara um EntityManager (JPA), mas deixa como null.
        // Em um projeto Spring com JPA de verdade, o Spring criaria e injetaria isso automaticamente.
        EntityManager entityManager = null;

        // Declara o repositório (camada de acesso a dados), mas deixa como null.
        // Em um app real com Spring Data JPA, o Spring geraria a implementação e injetaria aqui.
        TodoRepository repository = null; // Exemplo incompleto propositalmente.

        // Cria o validador já recebendo o repositório como dependência.
        // A ideia é mostrar que o TodoValidator "depende" do repository e não deveria criá-lo internamente.
        TodoValidator todoValidator = new TodoValidator(repository);

        // Cria um componente responsável por envio de e-mail/notificação.
        // Aqui ele não recebe dependências, então pode ser instanciado diretamente.
        MailSender sender = new MailSender();

        // Cria o serviço (regra de negócio) recebendo tudo o que ele precisa para operar.
        // Esse é o ponto principal do exemplo: o serviço não cria dependências, ele as recebe prontas.
        TodoService todoService = new TodoService(repository, todoValidator, sender);

        // Comentários abaixo sugerem a alternativa de injeção via setter (ao invés de construtor).
        // A mensagem é: você pode "setar" dependências depois, mas isso pode deixar o objeto incompleto
        // e aumenta a chance de erro (ex.: esquecer de setar algo).
//        BeanGerenciado beanGerenciado = new BeanGerenciado(null);
//        beanGerenciado.setValidator(todoValidator);
//        if(codicao == true){
//            beanGerenciado.setValidator();
//        }
    }
}