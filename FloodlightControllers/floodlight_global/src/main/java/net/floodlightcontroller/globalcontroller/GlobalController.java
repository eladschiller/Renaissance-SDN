package net.floodlightcontroller.globalcontroller;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import net.floodlightcontroller.packet.ICMP;
import org.projectfloodlight.openflow.protocol.OFFactories;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketOut;
import org.projectfloodlight.openflow.protocol.OFPortDesc;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.TransportPort;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.PortChangeType;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.util.SingletonTask;
import net.floodlightcontroller.linkdiscovery.ILinkDiscovery.LDUpdate;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.core.IFloodlightProviderService;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Set;

import net.floodlightcontroller.packet.Data;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.TCP;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.storage.StorageException;
import net.floodlightcontroller.threadpool.IThreadPoolService;
import net.floodlightcontroller.topology.ITopologyListener;
import net.floodlightcontroller.topology.ITopologyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalController implements IOFMessageListener, IFloodlightModule, IOFSwitchListener, ITopologyListener {

	// Floodlight services used
	protected IFloodlightProviderService floodlightProvider;
	protected IOFSwitchService switchService;
	protected ITopologyService topologyService;
	protected IRoutingService routingService;
	protected ILinkDiscoveryService linkDiscService;
	protected IThreadPoolService threadPoolService;

	protected Set<String> discoveredNodes; // The list of nodes discovered so far (stored as a string MAC)
	protected Set<String> directSwitches; // The list of Switches that are directly connected to the Global Controller (stored as a string MAC)
	protected List<SwitchNode> switchNodes; // SwitchNodes for all the switches/controllers in the topology, has information about switch and its neighbors and links
	protected static Logger logger;

	private Set<String> querySet; // Set containing Switch/Controller ID (in form of string) that are to be queried
	private Set<String> currentResponseSet; // Set containing Switch/Controller ID (in form of string) that we have received a reply from with the current label
	// Map of tag/label and list of responses (for every tag/label)
	private Map<Integer, List<Response>> responses;
	private Map<String, String> configParams;

	// Synchronizer variables
	private int previousLabel;
	private int currentLabel;
	private boolean newRound;

	// Running tasks on the controller (with given interval in milliseconds)
	protected SingletonTask installRulesTask;
	protected static final int TASK_DELAY = 500;
	protected static final int maxResponses = 1073741824;
	protected static final int maxLabel = 2147483647;

	// Change according to host ip that is running global controller (can be done through properties)
	private IPv4Address MY_IP;
	protected static final IPv4Address LOCAL_CONTROLLER_IP = IPv4Address.of("0.0.0.0");


	@Override
	public String getName() {
		return GlobalController.class.getSimpleName();
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// Run this module before topology and linkdiscovery in order to disable
		// LLDP being flooded as a default behavior
		return name.equals("topology") || name.equals("linkdiscovery");
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(ITopologyService.class);
		l.add(IRoutingService.class);
		l.add(ILinkDiscoveryService.class);
		l.add(IThreadPoolService.class);
		return l;
	}

	/**
	 * Init services and variables.
	 */
	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		switchService = context.getServiceImpl(IOFSwitchService.class);
		topologyService = context.getServiceImpl(ITopologyService.class);
		routingService = context.getServiceImpl(IRoutingService.class);
		linkDiscService = context.getServiceImpl(ILinkDiscoveryService.class);
		threadPoolService = context.getServiceImpl(IThreadPoolService.class);
		discoveredNodes = new HashSet<String>();
		directSwitches = new HashSet<String>();
		switchNodes = new ArrayList<SwitchNode>();
		logger = LoggerFactory.getLogger(GlobalController.class);
		querySet = new HashSet<String>();
		new HashSet<String>();
		currentResponseSet = new HashSet<String>();
		responses = new HashMap<Integer, List<Response>>();
		configParams = context.getConfigParams(this);
		MY_IP = IPv4Address.of(configParams.get("controllerIP"));
		previousLabel = 0;
		currentLabel = previousLabel + 1;
		responses.put(previousLabel, new ArrayList<Response>());
		responses.put(currentLabel, new ArrayList<Response>());
		newRound = false;
        	queryNetwork();
	}

	/**
	 * Add listeners and start the main task of the algortihm (installrulestask) upon startup
	 */
	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		final long startTime = System.nanoTime();
		// Listen to PACKET-IN messages and switch services to know when a direct switch has connected
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		switchService.addOFSwitchListener(this);
		topologyService.addListener(this);

		// Schedule commands to run tasks
		ScheduledExecutorService scheduledExecutor = threadPoolService.getScheduledExecutor();

		/*
		 * Uses findPaths() to find paths between nodes and sends packets to the local controller with rules to install.
		 * @param None
		 * @return Void
		 */
		installRulesTask = new SingletonTask(scheduledExecutor, new Runnable() {
			@Override
			public void run() {
				try {
					// Rules in form of packet-out to the local controller to be installed at the switches
					List<OFPacketOut> rules = new ArrayList<OFPacketOut>();

					// Construct and encapsulate the different layer packets
					Ethernet l2 = new Ethernet();
					l2.setEtherType(EthType.IPv4);

					IPv4 l3 = new IPv4();
					l3.setSourceAddress(MY_IP);
					l3.setDestinationAddress(LOCAL_CONTROLLER_IP);
					l3.setTtl((byte) 64);
					l3.setProtocol(IpProtocol.TCP);

					TCP l4 = new TCP();
					l4.setSourcePort(TransportPort.of(65003));
					l4.setDestinationPort(TransportPort.of(67));

					//Report the size of the querySet to the user to find out how many nodes have been discovered.
					logger.info("QS SIZE IS {}", querySet.size());

					//Always set the newRound to false before starting an iteration
					newRound=false;

                    //If all nodes have responded with the current label, increase the label
					if (!querySet.isEmpty() && currentResponseSet.containsAll(querySet)) {
						previousLabel = currentLabel;
						responses.put(++currentLabel %maxLabel, new ArrayList<Response>());
						// Remove previous responses that are not prevLbl or currLbl
						responses.remove(previousLabel -1);
						// Reset the old tree and build a new tree for every new label
						switchNodes = new ArrayList<SwitchNode>();
						List<Response> responseList = responses.get(previousLabel);
						// Construct the tree with help of responses (with tags from previous label after setting it to current label)
						for(Response currentResponse : responseList){
							if (!currentResponse.getNeighbours().equals("empty")) {
								createTopology(currentResponse, switchNodes);
							}
						}
						//Add all discovered nodes to compare with queryset
						for(SwitchNode switchNode : switchNodes){
							discoveredNodes.add(switchNode.getDpid());
						}
						// We don't query ourselves so remove
						discoveredNodes.remove(MY_IP.toString());
						// Check if whole topology discovered, if yes set newRound to true
						if (querySet.size() == discoveredNodes.size()) {
							newRound = true;
						}
						//Reset the queryset och current response set
						new HashSet<String>(querySet);
						querySet = new HashSet<String>(discoveredNodes);
						currentResponseSet = new HashSet<String>();
					}else {
					    for (String querySw : querySet) {
							if(!currentResponseSet.contains(querySw)){
							    //Find out who didn't answer
								//logger.info("NODE DID NOT ANSWER: {} ", querySw);
							}
					    }

                    }
                    //Print the current label.
					logger.info("currentLabel is: {}", currentLabel);
					// Remove any old response from global controller and add a record including its neighbors (4)
					Response localResponse = new Response(MY_IP.toString(), getLocalInfo(), "empty", currentLabel);
					List<Response> respList = responses.get(currentLabel);
					respList.remove(localResponse);
					respList.add(localResponse);
					responses.put(currentLabel, respList);

					List<Response> previousReponseList = responses.get(previousLabel);
					List<Response> currRespList = responses.get(currentLabel);
					
					// Add the needed responses to construct rules in an ordering from prevLbl first to currLbl last and process them
					Set<Response> topoSet = new LinkedHashSet<Response>();
					topoSet.addAll(previousReponseList);
					topoSet.removeAll(currRespList);
					topoSet.addAll(currRespList);
					List<SwitchNode> currentSwitchNodes = new ArrayList<SwitchNode>();
					for(Response responseInTopoSet : topoSet){
						createTopology(responseInTopoSet, currentSwitchNodes);
					}


					// Remove unreachable nodes from querySet and discoveredNodes       
					int managerIndex = currentSwitchNodes.indexOf(new SwitchNode(MY_IP.toString()));
					if(managerIndex!=-1){
						SwitchNode managerSwitchNode = currentSwitchNodes.get(managerIndex);
						for(SwitchNode node : currentSwitchNodes){
							if(!node.equals(managerSwitchNode)){
								String port = findPaths(managerSwitchNode, node, currentSwitchNodes);
								if (port.equals("")) {
									//logger.info("Removed {} from querySet because no path from manager", sn.getDpid());
									discoveredNodes.remove(node.getDpid());
									querySet.remove(node.getDpid());
								}
							}
						}
					}

                    //All the messages have been prepared, time to send out queries.
					queryNetwork();

					//Find out what rules to install.
					for (SwitchNode switchNodeInCurrent : currentSwitchNodes) {
						// Exclude managers since we can not install rules at the controllers
						if (!sun.net.util.IPAddressUtil.isIPv4LiteralAddress(switchNodeInCurrent.getDpid())) {
							// Directly connected switches need to be sent out a different port
							OFPort outPort;
							if (directSwitches.contains(switchNodeInCurrent.getDpid())) {
								outPort = OFPort.CONTROLLER;
							} else {
								outPort = OFPort.TABLE;
							}

							// Message to be sent and parsed by local controller
							String message = "ACTION: INSTALL-RULES" + "\nRULES: ";

							String targets = "";

							// Compute the primary and backup paths for a switch to any other node.
							for (SwitchNode switchNodeInCurrent2 : currentSwitchNodes) {
								if (!switchNodeInCurrent.equals(switchNodeInCurrent2)) {
									// Computes all the paths in shortest order,
									// outputs the output port to forward to split by '/', example: 1/2/3
									String port = findPaths(switchNodeInCurrent, switchNodeInCurrent2, currentSwitchNodes);
									if (!port.equals("")) {
										targets += switchNodeInCurrent2.getDpid() + "-" + port + ";";
									} else {
										//logger.info("NO PATH FROM {} TO {}", sn1.getDpid(), sn2.getDpid());
										// No path was found
									}
								}
							}

							if (targets == "") {
								targets = "empty";
							}

							message += targets + "\nSYNC-LABEL: " + currentLabel + "\nUNREACHABLE-MANAGERS: ";

							// Check and delete all unreachable managers on new rounds (and their rules, handled by local controller) (8)
							String unreachableManagers = "";
							if (newRound) {
								Response responseToCompare;
								Response responseCopy = new Response(switchNodeInCurrent.getDpid());
								if(previousReponseList.indexOf(responseCopy)==-1){
									responseToCompare = responseCopy;
									responseToCompare.setManagers("empty");
								} else{
									responseToCompare = previousReponseList.get(previousReponseList.indexOf(responseCopy));
								}
								String[] managers = responseToCompare.getManagers().split(";");
								for (String manager : managers) {
									//Skip if man is empty string or own IP
									if (manager.equals("empty") || manager.equals(MY_IP.toString())) {
										continue;
									}
									SwitchNode managerSwitchNode;
									SwitchNode managerSwitchNodeCopy = new SwitchNode(manager);
									if(currentSwitchNodes.indexOf(managerSwitchNodeCopy)==-1){
										managerSwitchNode = managerSwitchNodeCopy;
									} else{
										managerSwitchNode = currentSwitchNodes.get(currentSwitchNodes.indexOf(managerSwitchNodeCopy));
									}
									// If no path is found to the manager, add it to unreachable managers
									if (findPaths(switchNodeInCurrent, managerSwitchNode, currentSwitchNodes).equals("")) {
										unreachableManagers += manager + ";";
									}
								}
							}

							if (unreachableManagers.equals("")) {
								message += "empty";
							} else {
								message += unreachableManagers;
							}

							String directlyConnected = "";
							for(String directSwitch : directSwitches){
								directlyConnected += directSwitch + ";";
							}

							if(directlyConnected.equals("")){
								message += "\nDIRECTLY-CONNECTED: empty";
							} else{
								message += "\nDIRECTLY-CONNECTED: " + directlyConnected;
							}

							l2.setSourceMACAddress(MacAddress.NONE);
							l2.setDestinationMACAddress(MacAddress.of(switchNodeInCurrent.getDpid()));

							Data l7 = new Data();
							l7.setData(message.getBytes());

							l2.setPayload(l3);
							l3.setPayload(l4);
							l4.setPayload(l7);

							// Construct PACKET-OUT messages to be sent out from directly connected switches
							OFPacketOut packetOut = OFFactories.getFactory(OFVersion.OF_13).buildPacketOut()
									.setData(l2.serialize())
									.setActions(Collections.singletonList((OFAction) OFFactories
											.getFactory(OFVersion.OF_13).actions().output(outPort, 0xffFFffFF)))
									.build();

							rules.add(packetOut);
						}

					}

					// Send the PACKET-OUT messages
					Set<DatapathId> dpids = switchService.getAllSwitchDpids();
					if (dpids != null) {
						for (DatapathId dpid : dpids) {
							IOFSwitch ofSwitch = switchService.getSwitch(dpid);
							if (ofSwitch != null) {
								for (OFPacketOut rule : rules) {
									ofSwitch.write(rule);
								}
							}
						}
					}

				} catch (Exception e) {
					logger.error("Exception in installRulesTask.", e);
				} finally {
					installRulesTask.reschedule(TASK_DELAY, TimeUnit.MILLISECONDS);
				}
			}
		});

		// Start running task
		installRulesTask.reschedule(TASK_DELAY, TimeUnit.MILLISECONDS);
	}

	/*
	 * Queries all the nodes about their local neighbourhood
	 * Also queries other global controllers.
	 * @param None
	 * @return Void
	 */
	private void queryNetwork () {
        try {
            // Retrieve all directly connected switches (distance-1 from global controller) and send queries to them
            Set<DatapathId> dpids = switchService.getAllSwitchDpids();

            if (dpids != null) {
                for (DatapathId dpid : dpids) {
                    IOFSwitch ofSwitch = switchService.getSwitch(dpid);
                    if (ofSwitch != null) {
                        // Send a query to all discovered reachable nodes
                        for (String switchId : querySet) {
                            MacAddress destinationSwitch;
                            IPv4Address destinationIp;
                            // Query controllers
                            if (sun.net.util.IPAddressUtil.isIPv4LiteralAddress(switchId)) {
                                if (!switchId.equals(MY_IP.toString())) {
                                    destinationSwitch = MacAddress.NONE;
                                    destinationIp = IPv4Address.of(switchId);
                                } else {
                                    continue;
                                }
                            } else {
                                destinationSwitch = MacAddress.of(switchId);
                                destinationIp = LOCAL_CONTROLLER_IP;
                            }
                            // Construct the packet-out to be sent (as a query)
                            Ethernet l2 = new Ethernet();
                            l2.setEtherType(EthType.IPv4);
                            l2.setSourceMACAddress(MacAddress.of(dpid));
                            l2.setDestinationMACAddress(destinationSwitch);

                            IPv4 l3 = new IPv4();
                            l3.setSourceAddress(MY_IP);
                            l3.setDestinationAddress(destinationIp);
                            l3.setTtl((byte) 64);
                            l3.setProtocol(IpProtocol.TCP);

                            TCP l4 = new TCP();
                            l4.setSourcePort(TransportPort.of(65003));
                            l4.setDestinationPort(TransportPort.of(67));

                            // Construct the application layer message to be parsed by Local controller
                            String message = "ACTION: QUERY" + "\nIS-DIRECTLY-CONNECTED: ";
                            OFPort outPort;

                            // Send out different ports depending on if switch to query is directly connected or not
                            if (directSwitches.contains(switchId)) {
                                outPort = OFPort.CONTROLLER;
                                message += "true";
                            } else {
                                outPort = OFPort.TABLE;
                                message += "false";
                            }

                            // Include the current label (tag)
                            message += "\nSYNC-LABEL: " + currentLabel;

                            Data l7 = new Data();
                            l7.setData(message.getBytes());

                            l2.setPayload(l3);
                            l3.setPayload(l4);
                            l4.setPayload(l7);

                            // Create the packet-out and write to switch
                            OFPacketOut packetOut = ofSwitch.getOFFactory().buildPacketOut().setData(l2.serialize())
                                    .setActions(Collections.singletonList(
                                            (OFAction) ofSwitch.getOFFactory().actions().output(outPort, 0xffFFffFF)))
                                    .build();

                            ofSwitch.write(packetOut);
                        }

                    }

                }
            }

        } catch (StorageException e) {
            logger.info("Storage exception in queryTask. Terminating process " + e);
        } catch (Exception e) {
            logger.error("Exception in queryTask.", e);
        }
    }


	/*
	 * Adds new links to the current topology of sNodes using Response r
	 * @return void.
	 * @param response, response used to add new links to the existing topology.
	 * @param sNodes, The switchNodes that make up the current topology.
	 */
	private void createTopology(Response responseCurrent, List<SwitchNode> topologyNodes){
		if (!responseCurrent.getNeighbours().equals("empty")) {
			// Format is such that first Dpid1 is always the Dpid of Response r
			SwitchNode switchNode1 = new SwitchNode(responseCurrent.getDpid());
			Set<SwitchNode> addedChildren = new HashSet<SwitchNode>();
			String[] links = responseCurrent.getNeighbours().split(";");
			for (String link : links) {
				String[] dpid_port = link.split("<->");
				String[] dpPair1 = dpid_port[0].split("-");
				String[] dpPair2 = dpid_port[1].split("-");
				String dpid1 = dpPair1[0];
				String port1 = dpPair1[1];
				String dpid2 = dpPair2[0];
				String port2 = dpPair2[1];

				SwitchLink switchLink = new SwitchLink(dpid1, port1, dpid2, port2);

				// Check if it exists in the tree first, if it does then retrieve it

				SwitchNode switchNode2 = new SwitchNode(dpid2);
				if (!topologyNodes.contains(switchNode1)) {
					topologyNodes.add(switchNode1);
				} else {
					switchNode1 = topologyNodes.get(topologyNodes.indexOf(switchNode1));
				}
				if (!topologyNodes.contains(switchNode2)) {
					topologyNodes.add(switchNode2);
				} else {
					switchNode2 = topologyNodes.get(topologyNodes.indexOf(switchNode2));
				}

				addedChildren.add(switchNode2);
				switchNode1.addChild(switchNode2);
				switchNode1.addLink(switchNode2, switchLink);
				switchNode2.addChild(switchNode1);
				switchNode2.addLink(switchNode1, switchLink);
			}
			Set<SwitchNode> childrenToRemove = new HashSet<SwitchNode>(switchNode1.getChildren());
			childrenToRemove.removeAll(addedChildren);
			for(SwitchNode childToRemove : childrenToRemove){
				switchNode1.removeChild(childToRemove);
				switchNode1.removeLink(childToRemove);
				childToRemove.removeChild(switchNode1);
				childToRemove.removeLink(switchNode1);
			}
		}
	}


    /*
     * Returns the port to forward for the shortest path to a switch
     * @return String - Port to forward for shortest path
     * @param dpid1 - Path from switch1.
     * @param dpid2 - Path to switch
     */
	private String findPaths(SwitchNode dpid1, SwitchNode dpid2, List<SwitchNode> pathNodes) {
		// Check if the nodes exist first before finding a path
		if (pathNodes.contains(dpid1) && pathNodes.contains(dpid2)) {
			List<SwitchNode> visited = new ArrayList<SwitchNode>();
			Map<SwitchNode, SwitchNode> previousMap = new HashMap<SwitchNode, SwitchNode>();
			Queue<SwitchNode> queue = new LinkedList<SwitchNode>();
			List<SwitchNode> path = new ArrayList<SwitchNode>();

			queue.add(dpid1);
			visited.add(dpid1);
			SwitchNode current = dpid1;

			// Loop through all the children in a BFS way until we find the
			// switch/node we are looking for
			while (!queue.isEmpty()) {
				current = queue.poll();
				if (current.equals(dpid2)) {
					break;
				} else {
					for (SwitchNode sn : current.getChildren()) {
						if (!visited.contains(sn)) {
							visited.add(sn);
							queue.add(sn);
							previousMap.put(sn, current);
						}
					}
				}
			}

			// We found a path, now find the output port to push to
			if (current.equals(dpid2)) {
				for (SwitchNode node = dpid2; node != null; node = previousMap.get(node)) {
					path.add(node);
				}
				// Get the node to forward to in order to find out port number
				// (the second node in the path)
				Collections.reverse(path);
				SwitchNode portNode = path.get(1);
				// Find the port number
				SwitchLink link = dpid1.getLinks().get(portNode);
				if (link != null) {
					// Get the backup path, where we exclude the receiving node
					// on the link we just got
					List<SwitchNode> excludeNodes = new ArrayList<SwitchNode>();
					excludeNodes.add(portNode);
					String backupLink = "/" + findBackupPath(dpid1, dpid2, excludeNodes, pathNodes);
					if (link.getDpid1().equals(dpid1.getDpid()) && link.getDpid2().equals(portNode.getDpid())) {
						return link.getPort1() + backupLink;
					} else if (link.getDpid2().equals(dpid1.getDpid()) && link.getDpid1().equals(portNode.getDpid())) {
						return link.getPort2() + backupLink;
					} else {
						// This case should never occur, just for debugging.
						logger.info("Error in findPaths: incorrect link information.");
					}
				}
			}

		}

		// No path was found
		//logger.info("findPaths(): no path from {} to {}", dpid1.getDpid(), dpid2.getDpid());
		return "";
	}

    /*
     * Returns the port to forward for the shortest path to a switch
     * @return String - Port to forward for shortest path
     * @param dpid1 - Path from switch1.
     * @param dpid2 - Path to switch
     * @param excludeNodes - List of nodes to exclude for dpid1 in order to find backup paths
     */
	private String findBackupPath(SwitchNode dpid1, SwitchNode dpid2, List<SwitchNode> excludeNodes, List<SwitchNode> sNodes) {
		// Check if the nodes exist first before finding a path
		if (sNodes.contains(dpid1) && sNodes.contains(dpid2)) {
			List<SwitchNode> visited = new ArrayList<SwitchNode>();
			Map<SwitchNode, SwitchNode> previousMap = new HashMap<SwitchNode, SwitchNode>();
			Queue<SwitchNode> queue = new LinkedList<SwitchNode>();
			List<SwitchNode> path = new ArrayList<SwitchNode>();

			queue.add(dpid1);
			visited.add(dpid1);
			SwitchNode current = dpid1;

			// Loop through all the children in a BFS way until we find the
			// switch/node we are looking for
			while (!queue.isEmpty()) {
				current = queue.poll();
				if (current.equals(dpid2)) {
					break;
				} else {
					for (SwitchNode sn : current.getChildren()) {
						if (!(current.equals(dpid1) && excludeNodes.contains(sn))) {
							if (!visited.contains(sn)) {
								visited.add(sn);
								queue.add(sn);
								previousMap.put(sn, current);
							}
						}
					}
				}
			}

			// We found a path, now find the output port to push to
			if (current.equals(dpid2)) {
				for (SwitchNode node = dpid2; node != null; node = previousMap.get(node)) {
					path.add(node);
				}
				// Get the node to forward to in order to find out port number
				// (usually the second node)
				Collections.reverse(path);
				SwitchNode portNode = path.get(1);
				// Find the port number
				SwitchLink link = dpid1.getLinks().get(portNode);
				if (link != null) {
					excludeNodes.add(portNode);
					if (link.getDpid1().equals(dpid1.getDpid()) && link.getDpid2().equals(portNode.getDpid())) {
						return link.getPort1() + "/" + findBackupPath(dpid1, dpid2, excludeNodes, sNodes);
					} else if (link.getDpid2().equals(dpid1.getDpid()) && link.getDpid1().equals(portNode.getDpid())) {
						return link.getPort2() + "/" + findBackupPath(dpid1, dpid2, excludeNodes, sNodes);
					} else {
						// This case should never occur, just for debugging.
						logger.info("Error in findBackupPath: incorrect link information.");
					}
				}
			}

		}

		return "";
	}

	/*
	 * Method to handle packet-in.
	 */
	@Override
	public net.floodlightcontroller.core.IListener.Command receive(IOFSwitch ofSwitch, OFMessage message,
			FloodlightContext context) {
		Ethernet eth = IFloodlightProviderService.bcStore.get(context, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		if (eth.getEtherType() == EthType.IPv4) {
			// We got an IPv4 packet; get the payload from Ethernet
			IPv4 ipv4 = (IPv4) eth.getPayload();

			// Getters for IPv4 destination and source
			IPv4Address destinationIp = ipv4.getDestinationAddress();
			IPv4Address sourceIp = ipv4.getSourceAddress();


			// Check the IP protocol version of the IPv4 packet's payload.
			if (ipv4.getProtocol() == IpProtocol.TCP) {
				// We got a TCP packet; get the payload from IPv4
				TCP tcp = (TCP) ipv4.getPayload();
				// Only handle packets with destination MY_IP or LOCAL_CONTROLLER_IP (for link failure detection)
				if (destinationIp.equals(MY_IP) || destinationIp.equals(LOCAL_CONTROLLER_IP)) {
					Data data = (Data) tcp.getPayload();
					String dataString = new String(data.getData());
					// Parse messages string
					String[] parsedMsg = dataString.split("\\r?\\n");
					// Check that the lines are enough so that we do not get errors when parsing
					if (parsedMsg.length >= 2) {
						String action = parsedMsg[0].replaceAll("\\s+", "").split(":", 2)[1];
						MacAddress sourceMac = eth.getSourceMACAddress();
						MacAddress destinationMac = eth.getDestinationMACAddress();
						//Reply according to what message was recieved
						switch (action) {
							case "QUERY-REPLY":
									handleQueryReply(destinationMac, ofSwitch, parsedMsg, sourceMac, sourceIp);
								break;
							case "INFO":
									infoReply(sourceMac, ofSwitch, parsedMsg, destinationIp);
								break;
							case "QUERY":
									queryReply(sourceIp, destinationMac, parsedMsg, ofSwitch, sourceMac);
								break;
							default:
								break;
						}
					} else {
						//Other TCP message was recieved, send to controller
						Ethernet l2 = new Ethernet();
						l2.setEtherType(eth.getEtherType());
						l2.setSourceMACAddress(eth.getSourceMACAddress());
						l2.setDestinationMACAddress(eth.getDestinationMACAddress());

						IPv4 l3 = new IPv4();
						l3.setSourceAddress(ipv4.getSourceAddress());
						l3.setDestinationAddress(ipv4.getDestinationAddress());
						l3.setTtl(ipv4.getTtl());
						l3.setProtocol(ipv4.getProtocol());

						l2.setPayload(l3);
						l3.setPayload(tcp);

						OFPort outPort = OFPort.FLOOD;


						OFPacketOut po = ofSwitch.getOFFactory().buildPacketOut().setData(l2.serialize())
								.setActions(Collections.singletonList(
										(OFAction) ofSwitch.getOFFactory().actions().output(outPort, 0xffFFffFF)))
								.build();

						ofSwitch.write(po);
						}
				}
				//A ping was recieved, send it to the controller.
			} else if (ipv4.getProtocol()==IpProtocol.ICMP) {
				logger.info("WE JUST GOT A PING FROM {} ", sourceIp);


				ICMP icmp = (ICMP) ipv4.getPayload();

				String switchMac = MacAddress.of(ofSwitch.getId()).toString();
				String dstMac = eth.getDestinationMACAddress().toString();

				Ethernet l2 = new Ethernet();
				l2.setEtherType(eth.getEtherType());
				l2.setSourceMACAddress(eth.getSourceMACAddress());
				l2.setDestinationMACAddress(eth.getDestinationMACAddress());

				IPv4 l3 = new IPv4();
				l3.setSourceAddress(ipv4.getSourceAddress());
				l3.setDestinationAddress(ipv4.getDestinationAddress());
				l3.setTtl(ipv4.getTtl());
				l3.setProtocol(ipv4.getProtocol());


				l2.setPayload(l3);
				l3.setPayload(icmp);

				OFPort outPort = OFPort.FLOOD;


				OFPacketOut po = ofSwitch.getOFFactory().buildPacketOut().setData(l2.serialize())
						.setActions(Collections.singletonList(
								(OFAction) ofSwitch.getOFFactory().actions().output(outPort, 0xffFFffFF)))
						.build();

				ofSwitch.write(po);
				logger.info("SENT THE PING TO {}", destinationIp);

			}
		}

		return Command.CONTINUE;
	}

	public void handleQueryReply(MacAddress destinationMac, IOFSwitch ofSwitch, String[] parsedMessage, MacAddress sourceMac, IPv4Address sourceIp){
		if (destinationMac.toString().equals(MacAddress.of(ofSwitch.getId()).toString())) {
			String linkInfo = parsedMessage[1].replaceAll("\\s+", "").split(":", 2)[1];
			String managers = parsedMessage[2].replaceAll("\\s+", "").split(":", 2)[1];
			String syncLabel = parsedMessage[3].replaceAll("\\s+", "").split(":", 2)[1];
			int syncNumber = Integer.parseInt(syncLabel);

			if (syncNumber == currentLabel) {
				// Retrieve source MAC of the message or IP if it is a manager
				String sourceMacString = sourceMac.toString();
				if (sourceMacString.equals(MacAddress.NONE.toString())) {
					sourceMacString = sourceIp.toString();
				}
				// Add to responses in the Map of tag and responses
				Response response = new Response(sourceMacString, linkInfo, managers, syncNumber);
				List<Response> rList = responses.get(syncNumber);
				rList.remove(response);
				rList.add(response);
				if (responses.size() <= maxResponses) {
					responses.put(syncNumber, rList);
				} else { //C-reset
					responses = new HashMap<Integer, List<Response>>();
					Response localResponse = new Response(MY_IP.toString(), getLocalInfo(), "empty", currentLabel);
					List<Response> localList = new ArrayList<Response>();
					localList.add(localResponse);
					responses.put(syncNumber, localList);
				}

				// Add to current responses
				currentResponseSet.add(sourceMacString);
			}
		}
	}

	public void infoReply(MacAddress sourceMac, IOFSwitch ofSwitch, String[] parsedMessage, IPv4Address destinationIp){
		if (destinationIp.equals(LOCAL_CONTROLLER_IP) && sourceMac.toString().equals(MacAddress.of(ofSwitch.getId()).toString())) {
			String roundTrip = parsedMessage[1].replaceAll("\\s+", "").split(":", 2)[1];
			String lastRoundTrip = parsedMessage[2].replaceAll("\\s+", "").split(":", 2)[1];

			Ethernet l2 = new Ethernet();
			l2.setEtherType(EthType.IPv4);
			l2.setSourceMACAddress(MacAddress.NONE);
			l2.setDestinationMACAddress(MacAddress.NONE);

			IPv4 l3 = new IPv4();
			l3.setSourceAddress(MY_IP);
			l3.setDestinationAddress(LOCAL_CONTROLLER_IP);
			l3.setTtl((byte) 64);
			l3.setProtocol(IpProtocol.TCP);

			TCP l4 = new TCP();
			l4.setSourcePort(TransportPort.of(65003));
			l4.setDestinationPort(TransportPort.of(67));

			// Reply with port info received at controller
			String replyMessage = MY_IP + "-0";
			String dataMessage = "ACTION: INFO" + "\nROUNDTRIP: " + roundTrip + "\nLAST-ROUNDTRIP: "
					+ lastRoundTrip + "\nLINK-INFO: " + replyMessage;

			Data l7 = new Data();
			l7.setData(dataMessage.getBytes());

			l2.setPayload(l3);
			l3.setPayload(l4);
			l4.setPayload(l7);

			OFPacketOut packetOut = ofSwitch.getOFFactory().buildPacketOut()
					.setData(l2.serialize()).setActions(Collections.singletonList((OFAction) ofSwitch
							.getOFFactory().actions().output(OFPort.CONTROLLER, 0xffFFffFF)))
					.setInPort(OFPort.of(1)).build();
			ofSwitch.write(packetOut);
		}
	}

	public void queryReply(IPv4Address sourceIp, MacAddress destinationMac, String[] parsedMessage, IOFSwitch ofSwitch, MacAddress sourceMac){
		if (!sourceIp.equals(MY_IP) && destinationMac.equals(MacAddress.NONE)) {
			// Reply with local topology of global controller and sync label
			String syncLbl = parsedMessage[2].replaceAll("\\s+", "").split(":", 2)[1];

			Ethernet l2 = new Ethernet();
			l2.setEtherType(EthType.IPv4);
			l2.setSourceMACAddress(MacAddress.NONE);
			l2.setDestinationMACAddress(sourceMac);

			IPv4 l3 = new IPv4();
			l3.setSourceAddress(MY_IP);
			l3.setDestinationAddress(sourceIp);
			l3.setTtl((byte) 64);
			l3.setProtocol(IpProtocol.TCP);

			TCP l4 = new TCP();
			l4.setSourcePort(TransportPort.of(65003));
			l4.setDestinationPort(TransportPort.of(67));

			String linkMessage = getLocalInfo();

			// Reply with local topology and received sync label
			String replyMessage = "ACTION: QUERY-REPLY" + "\nLINKS: " + linkMessage + "\nMANAGERS: empty"
					+ "\nSYNC-LABEL: " + syncLbl;

			Data l7 = new Data();
			l7.setData(replyMessage.getBytes());

			l2.setPayload(l3);
			l3.setPayload(l4);
			l4.setPayload(l7);

			// Send back according to switches flow table
			OFPacketOut packetOut = ofSwitch.getOFFactory().buildPacketOut().setData(l2.serialize())
					.setActions(Collections.singletonList(
							(OFAction) ofSwitch.getOFFactory().actions().output(OFPort.TABLE, 0xffFFffFF)))
					.build();

			ofSwitch.write(packetOut);
		}
	}

	/*
	 * @return The local topology of the global controller.
	 */
	private String getLocalInfo() {
		String linkInfo = "";

		Set<DatapathId> dirActSwitches = switchService.getAllSwitchDpids();
		for (DatapathId dpid : dirActSwitches) {
			linkInfo += MY_IP + "-0<->" + MacAddress.of(dpid).toString() + "-1;";
		}

		if (linkInfo.isEmpty()) {
			linkInfo = "empty";
		}

		return linkInfo;
	}

	@Override
	public void switchAdded(DatapathId switchId) {
		querySet.add(MacAddress.of(switchId).toString());
		// Disable LLDP packets being sent out automatically on the newly added
		// switch
		discoveredNodes.add(MacAddress.of(switchId).toString());
		directSwitches.add(MacAddress.of(switchId).toString());
		// Disable from running LLDP normal module
		IOFSwitch sw = switchService.getSwitch(switchId);
		Collection<OFPort> collection = sw.getEnabledPortNumbers();
		if (collection != null) {
			for (OFPort ofPort : collection) {
				linkDiscService.AddToSuppressLLDPs(switchId, ofPort);
			}
		}
	}

	@Override
	public void switchRemoved(DatapathId switchId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void switchActivated(DatapathId switchId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void switchPortChanged(DatapathId switchId, OFPortDesc port, PortChangeType type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void switchChanged(DatapathId switchId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void switchDeactivated(DatapathId switchId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void topologyChanged(List<LDUpdate> linkUpdates) {
		// TODO Auto-generated method stub
	}

}
