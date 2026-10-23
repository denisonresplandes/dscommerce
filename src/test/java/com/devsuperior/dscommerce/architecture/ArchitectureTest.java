package com.devsuperior.dscommerce.architecture;

import com.devsuperior.dscommerce.orders.domain.OrderStatus;
import com.devsuperior.dscommerce.shared.domain.DomainException;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.equivalentTo;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * <b>Testes de arquitetura</b>: as regras do ADR 0001 escritas como código.
 *
 * <p><b>Por que isso existe?</b> Uma regra de arquitetura que vive só num documento é esquecida na primeira
 * correria ("é só um import rapidinho"). Aqui o ArchUnit lê o bytecode do projeto e <b>quebra o build</b> quando
 * alguém viola uma regra — a arquitetura passa a ser verificada tão automaticamente quanto qualquer teste.
 *
 * <p>Cada regra carrega uma mensagem ({@code .because(...)}) explicando o motivo, para que a falha ensine em vez de
 * só reprovar.
 *
 * <p>Convenção dos padrões de pacote: {@code ..domain..} significa "qualquer pacote que contenha o segmento
 * {@code domain}" (ex.: {@code users.domain}, {@code orders.domain.vo}, {@code shared.domain}).
 */
@AnalyzeClasses(
        packages = "com.devsuperior.dscommerce",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class ArchitectureTest {

    private static final String BASE = "com.devsuperior.dscommerce";

    // ---------------------------------------------------------------------------------------------------------
    // 1. Direção das dependências entre camadas:  presentation → application → domain ← infrastructure
    // ---------------------------------------------------------------------------------------------------------

    @ArchTest
    static final ArchRule domain_is_independent_of_frameworks =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "org.springframework..", "jakarta..", "org.hibernate..", "com.fasterxml..")
                    .because("o domínio é Java puro: sem Spring, JPA, web ou JSON; assim as regras de negócio "
                            + "são testáveis sem subir nada e sobrevivem a trocas de framework");

    @ArchTest
    static final ArchRule domain_does_not_know_outer_layers =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..application..", "..presentation..", "..infrastructure..")
                    .because("a dependência aponta para dentro: o domínio não conhece quem o usa");

    /**
     * Estas duas exceções são conscientes e estão documentadas no ADR (seção 2.2):
     * <ul>
     *   <li>{@code OrderStatus}: enum de domínio que a {@code OrderView} (aplicação) expõe e a resposta HTTP serializa.</li>
     *   <li>{@code DomainException}: o {@code GlobalExceptionHandler} existe justamente para traduzi-la em HTTP 422.</li>
     * </ul>
     */
    private static final DescribedPredicate<JavaClass> DOMAIN_TYPES_PRESENTATION_MAY_USE =
            equivalentTo(OrderStatus.class).or(equivalentTo(DomainException.class));

    @ArchTest
    static final ArchRule presentation_talks_only_to_application =
            noClasses().that().resideInAPackage("..presentation..")
                    .should().dependOnClassesThat(
                            resideInAPackage("..domain..").and(not(DOMAIN_TYPES_PRESENTATION_MAY_USE))
                                    .or(resideInAPackage("..infrastructure..")))
                    .because("controllers só chamam services da camada de aplicação; nada de agregados, "
                            + "repositórios ou adapters direto na camada web");

    @ArchTest
    static final ArchRule application_does_not_depend_on_outer_layers_or_technology =
            noClasses().that().resideInAPackage("..application..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..infrastructure..", "..presentation..",
                            "jakarta.persistence..", "org.springframework.data..", "org.springframework.web..")
                    .because("a aplicação orquestra casos de uso através de ports; JPA e web são detalhes de "
                            + "infraestrutura/apresentação (@Service e @Transactional são permitidos)");

    @ArchTest
    static final ArchRule infrastructure_does_not_depend_on_presentation =
            noClasses().that().resideInAPackage("..infrastructure..")
                    .should().dependOnClassesThat().resideInAPackage("..presentation..")
                    .because("persistência e segurança não devem conhecer a camada web");

    @ArchTest
    static final ArchRule nobody_outside_infrastructure_uses_infrastructure =
            noClasses().that().resideOutsideOfPackage("..infrastructure..")
                    .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
                    .because("a infraestrutura implementa ports e é ligada pelo Spring; nenhum outro código "
                            + "deve referenciá-la diretamente (inversão de dependência)");

    @ArchTest
    static final ArchRule nobody_outside_presentation_uses_presentation =
            noClasses().that().resideOutsideOfPackage("..presentation..")
                    .should().dependOnClassesThat().resideInAPackage("..presentation..")
                    .because("a camada web é a borda do sistema; ninguém depende dela");

    // ---------------------------------------------------------------------------------------------------------
    // 2. Fronteiras entre módulos (users × orders) e shared kernel
    // ---------------------------------------------------------------------------------------------------------

    @ArchTest
    static final ArchRule users_does_not_depend_on_orders =
            noClasses().that().resideInAPackage(BASE + ".users..")
                    .should().dependOnClassesThat().resideInAPackage(BASE + ".orders..")
                    .because("a dependência entre módulos é de mão única: orders consome users, nunca o contrário");

    @ArchTest
    static final ArchRule orders_sees_only_the_public_api_of_users =
            noClasses().that().resideInAPackage(BASE + ".orders..")
                    .should().dependOnClassesThat(
                            resideInAPackage(BASE + ".users..").and(not(resideInAPackage(BASE + ".users.api.."))))
                    .because("users.api é o único contrato público do módulo; todo o resto é interno e pode mudar");

    @ArchTest
    static final ArchRule only_the_acl_touches_the_users_module =
            noClasses().that().resideInAPackage(BASE + ".orders..")
                    .and().resideOutsideOfPackage(BASE + ".orders.infrastructure.acl..")
                    .should().dependOnClassesThat().resideInAPackage(BASE + ".users..")
                    .because("a tradução entre os vocabulários de orders e users acontece num único lugar: "
                            + "o Anti-Corruption Layer");

    @ArchTest
    static final ArchRule shared_kernel_does_not_depend_on_modules =
            noClasses().that().resideInAPackage(BASE + ".shared..")
                    .should().dependOnClassesThat().resideInAnyPackage(BASE + ".users..", BASE + ".orders..")
                    .because("o núcleo compartilhado deve poder ser usado por todos sem conhecer nenhum módulo");

    @ArchTest
    static final ArchRule modules_are_free_of_cycles =
            slices().matching(BASE + ".(*)..")
                    .should().beFreeOfCycles()
                    .because("ciclos entre módulos impedem entendê-los, testá-los e evoluí-los separadamente");

    // ---------------------------------------------------------------------------------------------------------
    // 3. Cada tipo de classe no seu lugar
    // ---------------------------------------------------------------------------------------------------------

    @ArchTest
    static final ArchRule controllers_live_in_presentation =
            classes().that().areAnnotatedWith(RestController.class)
                    .should().resideInAPackage("..presentation..");

    @ArchTest
    static final ArchRule services_live_in_application =
            classes().that().areAnnotatedWith(Service.class)
                    .should().resideInAPackage("..application..");

    @ArchTest
    static final ArchRule jpa_entities_live_in_infrastructure_persistence =
            classes().that().areAnnotatedWith(jakarta.persistence.Entity.class)
                    .should().resideInAPackage("..infrastructure.persistence..")
                    .because("entidades JPA são o modelo de persistência, separado do modelo de domínio");

    @ArchTest
    static final ArchRule repository_adapters_live_in_infrastructure =
            classes().that().areAnnotatedWith(org.springframework.stereotype.Repository.class)
                    .should().resideInAPackage("..infrastructure..");
}
