package net.floodlightcontroller.localcontroller;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.projectfloodlight.openflow.protocol.OFBucket;
import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFFlowAdd;
import org.projectfloodlight.openflow.protocol.OFFlowDelete;
import org.projectfloodlight.openflow.protocol.OFFlowStatsEntry;
import org.projectfloodlight.openflow.protocol.OFFlowStatsReply;
import org.projectfloodlight.openflow.protocol.OFGroupAdd;
import org.projectfloodlight.openflow.protocol.OFGroupModify;
import org.projectfloodlight.openflow.protocol.OFGroupType;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFPacketOut;
import org.projectfloodlight.openflow.protocol.OFPortDesc;
import org.projectfloodlight.openflow.protocol.OFStatsReply;
import org.projectfloodlight.openflow.protocol.OFStatsRequest;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.action.OFActionGroup;
import org.projectfloodlight.openflow.protocol.action.OFActionOutput;
import org.projectfloodlight.openflow.protocol.action.OFActions;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.Match.Builder;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFBufferId;
import org.projectfloodlight.openflow.types.OFGroup;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.TableId;
import org.projectfloodlight.openflow.types.TransportPort;
import org.projectfloodlight.openflow.types.U64;

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
import net.floodlightcontroller.linkdiscovery.ILinkDiscovery.UpdateOperation;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.core.IFloodlightProviderService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Set;

import net.floodlightcontroller.packet.Data;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.TCP;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.threadpool.IThreadPoolService;
import net.floodlightcontroller.topology.ITopologyListener;
import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.util.OFMessageUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListenableFuture;

public class LocalController implements IOFMessageListener, IFloodlightModule, IOFSwitchListener, ITopologyListener {

	protected IFloodlightProviderService floodlightProvider;
	protected IOFSwitchService switchService;
	protected ITopologyService topologyService;
	protected IRoutingService routingService;
	protected ILinkDiscoveryService linkDiscService;
	protected IThreadPoolService threadPoolService;

	// List of discovered Switches so far by DPID
	protected Map<String, Map<Integer, Integer>> roundtrip;
	protected Map<String, Map<Integer, Integer>> roundtripCounter;
	protected Map<String, Map<Integer, Integer>> lastRoundtrip;
	protected Map<String, Map<Integer, String>> localInfo;
	protected Map<String, Set<String>> managers;
	protected Map<String, Set<Integer>> groupNrMap;
	protected int threshold;
	protected Object lock;
	protected static Logger logger;

	protected SingletonTask discoveryTask;
	protected SingletonTask getFlowsTask;
	protected static final int TASK_DELAY = 500;
	protected static final int MAX_PRIORITY = 32768;

	// Change accordingly
	protected static final String MY_IP = "0.0.0.0";
	protected static final int MAX_NR_OF_CONTROLLERS = 7;
	protected static final int GROUP_DISTR = Integer.MAX_VALUE/MAX_NR_OF_CONTROLLERS;
	
	protected List<String> managerIps;

	@Override
	public String getName() {
		return LocalController.class.getSimpleName();
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

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		switchService = context.getServiceImpl(IOFSwitchService.class);
		topologyService = context.getServiceImpl(ITopologyService.class);
		routingService = context.getServiceImpl(IRoutingService.class);
		linkDiscService = context.getServiceImpl(ILinkDiscoveryService.class);
		threadPoolService = context.getServiceImpl(IThreadPoolService.class);
		logger = LoggerFactory.getLogger(LocalController.class);
		roundtrip = new HashMap<String, Map<Integer, Integer>>();
		roundtripCounter = new HashMap<String, Map<Integer, Integer>>();
		lastRoundtrip = new HashMap<String, Map<Integer, Integer>>();
		localInfo = new HashMap<String, Map<Integer, String>>();
		managers = new HashMap<String, Set<String>>();
		groupNrMap = new HashMap<String, Set<Integer>>();
		threshold = 25;
		lock = new Object();
		managerIps = new ArrayList<String>();
	}

	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		// Listen to PACKET-IN messages
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		switchService.addOFSwitchListener(this);
		topologyService.addListener(this);

		ScheduledExecutorService ses = threadPoolService.getScheduledExecutor();

