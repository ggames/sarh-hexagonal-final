package com.fich.sarh.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.fich.sarh", importOptions = {ImportOption.DoNotIncludeTests.class})
public class HexagonalArchitectureTest {

    @ArchTest
    static final ArchRule domainMustNotDependOnInfrastructure =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..infrastructure..");

    @ArchTest
    static final ArchRule applicationMustNotDependOnInfrastructure =
            noClasses()
                    .that().resideInAPackage("..application..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..infrastructure..");

    @ArchTest
    static final ArchRule domainMustNotDependOnApplication =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..application..");

    @ArchTest
    static final ArchRule domainMustNotDependOnSpringOrJpa =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("org.springframework..", "jakarta.persistence..");

    @ArchTest
    static final ArchRule domainModelsMustNotDependOnSpring =
            noClasses()
                    .that().resideInAPackage("..domain.model..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("org.springframework..");

    @ArchTest
    static final ArchRule domainModelsMustNotDependOnPersistence =
            noClasses()
                    .that().resideInAPackage("..domain.model..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("jakarta.persistence..");

    @ArchTest
    static final ArchRule controllersShouldBeInRestPackage =
            classes()
                    .that().areAnnotatedWith(RestController.class)
                    .should().resideInAPackage("..infrastructure..rest..");

    @ArchTest
    static final ArchRule controllersMustNotDependOnSpiPorts =
            noClasses()
                    .that().areAnnotatedWith(RestController.class)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..ports.output..", "..ports.outbound..");

    @ArchTest
    static final ArchRule controllersMustNotDependOnRepositories =
            noClasses()
                    .that().areAnnotatedWith(RestController.class)
                    .should().dependOnClassesThat()
                    .resideInAPackage("..repository..");
}
