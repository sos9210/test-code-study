package com.example.step3.study;

import com.example.step2.domain.Member;
import com.example.step2.domain.Study;
import com.example.step2.domain.StudyStatus;
import com.example.step2.member.MemberService;
import com.example.step2.study.StudyRepository;
import com.example.step2.study.StudyService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Testcontainers
@Slf4j
@ContextConfiguration(initializers = StudyServiceTest.ContainerPropertyInitializer.class)
class StudyServiceTest {

    @Mock
    MemberService memberService;

    @Autowired
    Environment environment;

    @Value("${container.port}") int port;

    @Autowired
    StudyRepository studyRepository;

    //static 키워드가 붙으면 모든 테스트에서 컨테이너를 공유한다. static키워드가 없으면 테스`트케이스마다 컨테이너를 띄우고 종료한다
    /*
    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer()
            .withDatabaseName("studytest"); //db이름을 줄 수 있다.
    */
    //도커 이미지 이름으로 컨테이너를 생성하는것도 가능하다.(로컬에 없으면 도커 원격저장소에서 불러온다)
    @Container
    static GenericContainer genericPostgreSQLContainer = new GenericContainer("postgres")
            .withExposedPorts(5432).withEnv("POSTGRES_HOST_AUTH_METHOD","trust")
            .withEnv("POSTGRES_DB","studytest");

    @BeforeAll
    static void beforeAll() {
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(log);
        //컨테이너 안에있는 로그를 출력해준다.
        genericPostgreSQLContainer.followOutput(logConsumer);
    }
    @BeforeEach
    void beforeEach() {
        System.out.println("====================================================");
        //System.out.println(genericPostgreSQLContainer.getMappedPort(5432));

        //스프링을 통해 컨테이너 정보가 출력된다
        System.out.println(environment.getProperty("container.port"));
        System.out.println(port);
        System.out.println("====================================================");
        studyRepository.deleteAll();
    }

    @Test
    void createNewStudy() {
        // Given
        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);

        Member member = new Member();
        member.setId(1L);
        member.setEmail("keesun@email.com");

        Study study = new Study(10, "테스트");

        given(memberService.findById(1L)).willReturn(Optional.of(member));

        // When
        studyService.createNewStudy(1L, study);

        // Then
        assertEquals(1L, study.getOwnerId());
        then(memberService).should(times(1)).notify(study);
        then(memberService).shouldHaveNoMoreInteractions();
    }

    @DisplayName("다른 사용자가 볼 수 있도록 스터디를 공개한다.")
    @Test
    void openStudy() {
        // Given
        StudyService studyService = new StudyService(memberService, studyRepository);
        Study study = new Study(10, "더 자바, 테스트");
        assertNull(study.getOpenedDateTime());

        // When
        studyService.openStudy(study);

        // Then
        assertEquals(StudyStatus.OPENED, study.getStatus());
        assertNotNull(study.getOpenedDateTime());
        then(memberService).should().notify(study);
    }
    //ApplicationContextInitializer 스프링 코어가 제공하는 인터페이스
    static class ContainerPropertyInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            //TestPropertyValues 테스트에서 프로퍼티즈를 추가하기위한 유틸
            //컨테이너 정보를 스프링 컨텍스트에다가 프로퍼티로 넣고 스프링을통해 가져와서 사용한다
            TestPropertyValues.of("container.port="+genericPostgreSQLContainer.getMappedPort(5432))
                    .applyTo(applicationContext.getEnvironment());
        }
    }
}