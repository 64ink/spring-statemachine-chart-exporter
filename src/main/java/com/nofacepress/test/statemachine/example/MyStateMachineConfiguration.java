/*
 * Copyright 2018 No Face Press, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nofacepress.test.statemachine.example;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnableWithStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.configurers.LocalTransitionConfigurer;
import org.springframework.statemachine.config.configurers.TransitionConfigurer;

@Configuration
@EnableStateMachineFactory(name = MyStateMachineConfiguration.MACHINE_NAME)
public class MyStateMachineConfiguration extends StateMachineConfigurerAdapter<MyStates, MyEvents> {

	public static final String MACHINE_NAME = "com.nofacepress.test.statemachine.example.MyStateMachineConfiguration";
	@Override
	public void configure(StateMachineStateConfigurer<MyStates, MyEvents> states) throws Exception {

		states.withStates().initial(MyStates.STATE_A).end(MyStates.STATE_F).states(new HashSet<MyStates>(
				Arrays.asList(MyStates.STATE_B, MyStates.STATE_C, MyStates.STATE_D, MyStates.STATE_E)));

	}


	@Override
	public void configure(StateMachineTransitionConfigurer<MyStates, MyEvents> transitions) throws Exception {

			
		transitions.withExternal().source(MyStates.STATE_A).target(MyStates.STATE_B).event(MyEvents.EVENT_1)
		.and()
			.withExternal().source(MyStates.STATE_A).target(MyStates.STATE_A).event(MyEvents.RETRY).and()

				.withExternal().source(MyStates.STATE_B).target(MyStates.STATE_E).event(MyEvents.EVENT_3).and()
				.withExternal().source(MyStates.STATE_B).target(MyStates.STATE_C).event(MyEvents.EVENT_2).and()
				.withExternal().source(MyStates.STATE_D).target(MyStates.STATE_B).event(MyEvents.EVENT_1).and()
				.withExternal().source(MyStates.STATE_D).target(MyStates.STATE_E).event(MyEvents.EVENT_3).and()
				.withExternal().source(MyStates.STATE_B).target(MyStates.STATE_G).event(MyEvents.EVENT_4);
	}
}
