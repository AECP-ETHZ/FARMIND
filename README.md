# FARMIND

### Introduction

This is an agent-based model for the simulation of farm level decision-making developed by the Agricultural Economics and Policy Group at ETH Zurich (www.aecp.ethz.ch).

The model represents each farm as an agent as well as a node in social networks. Each agent chooses a strategy for perform activities based on the satisfaction and uncertainty about its output. A set of activity options is determined for each agent according to the strategy it chooses and by considering its personal preference and social interactions with its peers. 

A mathematical programming model, integrated into the agent-based model, is then used to select an optimal activity for each agent to perform and generate an income value. Agents' satisfaction and uncertainty are updated accordingly. This sets the base for decision-making of next period.

### Program Overview and Requirements

The initial conditions of the agents with all parameters need to be specified in the `/data` directory before starting the model. The /test_data/ folder contains example initialization files. 

For the complete system documentation download the [documentation](../blob/tree/master/documentation). 

### Library Installation in Eclipse

Required libraries:
1. Download jgrapht-1.1.0.zip file from here: http://jgrapht.org/
2. Download commons-math3-3.6.1-bin.zip from here: http://commons.apache.org/proper/commons-math/download_math.cgi

Both of the those libraries **should** already be downloaded and included in the /lib/ directory. 

1. In Eclipse Open `Window -> Preferences -> java -> Build Path -> User Libraries`

2. Select `New...` and input the name of the library:
	Either *jgrapht* or *commons-math3-3.6.1*
	
3. After creating the library, link the new library with the jar files.
	Select "Add JAR" and select the JAR files from the /lib/ folder in the project that correspond to the library name. 
	
4. In the main Eclipse window, right click on the "Farmade" project and select "Build Path->Add Libraries".

5. In the window, select "User Libraries" and then select the newly created library. 

6. Refresh the project (F5) and compile.

### Java JAR File Extraction

To extract the required jar file for the external gams integration in eclipse do the following:

1. In eclipse right click on the /src/ folder and select 'Export' 

2. Under 'Java' select 'Runnable Jar File' and select 'Next'

3. Make sure the "Launch Configuration" is set to "Farmade - Consumat" as the entry point for the program and make sure to select 'Extract required libraries' to ensure that the jar file is a stand alone file. 

4. Choose a file location and hit 'Finish'.

### Troubleshooting Guide

1. Issue related to graph library:

>Exception in thread "main" java.lang.Error: Unresolved compilation problems: 
>	       Graph cannot be resolved to a type
>	       DefaultEdge cannot be resolved to a type
	       
To fix this issue ensure that you have installed all libraries as shown in the *Library Installation in Eclipse* section. 
If the issue persists, try removing the libraries and reinstalling them again.
	
2. Program hangs after printing "Waiting for gams results to be generated":
This is the expected program operation. It has generated the gams file and started the gams simulation. 
The gams program will produce a results file for the ABM to process. Until then it will wait. 
	
	When the ABM is running in test mode, the batch file 'run_gams.bat' will copy a results file (stored in /data/ folder as 'Grossmargin_P4,00') to simulate the gams output. 
