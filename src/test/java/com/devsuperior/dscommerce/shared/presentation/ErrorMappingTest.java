package com.devsuperior.dscommerce.shared.presentation;

import com.devsuperior.dscommerce.shared.application.ConflictException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Positive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste do {@link GlobalExceptionHandler} <b>isolado</b>: sem subir o Spring Boot, sem banco, sem os controllers
 * reais.
 *
 * <p><b>Como funciona:</b> {@code MockMvcBuilders.standaloneSetup} monta só o necessário para exercitar o MVC — um
 * controller de mentira ({@link ThrowingController}) cujos endpoints existem apenas para <i>lançar</i> a exceção que
 * queremos observar — e o {@code GlobalExceptionHandler} registrado como {@code @ControllerAdvice}. Assim dá para
 * provocar casos difíceis de reproduzir de verdade (como a corrida entre duas requisições que viola a chave única do
 * banco) e checar exatamente a resposta HTTP.
 */
class ErrorMappingTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Monta um MVC mínimo: só o controller de mentira + o handler real que queremos testar.
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ThrowingController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("ConflictException → 409 com a mensagem da exceção")
    void shouldMapConflictExceptionTo409() throws Exception {
        mockMvc.perform(get("/test/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("this e-mail is taken"));
    }

    /**
     * A exceção lançada imita a que o Hibernate/H2 produz de verdade: a mensagem traz o nome da tabela, o índice e o
     * SQL. O teste garante que NADA disso chega ao cliente — a resposta é genérica.
     */
    @Test
    @DisplayName("violação de integridade do banco → 409 genérico, sem vazar SQL")
    void shouldMapDataIntegrityViolationTo409WithoutLeakingSql() throws Exception {
        mockMvc.perform(get("/test/integrity"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("The request conflicts with existing data."))
                .andExpect(content().string(not(containsString("tb_user"))))
                .andExpect(content().string(not(containsString("insert into"))));
    }

    /**
     * Regressão do bug 2.9. A violação é gerada por um método real ({@code Sample.findById}), então o caminho dela é
     * "findById.id" — o formato que o Spring/Hibernate Validator produz de verdade. O handler deve ficar só com
     * "id".
     */
    @Test
    @DisplayName("ConstraintViolationException → 400 listando o campo, sem o nome do método")
    void shouldMapConstraintViolationTo400WithoutMethodName() throws Exception {
        mockMvc.perform(get("/test/validation"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", startsWith("Validation failed: id: ")))
                .andExpect(content().string(not(containsString("findById"))));
    }

    // ------------------------------------------------------------------------------------------------------------
    // Apoio do teste
    // ------------------------------------------------------------------------------------------------------------

    /** Exceção concreta, só para o teste: {@link ConflictException} é abstrata. */
    static class SampleConflictException extends ConflictException {
        SampleConflictException(String message) {
            super(message);
        }
    }

    /** Classe cujo método tem uma restrição de bean validation; serve para gerar uma violação autêntica. */
    static class Sample {
        @SuppressWarnings("unused")
        void findById(@Positive Long id) {
        }
    }

    /** Controller de mentira: cada endpoint apenas lança a exceção que o teste quer observar. */
    @RestController
    static class ThrowingController {

        @GetMapping("/test/conflict")
        void conflict() {
            throw new SampleConflictException("this e-mail is taken");
        }

        @GetMapping("/test/integrity")
        void integrity() {
            // Mensagem no estilo do banco: nomes de tabela e SQL, que jamais devem sair pela API.
            throw new DataIntegrityViolationException(
                    "could not execute statement [Unique index violation: \"UK_USER_EMAIL ON PUBLIC.TB_USER\"; "
                            + "SQL statement: insert into tb_user (email) values (?)]");
        }

        @GetMapping("/test/validation")
        void validation() throws NoSuchMethodException {
            // Pede ao Hibernate Validator para validar os PARÂMETROS de Sample.findById(0L): viola @Positive.
            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
            Method method = Sample.class.getDeclaredMethod("findById", Long.class);
            Set<ConstraintViolation<Sample>> violations = validator.forExecutables()
                    .validateParameters(new Sample(), method, new Object[]{0L});
            throw new ConstraintViolationException(violations);
        }
    }
}
