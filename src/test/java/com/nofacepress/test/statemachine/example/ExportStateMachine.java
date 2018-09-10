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

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;

import com.nofacepress.statemachine.exporter.StateMachineLucidChartExporter;
import com.nofacepress.statemachine.exporter.StateMachinePlantUMLExporter;
import com.nofacepress.statemachine.exporter.StateMachineSCXMLExporter;

@SpringBootApplication
public class ExportStateMachine {

	@Autowired
	ExportStateMachine(StateMachine<?, ?> machine) throws IOException, XMLStreamException {
		String filename = "statemachine.plantuml";
		StateMachinePlantUMLExporter.export(machine, null, filename);
		System.out.println("Saved state machine to " + filename);

		filename = "statemachine.scxml";
		StateMachineSCXMLExporter.export(machine, filename);
		System.out.println("Saved state machine to " + filename);

		filename = "statemachine-lucid.csv";
		StateMachineLucidChartExporter.export(machine, "State Machine", filename);
		System.out.println("Saved state machine to " + filename);

		System.exit(0);
	}

	public static void main(String[] args) {
		SpringApplication.run(ExportStateMachine.class, args);
	}
}
