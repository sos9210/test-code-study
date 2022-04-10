package com.example.teststudyproject;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

//Junit5에서부터는 클래스와 메소드에 public을 붙이지 않아도됨

//테스트명 생성 방법을 정한다.
//DisplayNameGenerator.ReplaceUnderscores.class 이 경우엔 '_'를 공백으로 치환한다.
//@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
//테스트 기본 전략은 테스트 메서드마다 테스트 클래스의 인스턴스를 생성해서 각 테스트메서드가 독립적이게 한다.
//Junit5부터는 @TestInstance를 사용해서 이 기본 전략을 변경할 수 있다.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)    //이 테스트 클래스의 메서드가 모두 같은 인스턴스를 사용하도록 한다.
//@Order라는 애노테이션을 사용해서 각 메서드마다 순서를 정한다
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)  //특정 순서대로 테스트 메서드를 실행하고 싶을때 사용한다
//각 메서드마다 상태를 공유하고 순서를 정해야한다면 @TestInstance와 @TestMethodOrder를 같이 사용한다.
class StudyTest {

    @Test
    @Order(2)
    void testInstance1() {
        System.out.println(this);
    }

    @Test
    @Order(1)
    void testInstance2() {
        System.out.println(this);
    }

    //테스트를 반복실행한다.
    //value는 반복횟수, name은 테스트 이름
   @RepeatedTest(value = 10, name = "{displayName}, {currentRepetition}/{totalRepetitions}")
   @DisplayName("반복테스트")
   @Order(3)
    void repeat_study(RepetitionInfo repetitionInfo){
        System.out.println("test : "+repetitionInfo.getCurrentRepetition() + "/" + repetitionInfo.getTotalRepetitions());
    }

    @DisplayName("valueSource의 배열만큼")
    @ValueSource(strings = {"반복","테스트","실행한다"}) //strings외에도 다양한 데이터타입 지원
    @ParameterizedTest(name = "{index} {displayName} meesage={0}")
    @Order(5)
    void parameterizedTest(String message){
        System.out.println(message);
    }

    @DisplayName("Study타입으로 받아본다.")
    @ValueSource(ints = {10,20,30}) //strings외에도 다양한 데이터타입 지원 //암묵적으로 타입 변환도 이루어지지만 커스텀한 타입으로 명시적 타입 변환도 가능하다.
                                    //SimpleArgumentConverter를 상속받는 클래스를 구현해서(static inner또는 public클래스) 파라미터로 받아서 사용한다.
                                    //@ConverWith애노테이션에 컨버터 타입을 파라미터로 넘겨준다.
    @ParameterizedTest(name = "{index} {displayName} meesage={0}")
    @Order(4)
    void parameterizedStudyTest(@ConvertWith(StudyConvertor.class) Study message){
        System.out.println(message);
    }

    //SimpleArgumentConverter는 하나의 인자에 대해서만 다른타입으로 변환해준다.
    static class StudyConvertor extends SimpleArgumentConverter{
        @Override
        protected Object convert(Object o, Class<?> targetType) throws ArgumentConversionException {
            assertEquals(Study.class,targetType,"study로 변환한다");
            return new Study(Integer.parseInt(o.toString()));
        }
    }

    @DisplayName("2개이상의 인자로 받아본다.")
    @ParameterizedTest(name = "{index} {displayName} meesage={0}")
    @CsvSource({"10, '자바 스터디'","20,'스프링'"}) //2개이상의 인자를 직접받음
    void parameterizedCsvTest(Integer limit, String name){
        Study study = new Study(limit, name);
        System.out.println(study);
    }

    @DisplayName("ArgumentsAccessor를 사용해서 받아본다.")
    @ParameterizedTest(name = "{index} {displayName} meesage={0}")
    @CsvSource({"10, '자바 스터디'","20,'스프링'"}) //argumentsAccessor를 인자로 받아서 사용 
    void parameterizedArgumentsAccessorCsvTest(ArgumentsAccessor argumentsAccessor){
        System.out.println(new Study(argumentsAccessor.getInteger(0),argumentsAccessor.getString(1)));
    }
    @DisplayName("ArgumentsAggregator를 직접 구현해서 사용해서 받아본다.")
    @ParameterizedTest(name = "{index} {displayName} meesage={0}")
    @CsvSource({"10, 'ArgumentsAggregator11'","20,'ArgumentsAggregator22'"})    //ArgumentsAggregator를 직접 구현해서 사용
    void parameterizedStudyCsvTest(@AggregateWith(StudyAggregator.class) ArgumentsAccessor argumentsAccessor){
        System.out.println(new Study(argumentsAccessor.getInteger(0),argumentsAccessor.getString(1)));
    }

