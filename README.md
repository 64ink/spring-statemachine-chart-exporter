# Spring State Machine to PlantUML Exporter

 This package write a PlanetUML state chart file based on information probed.
 at runtime, from a Spring State Machine/
 
 This was created to find errors when setting up the state machine.  It is very easy to make a mistake,
 forget or connect the incorrect states.
 
 It may also be useful for those that code first and document later.
 
## Getting started
 
 Using it is easy.
 
 ```java
     	StateMachineExporter.exportToPlantUML(machine, null, filename);
 ```
 
 See the test state machine setup in [src/test](https://github.com/nofacepress/spring-statemachine-plantuml-exporter/tree/master/src/test/java/com/nofacepress/test/statemachine/example) for a full working demo.
 

## Official Source Repository

* [Source Repository](https://github.com/nofacepress/spring-statemachine-plantuml-exporter)
* [License](LICENSE.md)
 
## Auto-arrangement of state nodes
  
  PlantUML does not currently have an option to auto-arrange the charts.  In fact, the
  specification requires the arrows specify the direction.  This is a difficult problem to 
  solve, especially for such a tiny project.
  
  The strategy is to plot states downward and when a node has more than one connection rotate
  from down, to right, to left, ...
  It is simple but it works pretty well.  In any event, the result is a text file which a human can tweak
  if needed.

