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
package com.nofacepress.statemachine.exporter;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.springframework.statemachine.StateMachine;

import com.nofacepress.csv4180.CSVWriter;
import com.nofacepress.statemachine.exporter.base.StateMachineBaseExporter;

/**
 * Creates a Lucid Chart state chart based on information probed from a Spring
 * State Machine. This was created to find errors when setting up the state
 * machine.
 * 
 * @see http://plantuml.com/
 * @author Thomas Davis
 */
public class StateMachineLucidChartExporter extends StateMachineBaseExporter {

	/**
	 * Creates a Lucid Chart state chart based on information probed from a Spring
	 * State Machine.
	 * 
	 * @param machine  the Spring StateMachine instance to probe.
	 * @param filename the file to save too.
	 * @throws IOException        on file I/O errors
	 * @throws XMLStreamException
	 */
	public static <S, E> void export(final StateMachine<S, E> machine, String pageTitle, String filename)
			throws IOException, XMLStreamException {
		OutputStreamWriter f;
		f = new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8);
		export(machine, pageTitle, new BufferedWriter(f));
		f.close();
	}

	/**
	 * Creates a Lucid Chart state chart based on information probed from a Spring
	 * State Machine.
	 * 
	 * @param machine the Spring StateMachine instance to probe.
	 * @param output  the output to write to.
	 * @throws IOException        on file I/O errors
	 * @throws XMLStreamException
	 */
	public static <S, E> void export(final StateMachine<S, E> machine, String pageTitle, Writer output)
			throws IOException, XMLStreamException {

		List<StateInfo> lstates = analyzeStateMachine(machine);

		CSVWriter writer = new CSVWriter(output);

		// header row
		writer.writeField("Id");
		writer.writeField("Name");
		writer.writeField("Shape Library");
		writer.writeField("Page ID");
		writer.writeField("Contained B");
		writer.writeField("Line Source");
		writer.writeField("Line Destination");
		writer.writeField("Source Arrow");
		writer.writeField("Destination Arrow");
		writer.writeField("Text Area 1");
		writer.writeField("Text Area 2");
		writer.writeField("Text Area 3");
		writer.newLine();

		// header row
		writer.writeField("1");
		writer.writeField("Page");
		writer.writeField("");
		writer.writeField("");
		writer.writeField("");
		writer.writeField("");
		writer.writeField("");
		writer.writeField("");
		writer.writeField("");
		writer.writeField(pageTitle);
		writer.writeField("");
		writer.writeField("");
		writer.newLine();

		final int INDEX_OFFSET = 2;

		// write out the states
		for (StateInfo source : lstates) {
			String label = source.name;
			if (source.qualifier != null) {
				label = String.format("%s\n<%s>", source.name, source.qualifier.name());
			}

			writer.writeField("" + (source.index + INDEX_OFFSET));
			writer.writeField("State Name");
			writer.writeField("UML");
			writer.writeField("1");
			writer.writeField("");
			writer.writeField("");
			writer.writeField("");
			writer.writeField("");
			writer.writeField("");
			writer.writeField(label);
			writer.writeField("");
			writer.writeField("");
			writer.newLine();

		}

		int lineCounter = INDEX_OFFSET + lstates.size();
		// write out the transitions
		for (StateInfo source : lstates) {

			for (TransitionInfo t : source.transitions) {

				writer.writeField("" + (lineCounter++));
				writer.writeField("Line");
				writer.writeField("");
				writer.writeField("1");
				writer.writeField("");
				writer.writeField("" + (source.index + INDEX_OFFSET));
				writer.writeField("" + (t.target.index + INDEX_OFFSET));
				writer.writeField("None");
				writer.writeField("Arrow");
				writer.writeField(t.event);
				writer.writeField("");
				writer.writeField("");
				writer.newLine();

			}

		}

		writer.flush();
		writer.close();
	}

}
