package com.nofacepress.test.statemachine.example;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import org.springframework.statemachine.annotation.OnTransition;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@OnTransition
public @interface StatesOnTransition {

    MyStates[] source() default {};

    MyStates[] target() default {};
}