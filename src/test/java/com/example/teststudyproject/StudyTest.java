package com.example.teststudyproject;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

//Junit5에서부터는 클래스와 메소드에 public을 붙이지 않아도됨

//테스트명 생성 방법을 정한다.
//DisplayNameGenerator.ReplaceUnderscores.class 이 경우엔 '_'를 공백으로 치환한다.
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StudyTest {

    @Test
    void create_test_case() {
        Study study = new Study();
        assertNotNull(study);
        System.out.println("create");
    }

    @Test
    @DisplayName("테스트명 작성1")    //각 테스트마다 이름을 지정해줄수 있음(권장)
    void create1() {
        System.out.println("create1");
    }
    @Disabled       //테스트에서 제외
    @Test
    void create2() {
        System.out.println("create2");
    }
    
    //@BeforeAll ,@AfterAll
    //@BeforeAll : 테스트 실행 전 딱 한번 실행, @AfterAll : 테스트 실행 후 딱 한번 실행
    //반드시 static으로 작성, private 금지 , 리턴타입은 void여야 한다.
    @BeforeAll
    static void beforeAll(){
        System.out.println("before all");
    }
    @AfterAll
    static void afterAll(){
        System.out.println("after all");
    }
    //각 테스트마다 이전과 이후에 호출 굳이 static일 필요없다
    @AfterEach
    void afterEach(){
        System.out.println("afterEach");
    }
    @BeforeEach
    void beforeEach(){
        System.out.println("beforeEach");
    }
}