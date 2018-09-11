# Spring State Machine Chart Exporter

 This package write a state chart file based on information probed.
 at runtime, from a Spring State Machine/
 
 Current exports include
 
 1. [PlantUML](http://plantuml.com/) state charts
 2. [SCXML](https://en.wikipedia.org/wiki/SCXML) states and transitions
 3. [Lucid Chart](https://www.lucidchart.com/) CSV file via *File / Import Data / Process File*
  
 This was created to find errors when setting up the state machine.  It is very easy to make a mistake,
 forget or connect the incorrect states.
 
 It may also be useful for those that code first and document later.
 
 Build status: [![build_status](https://travis-ci.org/phillip-kruger/apiee.svg?branch=master)](https://travis-ci.org/phillip-kruger/apiee)
 
## Maven Setup

```xml
<dependency>
  <groupId>com.nofacepress</groupId>
  <artifactId>spring-statemachine-chart-exporter</artifactId>
  <version>0.2.4</version>
</dependency>
```

## Getting started
 
 Using it is easy.
 
 ```java
// PlantUML
StateMachinePlantUMLExporter.export(machine, "MY State Machine", "statemachine.plantuml");
     	
// SCXML
StateMachineSCXMLExporter.export(machine, "statemachine.scxml");

// Lucid Chart
StateMachineLucidChartExporter.export(machine, "State Machine", "statemachine-lucid.csv");
 ```
 
 See the test state machine setup in [src/test](https://github.com/nofacepress/spring-statemachine-chart-exporter/blob/master/src/test/java/com/nofacepress/test/statemachine/example/) for a full working demo.
 

## Official Source Repository

* [Source Repository](https://github.com/nofacepress/spring-statemachine-chart-exporter)
* [License](LICENSE.md)
 
## Release Notes

### Arranging charts in PlantUML
 
  PlantUML does not currently have an option to auto-arrange the charts.  In fact, the
  specification requires the arrows specify the direction.  This is a difficult problem to 
  solve, especially for such a tiny project.
  
  The strategy is to plot states downward and when a node has more than one connection rotate
  from down, to right, to left, ...
  It is simple but it works pretty well.  In any event, the result is a text file which a human can tweak
  if needed.

