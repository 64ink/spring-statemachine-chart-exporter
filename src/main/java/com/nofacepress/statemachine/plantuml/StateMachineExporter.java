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
package com.nofacepress.statemachine.plantuml;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
 * 
 * @see http://plantuml.com/
 * @author Thomas Davis
 */
public class StateMachineExporter {

	private static final class PlanetUMLConstants {
		static final String DOWN_ARROW = "-down->";
		static final String LEFT_ARROW = "-left->";
		static final String RIGHT_ARROW = "-right->";
		static final String START_UML = "@startuml";
		static final String END_UML = "@enduml";
		static final String STATE_PARAM = "state";
		static final String AS = "as";
		static final String END_STATE = "[*]";
		static final String BEGIN_STATE = "[*]";
		static final String TITLE_PARAM = "title";
		static final String MONOCHROME = "skinparam monochrome true";
	}

	private static class StateInfo extends StateInfoBase {
		public List<TransitionInfo> transitions = new ArrayList<TransitionInfo>();

		public void addTransition(StateInfo targetState, String event) {
			TransitionInfo t = new TransitionInfo();
			t.target = targetState;
			t.event = event;
			transitions.add(t);
		}
	}

	private static class StateInfoBase {
		public String id;
		public String name;
		public StateQualifer qualifier = null;
		boolean targeted = false;
	};

	private static enum StateQualifer {
		/// the starting state
		initial,
		/// not reachable in the expected flow, but can be jumped to explicitly
		alternate,
		/// a state that is not reachable nor connected to anything else
		orphan,
		/// end of state machine)
		done;
	}

	private static class TransitionInfo {
		public StateInfoBase target;
		public String event;

	}

	private static <S, E> List<StateInfo> analyzeStateMachine(StateMachine<S, E> machine) {
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

		// sort for a predictable output
		stateList.sort((a, b) -> a.name.compareTo(b.name));
		for (int i = 0; i < stateList.size(); i++) {
			StateInfo state = stateList.get(i);
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
			state.transitions.sort((a, b) -> a.target.name.compareTo(b.target.name));
		}

		return stateList;
	}

	/**
	 * Creates a PlanetUML state chart based on information probed from a Spring
	 * State Machine.
	 * 
	 * @param machine  the Spring StateMachine instance to probe.
	 * @param title    the title to put on the chart, null is ok for no title.
	 * @param filename the file to save too.
	 * @throws IOException on file I/O errors
	 */
	public static <S, E> void exportToPlantUML(final StateMachine<S, E> machine, String title, String filename)
			throws IOException {

		List<StateInfo> lstates = analyzeStateMachine(machine);

		final String[] arrows = { PlanetUMLConstants.DOWN_ARROW, PlanetUMLConstants.RIGHT_ARROW,
				PlanetUMLConstants.LEFT_ARROW };

		FileWriter f;
		f = new FileWriter(filename);
		BufferedWriter writer = new BufferedWriter(f);

		writer.append(PlanetUMLConstants.START_UML + "\n");
		writer.append(PlanetUMLConstants.MONOCHROME + "\n");

		if (title != null && !title.isEmpty()) {
			writer.append(String.format("%s %s\n", PlanetUMLConstants.TITLE_PARAM, title));
		}

		for (StateInfo state : lstates) {
			String label = "";
			String clazz = "";
			if (state.qualifier != null) {
				label = String.format("\\n[<i>%s</i>]", state.qualifier.name());
				clazz = String.format(" <<%s>>", state.qualifier.name());
			}
			writer.append(String.format("%s \"%s%s\" %s %s%s\n", PlanetUMLConstants.STATE_PARAM, state.name, label,
					PlanetUMLConstants.AS, state.id, clazz));
		}

		for (StateInfo source : lstates) {
			if (source.qualifier == StateQualifer.initial) {
				writer.append(String.format("%s %s %s\n", PlanetUMLConstants.BEGIN_STATE,
						PlanetUMLConstants.RIGHT_ARROW, source.id));
			}
			if (source.qualifier == StateQualifer.done) {
				writer.append(String.format("%s %s %s\n", source.id, PlanetUMLConstants.DOWN_ARROW,
						PlanetUMLConstants.END_STATE));
			} else {
				int n = 0;
				for (TransitionInfo t : source.transitions) {
					writer.append(String.format("%s %s %s : %s\n", source.id, arrows[n++ % 3], t.target.id, t.event));
				}
			}
		}

		writer.append(PlanetUMLConstants.END_UML + "\n");

		writer.close();

	}

	private static String generateId(String name, int index) {
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
