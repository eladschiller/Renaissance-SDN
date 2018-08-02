# Renaissance: a self-Stabilizing Distributed SDN Control Plane
## A floodlight prototype implementation

This repository features a prototype implementation of the Renaissance algorithm [1], using the Floodlight SDN controller. An evaluation of performance is provided as well. This version of the implementation is by Ivan Tannerud and Anton Lundgren, and it builds upon earlier work one by Michael Tran. Emelie Ekenstedt provided assistance along the way.

A guide on how to run the Renaissance prototype:
1. Move to the folder floodlight_global and build it via the **ant** command.
2. Move to the folder floodlight_local and build it via the **ant** command.
3. While in the floodlight_local folder, start the local controller by typing **java -jar target/floodlight.jar**
4. Open a new terminal and move to the folder where the network topologies reside, namely mininetScripts. Start the desired topology using the command **sudo python "filename".py**
5. A mininet prompt will show up once the network has been started. In the prompt, use the command **xterm h1** to open a terminal on host 1.
6. In the newly opened terminal on host 1, move to floodlight_global and start it by using the command **java -jar target/floodlight.jar**
7. You are now able to see the network being discovered by the global controller. To run more than one controller, repeat steps 5-6 for other hosts.



References 

[1] Marco Canini, Iosif Salem, Liron Schiff, Elad Michael Schiller, Stefan Schmid `Renaissance: Self-Stabilizing Distributed SDN Control Plane,' International Conference on Distributed Computing Systems (ICDCS) 2018. An earlier version can be accessed via https://arxiv.org/abs/1712.07697