		// Thread for discovering the local neighourhood of every switch, using INFO messages.
		discoveryTask = new SingletonTask(ses, new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("discoveryTask running...");
					//For all switches, send out packet-outs through all the ports and find out what we get back.
					Set<DatapathId> dpids = switchService.getAllSwitchDpids();
					if (dpids != null) {
						for (DatapathId dpid : dpids) {
							IOFSwitch ofSwitch = switchService.getSwitch(dpid);
							if (ofSwitch != null) {
								String switchMac = MacAddress.of(ofSwitch.getId()).toString();
								for (OFPort port : ofSwitch.getEnabledPortNumbers()) { // Send through all active ports
									int portNumber = port.getPortNumber();
									if (portNumber > 0) { // Local PORT is not considered
										//Add our MAC and port to the message, to match with the MAC and port of a message we get back.
										String replyMessage = switchMac + "-" + portNumber;
										//Set up the different layers for a packet.
										Ethernet l2 = new Ethernet();
										l2.setEtherType(EthType.IPv4);
										l2.setSourceMACAddress(MacAddress.of(dpid));
										l2.setDestinationMACAddress(MacAddress.NONE);

										IPv4 l3 = new IPv4();
										l3.setSourceAddress(IPv4.toIPv4Address(MY_IP));
										l3.setDestinationAddress(IPv4.toIPv4Address(MY_IP));
										l3.setTtl((byte) 64);
										l3.setProtocol(IpProtocol.TCP);

										TCP l4 = new TCP();
										l4.setSourcePort(TransportPort.of(65003));
										l4.setDestinationPort(TransportPort.of(67));

										String roundTrip;
										String lastRoundTrip;

										//Failure detector, rtValue is how many times we have asked the switch if it is alive.
										synchronized (lock) {
											Map<Integer, Integer> roundTripValues = roundtrip.get(switchMac);
											Map<Integer, Integer> lastRoundTripValues = lastRoundtrip.get(switchMac);

											Integer roundTripValue = roundTripValues.get(portNumber);
											Integer lastRoundTripValue = lastRoundTripValues.get(portNumber);

											if (roundTripValue == null) {
												roundTripValues.put(portNumber, 0);
												roundTrip = "0";
											} else {
												roundTrip = Integer.toString(roundTripValue);
											}

											if (lastRoundTripValue == null) {
												lastRoundTripValues.put(portNumber, 0);
												lastRoundTrip = "0";
											} else {
												lastRoundTrip = Integer.toString(lastRoundTripValue);
											}
										}

										String dataMessage = "ACTION: INFO" + "\nROUNDTRIP: " + roundTrip + "\nLAST-ROUNDTRIP: "
												+ lastRoundTrip + "\nLINK-INFO: " + replyMessage;

										Data l7 = new Data();
										l7.setData(dataMessage.getBytes());

										l2.setPayload(l3);
										l3.setPayload(l4);
										l4.setPayload(l7);

										OFPacketOut packetOut = ofSwitch.getOFFactory().buildPacketOut().setData(l2.serialize())
												.setActions(Collections.singletonList((OFAction) ofSwitch.getOFFactory()
														.actions().output(port, 0xffFFffFF)))
												.build();
										ofSwitch.write(packetOut);
										
										// Send additional packet to Global controller if port is 1 (we assume all connections to be port 1)
										if(portNumber==1){
											OFPacketOut packetOutGlobal = ofSwitch.getOFFactory().buildPacketOut().setData(l2.serialize())
													.setActions(Collections.singletonList((OFAction) ofSwitch.getOFFactory()
															.actions().output(OFPort.CONTROLLER, 0xffFFffFF)))
													.build();
											ofSwitch.write(packetOutGlobal);
										}
									}
								}
							}
						}
					}
				} catch (Exception e) {
					logger.info("Exception in discoveryTask. Terminating process " + e);
				} finally {
					discoveryTask.reschedule(TASK_DELAY, TimeUnit.MILLISECONDS);
				}
			}
		});

		// Thread used to get flows from the switches, used to check what managers a switch has.
		getFlowsTask = new SingletonTask(ses, new Runnable() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				try {
					logger.info("getFlowsTask running...");
					// Send through all directly connected switches
					Set<DatapathId> dpids = switchService.getAllSwitchDpids();
					if (dpids != null) {
						for (DatapathId dpid : dpids) {
							IOFSwitch ofSwitch = switchService.getSwitch(dpid);
							ListenableFuture<?> future;
							List<OFStatsReply> replies = null;
							Match match = ofSwitch.getOFFactory().buildMatch().build();
							Set<String> currentManagers = new HashSet<String>();
							OFStatsRequest<?> request = ofSwitch.getOFFactory().buildFlowStatsRequest().setMatch(match)
									.setOutPort(OFPort.ANY).setTableId(TableId.ALL).setOutGroup(OFGroup.ANY).build();
							try {
								future = ofSwitch.writeStatsRequest(request);
								replies = (List<OFStatsReply>) future.get(TASK_DELAY, TimeUnit.MILLISECONDS);
								if (replies != null) {
									for (OFStatsReply reply : replies) {
										OFFlowStatsReply statsReply = (OFFlowStatsReply) reply;
										List<OFFlowStatsEntry> flowEntries = statsReply.getEntries();
										for (OFFlowStatsEntry flowEntry : flowEntries) {
											// See if these match a manager i.e. priority=0 and match has any source ip (this is to find out the managers of each switch)
											if (flowEntry.getPriority() == 0) {
												IPv4Address address = flowEntry.getMatch().get(MatchField.IPV4_SRC);
												if (address != null) {
													//Add the manager to the list of managers.
													currentManagers.add(address.toString());
												}
											}
										}
									}
								}
							} catch (Exception e) {
								logger.error("Failure retrieving statistics from switch {}. {}", ofSwitch, e);
							}
							managers.put(MacAddress.of(dpid).toString(), currentManagers);
						}
					}

				} catch (Exception e) {
					logger.error("Exception in getFlowsTask.", e);
				} finally {
					getFlowsTask.reschedule(TASK_DELAY, TimeUnit.MILLISECONDS);
				}
			}
		});

		// Start running task
		discoveryTask.reschedule(TASK_DELAY, TimeUnit.MILLISECONDS);
		getFlowsTask.reschedule(TASK_DELAY, TimeUnit.MILLISECONDS);
	}

	/*
	 * A packet-in was recieved.
	 */
	@Override
	public net.floodlightcontroller.core.IListener.Command receive(IOFSwitch ofSwitch, OFMessage message,
			FloodlightContext context) {
		Ethernet ethernet = IFloodlightProviderService.bcStore.get(context, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		if (ethernet.getEtherType() == EthType.IPv4) {
			/* We got an IPv4 packet; get the payload from Ethernet */
			IPv4 ipv4 = (IPv4) ethernet.getPayload();

			IPv4Address sourceIp = ipv4.getSourceAddress();
			IPv4Address destinationIp = ipv4.getDestinationAddress();
			String switchMac = MacAddress.of(ofSwitch.getId()).toString();
			String destinationMac = ethernet.getDestinationMACAddress().toString();

			// Note that this only works for PacketIn messages, but in this module we only listen to PacketIn
			OFPacketIn packetIn = (OFPacketIn) message;

			if (ipv4.getProtocol() == IpProtocol.TCP) {
				/* We got a TCP packet; get the payload from IPv4 */
				TCP tcp = (TCP) ipv4.getPayload();
				// Message received from Master Controller IP (c0)
				if (destinationIp.toString().equals(MY_IP)) {
					Data data = (Data) tcp.getPayload();
					String dataString = new String(data.getData());

					// Parse the message sent from other controllers
					String[] parsedMessage = dataString.split("\\r?\\n");
					if (parsedMessage.length >= 3) {
						String action = parsedMessage[0].replaceAll("\\s+", "").split(":", 2)[1];
						String targetSwitch = parsedMessage[1].replaceAll("\\s+", "").split(":", 2)[1];
						String requestingIP = parsedMessage[2].replaceAll("\\s+", "").split(":", 2)[1];
						//Chose the right function depending on the action requested by the global controller.
						switch (action) {
							case "INSTALL-RULES": {
										installRules(switchMac, destinationMac, sourceIp, requestingIP, parsedMessage, targetSwitch, ofSwitch);
								break;
							}
							case "QUERY": {
										queryReply(destinationMac, switchMac, parsedMessage, ethernet, ofSwitch, packetIn, sourceIp);
								}
								break;
							case "INFO": {
										infoReply(packetIn, targetSwitch, requestingIP, parsedMessage, switchMac);
								break;
							}

							default: {
								logger.info("Invalid message targeted to local controller {}", action);
							}
						}
					}
				}
			}
		}
		return Command.CONTINUE;
	}

	public void installRules(String switchMac, String destinationMac, IPv4Address sourceIp, String requestingIP, String[] parsedMessage, String targetSwitch, IOFSwitch ofSwitch) {
		if (switchMac.equals(destinationMac)) {
			if (!managerIps.contains(sourceIp.toString())) {
				managerIps.add(sourceIp.toString());
			}
			int managerIndex = managerIps.indexOf(sourceIp.toString());
			int syncLabel = Integer.parseInt(requestingIP);
			String unreachableManagers = parsedMessage[3].replaceAll("\\s+", "").split(":", 2)[1];
			String directlyConnectedSwitches = parsedMessage[4].replaceAll("\\s+", "").split(":", 2)[1];
			Set<String> directSwitches = new HashSet<String>(Arrays.asList(directlyConnectedSwitches.split(";")));
			if (!targetSwitch.equals("empty")) {
				OFFactory myFactory = ofSwitch.getOFFactory();
				String[] targets = targetSwitch.split(";");

				int groupNr = managerIndex * GROUP_DISTR;
				for (String target : targets) {
					String[] pairs = target.split("-");
					String[] paths = pairs[1].split("/");
					//logger.info("Data received from Manager: {}", srcIp);

					MacAddress destinationSwitch;

					// Install rules from switches to controllers/hosts
					// Can replace this with your regex check
					if (sourceIp.toString().equals(pairs[0])) {
						destinationSwitch = MacAddress.NONE;
					} else if (sun.net.util.IPAddressUtil.isIPv4LiteralAddress(pairs[0])) {
						continue;
					} else {
						destinationSwitch = MacAddress.of(pairs[0]);
					}

					// Install primary path with backups
					groupNr++;
					List<OFBucket> buckets = new ArrayList<OFBucket>();
					for (int i = 0; i < paths.length; i++) {
						List<OFAction> actions = new ArrayList<OFAction>();
						actions.add((OFAction) ofSwitch.getOFFactory().actions()
								.buildOutput().setMaxLen(0xffFFffFF)
								.setPort(OFPort
												 .of(Integer.valueOf(paths[i])))
								.build());
						if (directSwitches.contains(switchMac) && destinationSwitch.equals(MacAddress.NONE)) {
							actions.add((OFAction) ofSwitch.getOFFactory().actions()
									.buildOutput().setMaxLen(0xffFFffFF)
									.setPort(OFPort.CONTROLLER)
									.build());
						}
						buckets.add(
								ofSwitch.getOFFactory().buildBucket()
										.setWatchPort(OFPort.of(Integer.valueOf(paths[i])))
										.setWatchGroup(OFGroup.ZERO)
										.setActions(actions)
										.build());

					}

					//Check if groupNr already exists for MAC
					if (groupNrMap.get(switchMac).contains(groupNr)) {
						OFGroupModify groupModify = ofSwitch.getOFFactory().buildGroupModify()
								.setGroup(OFGroup.of(groupNr))
								.setGroupType(OFGroupType.FF).setBuckets(buckets).build();
						ofSwitch.write(groupModify);
					} else {
						groupNrMap.get(switchMac).add(groupNr);
						OFGroupAdd groupAdd = ofSwitch.getOFFactory().buildGroupAdd()
								.setGroup(OFGroup.of(groupNr))
								.setGroupType(OFGroupType.FF).setBuckets(buckets).build();
						ofSwitch.write(groupAdd);
					}

					// Actions
					List<OFAction> actionList = new ArrayList<OFAction>();
					OFActions actions = myFactory.actions();

					// Output action
					OFActionGroup actionGroup = actions.buildGroup()
							.setGroup(OFGroup.of(groupNr)).build();

					actionList.add(actionGroup);

					// Build Match and FlowMod depending on if Switch has a Global controller or not
					Builder builder = myFactory.buildMatch().setExact(MatchField.ETH_DST, destinationSwitch)
							.setExact(MatchField.ETH_TYPE, EthType.IPv4);
					if (directSwitches.contains(destinationSwitch.toString()) || destinationSwitch.equals(MacAddress.NONE)) {
						// Match for destination
						builder.setExact(MatchField.IPV4_DST, sourceIp);

						// Add packet rules from ping/iperf
						Match matchPackets = myFactory.buildMatch()
								.setExact(MatchField.IPV4_DST, sourceIp)
								.setExact(MatchField.ETH_TYPE, EthType.IPv4).build();

						OFFlowAdd flowPackets = myFactory.buildFlowAdd().setCookie(U64.ofRaw(managerIndex * GROUP_DISTR + ((syncLabel % GROUP_DISTR) + 2)))
								.setBufferId(OFBufferId.NO_BUFFER).setMatch(matchPackets)
								.setActions(actionList).setPriority(1).build();

						ofSwitch.write(flowPackets);
					} else {
						// Match for source
						builder.setExact(MatchField.IPV4_SRC, sourceIp);
					}

					// FlowMod/FlowAdd packet message.
					OFFlowAdd flowAdd = myFactory.buildFlowAdd().setCookie(U64.ofRaw(managerIndex * GROUP_DISTR + ((syncLabel % GROUP_DISTR) + 2)))
							.setBufferId(OFBufferId.NO_BUFFER).setMatch(builder.build())
							.setActions(actionList).setPriority(MAX_PRIORITY - 2).build();

					ofSwitch.write(flowAdd);
				}
			}

			OFFactory myFactory = ofSwitch.getOFFactory();

			// Switch destination rule to controller if directed switch to Global controller
			if (directSwitches.contains(switchMac)) {
				Match mb = myFactory.buildMatch()
						.setExact(MatchField.ETH_DST, MacAddress.of(ofSwitch.getId()))
						.setExact(MatchField.IPV4_DST, sourceIp)
						.setExact(MatchField.ETH_TYPE, EthType.IPv4).build();

				// Actions
				List<OFAction> actionList = new ArrayList<OFAction>();
				OFActions actions = myFactory.actions();

				// Output action
				OFActionOutput output = actions.buildOutput().setMaxLen(0xFFffFFff)
						.setPort(OFPort.CONTROLLER).build();

				actionList.add(output);

				// FlowMod/FlowAdd packet message.
				OFFlowAdd flowAdd = myFactory.buildFlowAdd().setCookie(U64.ofRaw(managerIndex * GROUP_DISTR + ((syncLabel % GROUP_DISTR) + 2)))
						.setBufferId(OFBufferId.NO_BUFFER)
						.setMatch(mb).setActions(actionList).setPriority(MAX_PRIORITY).build();

				ofSwitch.write(flowAdd);
			}

			// Install manager rule-------------
			// Match
			Match managerMatch = myFactory.buildMatch().setExact(MatchField.IPV4_SRC, sourceIp)
					.setExact(MatchField.ETH_TYPE, EthType.IPv4)
					.setExact(MatchField.IPV4_DST, sourceIp)
					.build();

			// Actions
			List<OFAction> actionListManager = new ArrayList<OFAction>();
			OFActions actionsManager = myFactory.actions();

			// Output action
			OFActionOutput outputManager = actionsManager.buildOutput().setMaxLen(0xFFffFFff)
					.setPort(OFPort.CONTROLLER).build();

			actionListManager.add(outputManager);

			//FlowAdd packet message.
			OFFlowAdd flowAddManager = myFactory.buildFlowAdd().setCookie(U64.ofRaw(managerIndex * GROUP_DISTR + ((syncLabel % GROUP_DISTR) + 2)))
					.setBufferId(OFBufferId.NO_BUFFER)
					.setMatch(managerMatch).setActions(actionListManager).setPriority(0).build();

			ofSwitch.write(flowAddManager);

			// Delete flows that are 2 rounds behind
			OFFlowDelete flowDelete = ofSwitch.getOFFactory().buildFlowDelete()
					.setCookie(U64.ofRaw(managerIndex * GROUP_DISTR + (((syncLabel - 2) % GROUP_DISTR) + 2)))
					.setCookieMask(U64.NO_MASK)
					.build();
			ofSwitch.write(flowDelete);


			// Delete rules from unreachable managers
			String[] managers = unreachableManagers.split(";");
			for (String manager : managers) {
				if (!manager.equals("empty") && sun.net.util.IPAddressUtil.isIPv4LiteralAddress(manager)) {
					OFFlowDelete flowManDeleteManager = ofSwitch.getOFFactory().buildFlowDelete()
							.setMatch(myFactory.buildMatch().setExact(MatchField.IPV4_SRC, IPv4Address.of(manager))
											  .setExact(MatchField.ETH_TYPE, EthType.IPv4).build())
							.build();

					OFFlowDelete flowManagerDelete = ofSwitch.getOFFactory().buildFlowDelete()
							.setMatch(myFactory.buildMatch().setExact(MatchField.IPV4_DST, IPv4Address.of(manager))
											  .setExact(MatchField.ETH_TYPE, EthType.IPv4).build())
							.build();

					ofSwitch.write(flowManDeleteManager);
					ofSwitch.write(flowManagerDelete);
				}
			}
		}
	}
	public void queryReply(String destinationMac, String switchMac, String[] parsedMessage, Ethernet ethernet, IOFSwitch ofSwitch, OFPacketIn packetIn, IPv4Address sourceIp) {
		// Check if the destination matches the switch mac
		if (destinationMac.equals(switchMac)) {
			String isDirectlyConnected = parsedMessage[1].replaceAll("\\s+", "").split(":", 2)[1];
			String syncLabel = parsedMessage[2].replaceAll("\\s+", "").split(":", 2)[1];

			// Construct SwitchLink in String format and
			// send back to requesting In-band controller
			// (source mac)...
			Ethernet l2 = new Ethernet();
			l2.setEtherType(EthType.IPv4);
			l2.setSourceMACAddress(MacAddress.of(ofSwitch.getId()));
			l2.setDestinationMACAddress(ethernet.getSourceMACAddress());

			IPv4 l3 = new IPv4();
			l3.setSourceAddress(IPv4.toIPv4Address(MY_IP));
			// Send back to requesting global controller
			l3.setDestinationAddress(sourceIp);
			l3.setTtl((byte) 64);
			l3.setProtocol(IpProtocol.TCP);

			TCP l4 = new TCP();
			l4.setSourcePort(TransportPort.of(65003));
			l4.setDestinationPort(TransportPort.of(67));

			// Send all links for specific switch. Check
			// roundtripCounter < threshold
			String linkMessage = "";
			Map<Integer, String> linkInfo = localInfo.get(switchMac);

			Map<Integer, Integer> switchRtc = roundtripCounter.get(switchMac);
			if (linkInfo != null && switchRtc != null) {
				for (Entry<Integer, String> entry : linkInfo.entrySet()) {
					Integer port = entry.getKey();
					String link = entry.getValue();
					Integer roundTripCounter = switchRtc.get(port);
					if (roundTripCounter < threshold) {
						linkMessage += link + ";";
					}
				}
			}

			if (linkMessage.isEmpty()) {
				linkMessage = "empty";
			}


			// Get all managers for a switch
			String managerMessage = "";
			Set<String> currentManagers = this.managers.get(destinationMac);
			for (String man : currentManagers) {
				managerMessage += man + ";";
			}
			if (managerMessage.isEmpty()) {
				managerMessage = "empty";
			}

			String replyMessage = "ACTION: QUERY-REPLY" + "\nLINKS: " + linkMessage + "\nMANAGERS: "
					+ managerMessage + "\nSYNC-LABEL: " + syncLabel;

			OFPort outPort;
			// Check if the requested controller is querying
			// a directly connected switch
			if (isDirectlyConnected.equals("true")) {
				outPort = OFPort.CONTROLLER;
			} else {
				outPort = OFMessageUtils.getInPort(packetIn);
			}

			Data l7 = new Data();
			l7.setData(replyMessage.getBytes());

			l2.setPayload(l3);
			l3.setPayload(l4);
			l4.setPayload(l7);

			// Send back according to switches flow table
			OFPacketOut packetOut = ofSwitch.getOFFactory().buildPacketOut().setData(l2.serialize())
					.setActions(Collections.singletonList(
							(OFAction) ofSwitch.getOFFactory().actions().output(outPort, 0xffFFffFF)))
					.build();

			ofSwitch.write(packetOut);
			//logger.info("QUERY-REPLY TO MANAGER {} with Switch {}, message: " + replyMsg, srcIp, switchMac);
		}
	}

	public void infoReply(OFPacketIn packetIn, String targetSwitch, String requestingIp, String[] parsedMessage, String switchMac){
		Integer receivedPort = OFMessageUtils.getInPort(packetIn).getPortNumber();
		if (receivedPort > 0) {
			Integer roundTrip = Integer.valueOf(targetSwitch);
			Integer lastRoundTrip = Integer.valueOf(requestingIp);

			String linkInfo = parsedMessage[3].replaceAll("\\s+", "").split(":", 2)[1];
			String discoveredLink = switchMac + "-" + receivedPort + "<->" + linkInfo;
			//logger.info("Received from switchMac: {}", switchMac);
			//logger.info("With receivedPort {} , and linkInfo {}", receivedPort, linkInfo);
			synchronized (lock) {

				Map<Integer, Integer> roundTripValues = roundtrip.get(switchMac);
				Map<Integer, Integer> lastRoundTripValues = lastRoundtrip.get(switchMac);

				if (lastRoundTripValues != null) {
					Integer lastroundTripValue = lastRoundTripValues.get(receivedPort);
					if (lastroundTripValue != null) {
						if (lastRoundTrip > lastroundTripValue) {
							lastRoundTripValues.put(receivedPort, lastRoundTrip);
						}
					} else {
						logger.info("lastrtValue is null.");
					}
				} else {
					logger.info("lastrtValues is null.");
				}

				if (roundTripValues != null) {
					Integer roundTripValue = roundTripValues.get(receivedPort);
					if (roundTripValue != null) {
						if (roundTrip >= roundTripValue) {
							// Update roundtripCounter
							Map<Integer, Integer> switchRoundTripCounter = roundtripCounter.get(switchMac);
							Map<Integer, Integer> updatedRoundTripCounter = new HashMap<Integer, Integer>();
							for (Entry<Integer, Integer> entry : switchRoundTripCounter.entrySet()) {
								updatedRoundTripCounter.put(entry.getKey(),
											   Math.max(0, Math.min(threshold, entry.getValue() + 1)));
							}
							updatedRoundTripCounter.put(receivedPort, 0);
							roundtripCounter.put(switchMac, updatedRoundTripCounter);
							roundTripValues.put(receivedPort, roundTrip + 1);
							localInfo.get(switchMac).put(receivedPort, discoveredLink);
						}
					} else {
						logger.info("rtValue is null.");
					}
				} else {
					logger.info("rtValue is null.");
				}

			}
		}
	}

	@Override
	public void switchAdded(DatapathId switchId) {
		// Disable LLDP packets being sent out automatically on the newly added
		// switch
		IOFSwitch sw = switchService.getSwitch(switchId);
		Collection<OFPort> c = sw.getEnabledPortNumbers();
		if (c != null) {
			for (OFPort ofp : c) {
				linkDiscService.AddToSuppressLLDPs(switchId, ofp);
				// logger.info("Port {} disabled from LLDP", ofp);
			}
		}

		String switchMac = MacAddress.of(switchId).toString();
		roundtrip.put(switchMac, new HashMap<Integer, Integer>());
		roundtripCounter.put(switchMac, new HashMap<Integer, Integer>());
		lastRoundtrip.put(switchMac, new HashMap<Integer, Integer>());
		localInfo.put(switchMac, new HashMap<Integer, String>());
		managers.put(switchMac, new HashSet<String>());
		groupNrMap.put(switchMac, new HashSet<Integer>());
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

	/*
	 * Get notified when topology has changed. Install flows based on new
	 * topology (remember to clear old flows first or modify them).
	 * @param linkUpdates
	 */
	@Override
	public void topologyChanged(List<LDUpdate> linkUpdates) {
		// Detect link downs
		for (LDUpdate ld : linkUpdates) {
			if (ld.getOperation().equals(UpdateOperation.PORT_DOWN)) {
				logger.info("LINK DOWN DETECTED! TIME: {}", System.nanoTime());
				break;
			}
		}
	}

}