    static class StudyAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
            return new Study(argumentsAccessor.getInteger(0),argumentsAccessor.getString(1));
        }
    }
    //
/*    static class StudyConvertor implements {
        @Override
        protected Object convert(Object o, Class<?> targetType) throws ArgumentConversionException {
            assertEquals(Study.class,targetType,"study로 변환한다");
            return new Study(Integer.parseInt(o.toString()));
        }
    }*/

//    @EnabledOnOs(OS.MAC) 특정OS에서만 실행하도록 설정
//    @DisabledOnOs(OS.MAC) 특정OS에서 실행하지않도록 설정
    // 이외에도 @Enabled .... @Disabled... 등으로 시작하는 설정이 있다.
    @Test
    void create_test_case() {
        System.out.println(System.getenv("OS"));
        //jupiter
        //@EnabledIfEnvironmentVariable로 대체 가능
        //특정 조건을 만족하는경우에만 이후테스트를 실행 그렇지않으면 테스트 생략
        assumeTrue("Windows_NT".equalsIgnoreCase(System.getenv("OS")));
        
        //특정 조건을 만족하는경우 해당 코드블럭 실행
        assumingThat("Windows_NT".equalsIgnoreCase(System.getenv("OS")), () -> {
            //todo
        });
        Study study = new Study(1);

        //테스트실패시 실패한 테스트에 대해서만 확인가능하지만
        //asertAll은 여러 테스트를 하나로 묶을 수 있다.
        assertAll(
                //assertEquals비교 순서는 크게 상관없다.
                //     의도한 바는 (기대하는 값expected, 실제 값actual) 순으로 작성한다.
                ()-> assertEquals(StudyStatus.DRAFT, study.getStatus(),"실패할 경우 로그에 메세지를 남길 수 있다."),
                ()-> assertEquals(StudyStatus.DRAFT, study.getStatus(),() -> "람다식으로(또는 익명클래스구현)으로 메세지를 작성하면 실패할 경우에만 메세지를 연산한다"),
                () -> assertNotNull(study),
                () -> assertTrue(study.getLimit() > 0, ()-> "참거짓 여부를 확인")
        );

        //assertThrows는 exception을 반환한다.
        //assertThrows(기대하는 Exception타입, 실행할 경우 실제 발생하는 Exception)
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new Study(-10));
        //반환된 exception의 메세지를 비교검증할 수 있다.
        assertEquals(ex.getMessage(),"limit은 0보다 작으면 IllegalArgumentException");

        //해당(기대하는 시간, 실제 실행시 걸리는 시간)
        //10초안에 끝나지않으면 테스트실패
        assertTimeout(Duration.ofSeconds(10), () -> new Study(10));


        //기대하는 시간을 넘어서면 즉각적으로 테스트 종료한다.
        /* 주의점
         * executable 코드블럭은 별도의 쓰레드에서 실행하기 때문에 쓰레드로컬을 사용하는 코드가 있을 경우 예상하지 못한 결과가 나올수 있다.
         * 예를들어 스프링 @Transaction의 경우 쓰레드 로컬을 기본전략으로 사용하는데 이 경우 실제 롤백이 안되는 경우가 생길 수 있다.
         * 쓰레드와 관계없는 코드일 경우만 사용하는것을 권장
         */
        assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
           new Study(10);
           Thread.sleep(100);
        });
    }

    @Test
    @DisplayName("테스트명 작성1")    //각 테스트마다 이름을 지정해줄수 있음(권장)
    @Tag("fast")  //IDE : edit Configuration에서 @Tag 애노테이션이 붙어있는 테스트중에 해당 name만 실행가능하도록 설정가능
                //빌드툴, ci : Tag 애노테이션이 붙어있는 테스트중에 해당 name만 실행가능하도록 pom에 플러그인 추가설정
    //@FastTest // @Tag를 직접 정의해서 사용가능하다
    void create1() {
        System.out.println("create1");
    }
    @Disabled       //테스트에서 제외하는 설정
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