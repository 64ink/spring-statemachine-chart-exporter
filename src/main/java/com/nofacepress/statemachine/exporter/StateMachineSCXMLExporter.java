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

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.springframework.statemachine.StateMachine;

import com.nofacepress.statemachine.exporter.base.StateMachineBaseExporter;

/**
 * Creates a SCXML state chart based on information probed from a Spring State
 * Machine. This was created to find errors when setting up the state machine.
 */
public class StateMachineSCXMLExporter extends StateMachineBaseExporter {

	/**
	 * Creates a SCXML state chart based on information probed from a Spring State
	 * Machine.
	 * 
	 * @param machine  the Spring StateMachine instance to probe.
	 * @param          <S> the class for the state machine states
	 * @param          <E> the class for the state machine events
	 * @param filename the file to save too.
	 * @throws IOException        on file I/O errors
	 * @throws XMLStreamException on XML stream error
	 */
	public static <S, E> void export(final StateMachine<S, E> machine, String filename)
			throws IOException, XMLStreamException {
		OutputStreamWriter f;
		f = new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8);
		export(machine, new BufferedWriter(f));
		f.close();
	}

	/**
	 * Creates a SCXML state chart based on information probed from a Spring State
	 * Machine.
	 * 
	 * @param machine the Spring StateMachine instance to probe.
	 * @param         <S> the class for the state machine states
	 * @param         <E> the class for the state machine events
	 * @param output  the output to write to.
	 * @throws IOException        on file I/O errors
	 * @throws XMLStreamException on XML stream error
	 */
	public static <S, E> void export(final StateMachine<S, E> machine, Writer output)
			throws IOException, XMLStreamException {

		List<StateInfo> lstates = analyzeStateMachine(machine);

		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = factory.createXMLStreamWriter(output);

		writer.writeStartDocument("UTF-8", "1.0");
		writer.writeCharacters("\n");
		writer.writeStartElement("scxml");
		writer.writeAttribute("xmlns", "http://www.w3.org/2005/07/scxml");
		writer.writeAttribute("version", "1.0");
		writer.writeAttribute("initial", machine.getInitialState().getId().toString());

		for (StateInfo source : lstates) {
			writer.writeCharacters("\n  ");
			writer.writeStartElement("state");
			writer.writeAttribute("id", source.name);

			for (TransitionInfo t : source.transitions) {
				writer.writeCharacters("\n    ");
				writer.writeStartElement("transition");
				writer.writeAttribute("event", t.event);
				writer.writeAttribute("target", t.target.name);
				writer.writeEndElement();
			}

			writer.writeEndElement();
		}

		writer.writeEndElement();
		writer.writeCharacters("\n");
		writer.writeEndDocument();
		writer.flush();

	}

}
