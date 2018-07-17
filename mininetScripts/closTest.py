#!/usr/bin/python

from mininet.net import Mininet
from mininet.node import Controller, RemoteController, OVSController
from mininet.node import CPULimitedHost, Host, Node
from mininet.node import OVSSwitch, UserSwitch
from mininet.node import IVSSwitch
from mininet.cli import CLI
from mininet.log import setLogLevel, info
from mininet.link import TCLink, Intf
from subprocess import call
import time
import os

class InbandController( RemoteController ):

	def checkListening( self ):
		"Overridden to do nothing."
		return

def myNetwork():

	net = Mininet( topo=None,
					build=False)

	info( '*** Adding controller\n' )
	# Change port and IP accordingly (c0 is Local controller, rest are Global controllers)
	c1=net.addController(name='c1',
					controller=InbandController,
					ip='10.0.0.1',
					protocol='tcp',
					port=6653)

	c2=net.addController(name='c2',
					controller=InbandController,
					ip='10.0.0.2',
					protocol='tcp',
					port=6653)

	c3=net.addController(name='c3',
					controller=InbandController,
					ip='10.0.0.3',
					protocol='tcp',
					port=6653)

	c0=net.addController(name='c0',
					controller=RemoteController,
					ip='0.0.0.0',
					protocol='tcp',
					port=6653)

	info( '*** Add switches\n')
	s1 = net.addSwitch('s1', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s2 = net.addSwitch('s2', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s3 = net.addSwitch('s3', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s4 = net.addSwitch('s4', cls=OVSSwitch, inband=True, protocols='OpenFlow13') #Top 4
	s5 = net.addSwitch('s5', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s6 = net.addSwitch('s6', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s7 = net.addSwitch('s7', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s8 = net.addSwitch('s8', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s9 = net.addSwitch('s9', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s10 = net.addSwitch('s10', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s11 = net.addSwitch('s11', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s12 = net.addSwitch('s12', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s13 = net.addSwitch('s13', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s14 = net.addSwitch('s14', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s15 = net.addSwitch('s15', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s16 = net.addSwitch('s16', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s17 = net.addSwitch('s17', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s18 = net.addSwitch('s18', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s19 = net.addSwitch('s19', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s20 = net.addSwitch('s20', cls=OVSSwitch, inband=True, protocols='OpenFlow13')




	info( '*** Add hosts\n')
	h1 = net.addHost('h1', cls=Host, ip='10.0.0.1', defaultRoute=None)
	h2 = net.addHost('h2', cls=Host, ip='10.0.0.2', defaultRoute=None)
	h3 = net.addHost('h3', cls=Host, ip='10.0.0.3', defaultRoute=None)


	# Add in link options for different experiments
	linkopts = dict(bw=1000)

	info( '*** Add links\n')
	net.addLink(s1, h1, cls=TCLink,**linkopts) #s1 Perth: 5 single switches attached
	net.addLink(s20, h2, cls=TCLink,**linkopts)	
	net.addLink(s7, h3, cls=TCLink,**linkopts)
	net.addLink(s1, s5, cls=TCLink,**linkopts)
	net.addLink(s1, s9, cls=TCLink,**linkopts)
	net.addLink(s1, s13, cls=TCLink,**linkopts)
	net.addLink(s1, s17, cls=TCLink,**linkopts)
	net.addLink(s2, s5, cls=TCLink,**linkopts)
	net.addLink(s2, s9, cls=TCLink,**linkopts)
	net.addLink(s2, s13, cls=TCLink,**linkopts)
	net.addLink(s2, s17, cls=TCLink,**linkopts)
	net.addLink(s3, s6, cls=TCLink,**linkopts)
	net.addLink(s3, s10, cls=TCLink,**linkopts)
	net.addLink(s3, s14, cls=TCLink,**linkopts)
	net.addLink(s3, s18, cls=TCLink,**linkopts)
	net.addLink(s4, s6, cls=TCLink,**linkopts)
	net.addLink(s4, s10, cls=TCLink,**linkopts)
	net.addLink(s4, s14, cls=TCLink,**linkopts)
	net.addLink(s4, s18, cls=TCLink,**linkopts)
	net.addLink(s5, s7, cls=TCLink,**linkopts)
	net.addLink(s5, s8, cls=TCLink,**linkopts)
	net.addLink(s6, s7, cls=TCLink,**linkopts)
	net.addLink(s6, s8, cls=TCLink,**linkopts)
	net.addLink(s9, s11, cls=TCLink,**linkopts)
	net.addLink(s9, s12, cls=TCLink,**linkopts)
	net.addLink(s10, s11, cls=TCLink,**linkopts)
	net.addLink(s10, s12, cls=TCLink,**linkopts)
	net.addLink(s13, s15, cls=TCLink,**linkopts)
	net.addLink(s13, s16, cls=TCLink,**linkopts)
	net.addLink(s14, s15, cls=TCLink,**linkopts)
	net.addLink(s14, s16, cls=TCLink,**linkopts)
	net.addLink(s17, s19, cls=TCLink,**linkopts)
	net.addLink(s17, s20, cls=TCLink,**linkopts)
	net.addLink(s18, s19, cls=TCLink,**linkopts)
	net.addLink(s18, s20, cls=TCLink,**linkopts)




	info( '*** Starting network\n')
	net.build()
	info( '*** Starting controllers\n')
	c0.start()
	c1.start()
	c2.start()
	c3.start()

	info( '*** Starting switches\n')
	net.get('s1').start([c0, c1])
	net.get('s2').start([c0])
	net.get('s3').start([c0])
	net.get('s4').start([c0])
	net.get('s5').start([c0])
	net.get('s6').start([c0])
	net.get('s7').start([c0, c3])
	net.get('s8').start([c0])
	net.get('s9').start([c0])
	net.get('s10').start([c0])
	net.get('s11').start([c0])
	net.get('s12').start([c0])
	net.get('s13').start([c0])
	net.get('s14').start([c0])
	net.get('s15').start([c0])
	net.get('s16').start([c0])
	net.get('s17').start([c0])
	net.get('s18').start([c0])
	net.get('s19').start([c0])
	net.get('s20').start([c0, c2])




	info( '*** Post configure switches and hosts\n')
	s1.cmd('ifconfig s1 10.0.0.21 up')
	s7.cmd('ifconfig s7 10.0.0.28 up')
	s20.cmd('ifconfig s20 10.0.0.40 up')

	s7.cmd('route add 10.0.0.3 dev s7')
	s1.cmd('route add 10.0.0.1 dev s1')
	s20.cmd('route add 10.0.0.2 dev s20')

	rootdir = '/sys/class/net'
	h1.cmd('ifconfig h1-eth0 mtu 10000')
	h2.cmd('ifconfig h2-eth0 mtu 10000')
	h3.cmd('ifconfig h3-eth0 mtu 10000')
	for switch in net.switches:
		for subdir, dirs, files in os.walk(rootdir):
			for dir in dirs:
				if(str(switch) == dir.split("-")[0]):
					switch.cmd('ifconfig ' + dir + ' mtu 65000')
	net.staticArp()

	#time.sleep(15)
	info("Starting Global Controller")
	
	#h1.cmd('cd ../controllers_used_in_labs/floodlight_global')
	#h1.cmd('java -jar target/floodlight.jar &')
	info("C1 started")
	#h2.cmd('cd ../controllers_used_in_labs/floodlight_global2')
	#h2.cmd('java -jar target/floodlight.jar &')
	info("C2 started")
	#h3.cmd('cd ../controllers_used_in_labs/floodlight_global3')
	#h3.cmd('java -jar target/floodlight.jar &')
	info("C3 started")


	CLI(net)
	#info("Stopping Network")
	net.stop()

if __name__ == '__main__':
	setLogLevel( 'info' )
	myNetwork()


