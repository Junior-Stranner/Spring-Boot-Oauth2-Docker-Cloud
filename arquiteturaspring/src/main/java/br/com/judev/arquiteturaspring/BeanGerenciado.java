package br.com.judev.arquiteturaspring;

import br.com.judev.arquiteturaspring.todos.TodoEntity;
import br.com.judev.arquiteturaspring.todos.TodoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Lazy
// Faz com que o Spring crie este bean "sob demanda" (quando alguém realmente precisar/injetar),
// e não na inicialização da aplicação. Útil para reduzir tempo de startup ou evitar ciclos.

@Component
// Registra a classe no container do Spring como um bean gerenciado.
// A partir daí, o Spring pode instanciar, guardar e injetar este objeto em outros lugares.

@Scope(BeanDefinition.SCOPE_SINGLETON)
// Define o escopo do bean. Singleton = 1 instância para toda a aplicação (padrão do Spring).
// Em web apps, isso significa que a mesma instância pode ser compartilhada por várias requisições/usuários,
// então NÃO é um bom lugar para guardar estado por usuário (ex.: idUsuarioLogado) como campo mutável.
// Os exemplos comentados (request/session/application) são outros escopos possíveis em apps web.
//
// @Scope(WebApplicationContext.SCOPE_APPLICATION) -> parecido com singleton no contexto web
// @Scope("request")  -> 1 instância por requisição HTTP
// @Scope("session")  -> 1 instância por sessão do usuário
// @Scope("application") -> escopo de aplicação web (varia conforme configuração)
public class BeanGerenciado {

    private String idUsuarioLogado;
    // Campo de estado. Se este bean for singleton (como está), isso pode causar problema de concorrência:
    // diferentes usuários/requisições podem sobrescrever esse valor. Em geral, evita-se guardar isso em singleton.

    @Autowired
    private TodoValidator validator;
    // Injeção por campo (field injection).
    // O Spring vai colocar aqui uma instância de TodoValidator automaticamente.
    // É simples, mas normalmente não é a abordagem mais recomendada (testes e imutabilidade ficam piores).

    @Autowired
    public BeanGerenciado(TodoValidator validator) {
        this.validator = validator;
    }
    // Injeção por construtor (constructor injection).
    // Aqui o Spring chama esse construtor e fornece o TodoValidator.
    // Essa é a forma mais recomendada porque o objeto já nasce com a dependência obrigatória.

    public void utilizar() {
        var todo = new TodoEntity();
        // Cria uma entidade Todo "vazia" só para exemplificar o uso.

        validator.validar(todo);
        // Usa o validador injetado para validar o todo.
        // Isso mostra o bean "utilizando" uma dependência gerenciada pelo Spring.
    }

    @Autowired
    public void setValidator(TodoValidator validator) {
        this.validator = validator;
    }
    // Injeção por setter (setter injection).
    // O Spring pode chamar esse método e setar a dependência depois de construir o objeto.
    // É útil quando a dependência é opcional ou quando você quer permitir troca,
    // mas pode deixar o objeto "meio configurado" se não for bem controlado.
}