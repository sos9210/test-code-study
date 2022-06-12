package com.example.step2.study;

import com.example.step2.domain.Member;
import com.example.step2.domain.Study;
import com.example.step2.member.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
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
        assertEquals(member, study.getOwner());
    }
}