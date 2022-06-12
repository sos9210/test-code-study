package com.example.teststudyproject;

import org.junit.Before;
import org.junit.Test;

//junit5를 사용하면 junit4를 지원하지만 @Rule은 지원하지않는다.
//@EnableRuleMigrationSupport를 사용하면 @Rule을 어느정도 지원하지만 완전하지않다.
public class StudyJunit4Test {

    @Before
    public void before(){
        System.out.println("before");
    }

    @Test
    public void createTest() {
        System.out.println("Test");
    }
    @Test
    public void createTest2() {
        System.out.println("Test2");
    }
}
