package com.example;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import org.junit.jupiter.api.Test;

//ArchUnit : 애플리케이션의 아키텍처가 정해진 룰을 지키고 있는지 테스트
//@AnalyzeClasses(packagesOf = App.class) //App.class의 위치를 기준으로 한다.
@AnalyzeClasses(packages = "com.example.step2")
public class ArchTests {

    //애노테이션을 활용하는 방법법
    @ArchTest
    ArchRule domainPackageRule = ArchRuleDefinition.classes().that().resideInAPackage("..domain..")
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage("..study..","..member..","..domain..");

    @ArchTest
    ArchRule memberPackageRule = ArchRuleDefinition.noClasses().that().resideInAPackage("..domain..")
            .should().accessClassesThat().resideInAnyPackage("..member..");

    @ArchTest
    ArchRule studyPackageRule = ArchRuleDefinition.noClasses().that().resideOutsideOfPackage("..study..")
            .should().accessClassesThat().resideInAPackage("..study..");

    @ArchTest
    ArchRule freeOfCycles = SlicesRuleDefinition.slices().matching("..step2.(*)..")
            .should().beFreeOfCycles();

    @Test
    void packageDependencyTests() {
        JavaClasses classes = new ClassFileImporter().importPackages("com.example.step2");
        /**
         * TODO ..domain.. 패키지에 있는 클래스는 ..study.., ..member.., ..domain에서 참조 가능.
         * TODO ..member.. 패키지에 있는 클래스는 ..study..와 ..member..에서만 참조 가능.
         * TODO(반대로) ..domain.. 패키지는 ..member.. 패키지를 참조하지 못한다.
         * TODO ..study.. 패키지에 있는 클래스는 ..study.. 에서만 참조 가능.
         * TODO 순환 참조 없어야 한다.
         */

        ArchRule domainPackageRule = ArchRuleDefinition.classes().that().resideInAPackage("..domain..")
                .should().onlyBeAccessed().byClassesThat().resideInAnyPackage("..study..","..member..","..domain..");
        domainPackageRule.check(classes);


        ArchRule memberPackageRule = ArchRuleDefinition.noClasses().that().resideInAPackage("..domain..")
                .should().accessClassesThat().resideInAnyPackage("..member..");
        domainPackageRule.check(classes);

        ArchRule studyPackageRule = ArchRuleDefinition.noClasses().that().resideOutsideOfPackage("..study..")
                .should().accessClassesThat().resideInAPackage("..study..");
        domainPackageRule.check(classes);

        ArchRule freeOfCycles = SlicesRuleDefinition.slices().matching("..step2.(*)..")
                .should().beFreeOfCycles();
        freeOfCycles.check(classes);
    }
}
