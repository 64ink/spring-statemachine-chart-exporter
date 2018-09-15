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
package com.nofacepress.statemachine.exporter.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

/**
 * Creates a PlanetUML state chart based on information probed from a Spring
 * State Machine. This was created to find errors when setting up the state
 * machine.
 */
public class StateMachineBaseExporter {

	protected static class StateInfo extends StateInfoBase {
		public List<TransitionInfo> transitions = new ArrayList<TransitionInfo>();

		public void addTransition(StateInfo targetState, String event) {
			TransitionInfo t = new TransitionInfo();
			t.target = targetState;
			t.event = event;
			transitions.add(t);
		}

	}

	protected static class StateInfoBase {
		public int index = 0;
		public String id;
		public String name;
		public StateQualifer qualifier = null;
		boolean targeted = false;
		int pathlength = -1;

		public static int compare(StateInfoBase a, StateInfoBase b) {
			if (a == b) return 0;
			if (a.qualifier == StateQualifer.initial)
				return -1;
			if (b.qualifier == StateQualifer.initial)
				return 1;
			int x = Integer.compare(b.pathlength, a.pathlength);
			return x == 0 ? a.name.compareTo(b.name) : x;
		}

	};

	protected static enum StateQualifer {
		/// the starting state
		initial,
		/// not reachable in the expected flow, but can be jumped to explicitly
		alternate,
		/// a state that is not reachable nor connected to anything else
		orphan,
		/// end of state machine)
		done;
	}

	protected static class TransitionInfo {
		public StateInfoBase target;
		public String event;

	}

	protected static <S, E> int getPathLength(StateInfo info) {
		if (info.pathlength < 0) {

			info.pathlength = 0; // handles circular paths

			int length = 0;
			for (TransitionInfo t : info.transitions) {
				if (t.target.qualifier != StateQualifer.initial) {
					int l = getPathLength(StateInfo.class.cast(t.target));
					length = (l > length) ? l : length;
				}
			}
			info.pathlength = length + 1;

		}
		return info.pathlength;
	}

	protected static <S, E> List<StateInfo> analyzeStateMachine(StateMachine<S, E> machine) {
		State<S, E> initialState = machine.getInitialState();
		Collection<State<S, E>> states = machine.getStates();
		Collection<Transition<S, E>> transitions = machine.getTransitions();

		List<StateInfo> stateList = new ArrayList<StateInfo>();
		Map<S, StateInfo> stateMAP = new HashMap<S, StateInfo>();
		StateInfo initial = null;

		// go through all the states first as some of them may be missing from the
		// transitions
		for (State<S, E> s : states) {
			StateInfo info = new StateInfo();
			info.name = s.getId().toString();
			stateMAP.put(s.getId(), info);
			stateList.add(info);
			if (s == initialState) {
				initial = info;
			}
		}

		// walk all the transitions
		for (Transition<S, E> t : transitions) {
			State<S, E> sourceState = t.getSource();
			StateInfo source = stateMAP.get(sourceState.getId());
			State<S, E> targetState = t.getTarget();
			StateInfo target = stateMAP.get(targetState.getId());
			String event = t.getTrigger().getEvent().toString();
			source.addTransition(target, event);
			target.targeted = true; // help of determine if this node is reachable in the normal flow
		}

		// compute path lengths, starting with the initial
		initial.qualifier = StateQualifer.initial;
		getPathLength(initial);
		for (StateInfo s : stateList) {
			if (s.pathlength < 0) {
				getPathLength(s);
			}
		}

		// sort for a predictable output
		stateList.sort((a, b) -> StateInfoBase.compare(a, b));
		for (int i = 0; i < stateList.size(); i++) {
			StateInfo state = stateList.get(i);
			state.index = i;
			state.id = generateId(state.name, i + 1);

			if (state == initial) {
				state.qualifier = StateQualifer.initial;
			} else if (!state.targeted && state.transitions.isEmpty()) {
				state.qualifier = StateQualifer.orphan;
			} else if (state.transitions.isEmpty()) {
				state.qualifier = StateQualifer.done;
			} else if (!state.targeted) {
				state.qualifier = StateQualifer.alternate;
			}
			// sort for a predictable output
			state.transitions.sort((a, b) -> StateInfoBase.compare(a.target, b.target));
		}

		return stateList;
	}

	protected static String generateId(String name, int index) {
		// making a readable id
		StringBuffer sb = new StringBuffer(name.length() + 3);
		for (int i = 0; i < name.length(); i++) {
			char ch = name.charAt(i);
			if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch == '_')) {
				sb.append(ch);
			} else if (ch == ' ' || ch == '-') {
				sb.append('_');
			}
		}
		sb.append('_');
		sb.append(Integer.toString(index));
		return sb.toString();
	}

}
