package com.example.step2.member;

import com.example.step2.domain.Member;
import com.example.step2.domain.Study;

import java.util.Optional;

public interface MemberService {

    Optional<Member> findById(Long memberId);

    void validate(Long memberId);

    void notify(Study any);

    void notify(Member member);
}
