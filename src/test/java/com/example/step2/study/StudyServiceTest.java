package com.example.step2.study;

import com.example.step2.domain.Member;
import com.example.step2.domain.Study;
import com.example.step2.domain.StudyStatus;
import com.example.step2.member.MemberService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

//인터페이스는 있는데 구현체가 없는경우가 Mock객체를 사용하기 적절한 경우다.
@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @Mock
    MemberService memberService;
    @Mock
    StudyRepository studyRepository;

    @Test   //Mock객체 만들기
    void createStudtyService1(/*@Mock MemberService memberService, @Mock StudyRepository studyRepository*/){
        /* Mock객체를 만드는법
         *   1.애노테이션 사용
         *   2.Mockito 정적메서드 사용
         *   3.애노테이션을 활용해서 테스트메서드 파라미터에 넣어사용
         */
        //Mock객체를 여러번 사용한다면 애노테이션을 활용하는법이 더 좋아보인다.(애노테이션을 활용하는경우는 테스트 클래스에 @ExtendWith(MockitoExtension.class) 설정해야 가능)

        //MemberService memberService = Mockito.mock(MemberService.class);
        //StudyRepository studyRepository = Mockito.mock(StudyRepository.class);
        StudyService studyService = new StudyService(memberService,studyRepository);

        assertNotNull(studyService);
    }

    @Test   //Mock객체 Stubbing
    void createNewStudy(){
        StudyService studyService = new StudyService(memberService,studyRepository);
        assertNotNull(studyService);

        Optional<Member> optionalMember = memberService.findById(1L);   //Optional.isEmpty 반환
        memberService.validate(1L);                             //아무런 에러도 발생하지않음

        /*--- */

        Member member = new Member();
        member.setId(1L);
        member.setEmail("sung@naver.com");

        //when() : 인자로 주어진 행동이 실행되면
        //thenReturn : 앞선 when조건에 맞으면 반환한다.
        //ArgumentMatchers.any() 어떤 파라미터가 들어가도 상관없음을 의미
        //Mockito.when(memberService.findById(1L)).thenReturn(Optional.of(member));
        Mockito.when(memberService.findById(ArgumentMatchers.any())).thenReturn(Optional.of(member));

        assertEquals("sung@naver.com", memberService.findById(1L).get().getEmail());
        assertEquals("sung@naver.com", memberService.findById(2L).get().getEmail());

        /*---*/

        //void를 반환하는 메서드의 경우
        //memberService의 validate(1L)가 호출되면 예외발생
        Mockito.doThrow(new IllegalArgumentException()).when(memberService).validate(1L);

        assertThrows(IllegalArgumentException.class, () -> {
            memberService.validate(1L);
        });
        memberService.validate(2L);

        /*--- */
        Mockito.when(memberService.findById(ArgumentMatchers.any()))
                .thenReturn(Optional.of(member))
                .thenThrow(new RuntimeException())
                .thenReturn(Optional.empty());

        Optional<Member> byId = memberService.findById(1L);
        assertEquals("sung@naver.com", byId.get().getEmail());
        assertThrows(RuntimeException.class, () -> {
            memberService.findById(1L);
        });
        assertEquals(Optional.empty(),memberService.findById(1L));

        Study study = new Study(10,"java");
 //       studyService.createNewStudy(1L,study);

    }

    @Test   //Mock객체 Stubbing 연습문제
    void createNewStudyTest(){
        StudyService studyService = new StudyService(memberService,studyRepository);

        Member member = new Member();
        member.setId(1L);
        member.setEmail("sung@naver.com");
        Study study = new Study(10, "테스트");

        Mockito.when(memberService.findById(1L)).thenReturn(Optional.of(member));
        Mockito.when(studyRepository.save(study)).thenReturn(study);

        studyService.createNewStudy(1L,study);
        assertEquals(1L, study.getOwnerId());
    }
    @Test   //Mock객체 확인, BDD 스타일 Mockito API
    void createNewStudyTest2(){
        StudyService studyService = new StudyService(memberService,studyRepository);

        Member member = new Member();
        member.setId(1L);
        member.setEmail("sung@naver.com");

        Study study = new Study(10, "테스트");

        //When,thenReturn대신에 BDD스타일에 어울리는 Given, willReturn사용
        BDDMockito.given(memberService.findById(1L)).willReturn(Optional.of(member));
        BDDMockito.given(studyRepository.save(study)).willReturn(study);

        studyService.createNewStudy(1L,study);
        assertEquals(1L, study.getOwnerId());

        //테스트내용..
        //memberService에서 한번 notify가 study라는 매개변수를 가지고 호출되는지 테스트한다.
        //Mockito.verify(memberService, Mockito.times(1)).notify(study);
        BDDMockito.then(memberService).should(Mockito.times(1)).notify(study);

        //memberService에서 validate()가 전혀 호출되지 않는지 테스트한다.
        //Mockito.verify(memberService,Mockito.never()).validate(Mockito.any());
        BDDMockito.then(memberService).should(Mockito.never()).validate(Mockito.any());

        //memberService에서 notify(Study)가 먼저실행되고 notify(Member)가 실행되는지 순서를 테스트한다.
        InOrder inOrder = Mockito.inOrder(memberService);
        inOrder.verify(memberService).notify(study);
        inOrder.verify(memberService).notify(member);

        //memberService에서 더이상의 행동이 없어야한다.
        //Mockito.verifyNoMoreInteractions(memberService);
        BDDMockito.then(memberService).shouldHaveNoMoreInteractions();
    }

    @DisplayName("다른 사용자가 볼 수 있도록 스터디를 공개한다.")
    @Test
    void openStudy() {
        // Given
        StudyService studyService = new StudyService(memberService, studyRepository);
        Study study = new Study(10, "더 자바, 테스트");
        // TODO studyRepository Mock 객체의 save 메소드를호출 시 study를 리턴하도록 만들기.
        BDDMockito.given(studyRepository.save(study)).willReturn(study);

        // When
        studyService.openStudy(study);

        // Then
        // TODO study의 status가 OPENED로 변경됐는지 확인
        Assertions.assertEquals(study.getStatus(),StudyStatus.OPENED);

        // TODO study의 openedDataTime이 null이 아닌지 확인
        Assertions.assertNotNull(study.getOpenedDateTime());

        // TODO memberService의 notify(study)가 호출 됐는지 확인.
        BDDMockito.then(memberService).should().notify(study);
    }
}