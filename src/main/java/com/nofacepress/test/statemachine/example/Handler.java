package com.nofacepress.test.statemachine.example;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.annotation.OnStateEntry;
import org.springframework.statemachine.annotation.OnStateExit;
import org.springframework.statemachine.annotation.OnStateMachineStart;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.stereotype.Component;

@Component
@WithStateMachine(id = MyStateMachineConfiguration.MACHINE_NAME)
//@Lazy(false)
public class Handler {

	@Autowired
	Handler(BeanFactory beanFactory) {
		System.out.println("Handler: " + beanFactory.toString());
		
	}
	
	@StatesOnTransition(target= MyStates.STATE_B )
	public void onA(StateContext<MyStates, MyEvents> stateContext) {
		Object e = stateContext.getEvent();
		String s = (e==null) ? "null" : e.toString();
		System.out.println("*** onB " + s);
	}

	@StatesOnTransition(target= MyStates.STATE_A)
	public void onB(StateContext<MyStates, MyEvents> stateContext) {
		//stateContext.getStateMachine().getInitialState().
		Object e = stateContext.getEvent();
		String s = (e==null) ? "null" : e.toString();
		System.out.println("*** onA " + s);
	}

    @OnStateMachineStart
    public void onStateMachineStart() {
		System.out.println("starting");
    }

    @OnStateEntry
    public void anyStateEntry() {
		System.out.println("@OnStateEntry");
    }

    @OnStateExit
    public void anyStateExit() {
		System.out.println("@OnStateExit");
    }
    
}
