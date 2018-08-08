# Renaissance: a self-Stabilizing Distributed SDN Control Plane
## A floodlight prototype implementation

This repository features a prototype implementation of the Renaissance algorithm [1], using the Floodlight SDN controller. An evaluation of performance is provided as well.

A guide on how to run the Renaissance prototype:
1. Move to the folder floodlight_global and build it via the **ant** command.
2. Move to the folder floodlight_local and build it via the **ant** command.
3. While in the floodlight_local folder, start the local controller by typing **java -jar target/floodlight.jar**
4. Open a new terminal and move to the folder where the network topologies reside, namely mininetScripts. Start the desired topology using the command **sudo python "filename".py**
5. A mininet prompt will show up once the network has been started. In the prompt, use the command **xterm h1** to open a terminal on host 1.
6. In the newly opened terminal on host 1, move to floodlight_global and start it by using the command **java -jar target/floodlight.jar**
7. You are now able to see the network being discovered by the global controller. To run more than one controller, repeat steps 5-6 for other hosts.

### Code Authors
This version of the implementation is by [Ivan Tannerud](https://www.linkedin.com/in/ivan-tannerud-12416b57/) and [Anton Lundgren](https://www.linkedin.com/in/anton-lundgren-432a43126/), and it builds upon earlier work done by [Michael Tran](https://www.linkedin.com/in/michael-tran-a1a8b514b/). [Emelie Ekenstedt](https://www.linkedin.com/in/emelie-ekenstedt-363463107/) provided assistance along the way. This code was developed as part of two master thesis porjects at Chalmers University of Technology, where [Elad Michael Schiller](http://www.cse.chalmers.se/~elad/) served as the primary supervisor and [Stefan Schmid](https://ct.cs.univie.ac.at/team/person/101989/) was the external examiner.   

### References 

[1] Marco Canini, Iosif Salem, Liron Schiff, Elad Michael Schiller, Stefan Schmid `Renaissance: Self-Stabilizing Distributed SDN Control Plane,' International Conference on Distributed Computing Systems (ICDCS) 2018. An earlier version can be accessed via https://arxiv.org/abs/1712.07697
