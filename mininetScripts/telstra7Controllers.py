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

	c0=net.addController(name='c0',
					controller=RemoteController,
					ip='0.0.0.0',
					protocol='tcp',
					port=6653)

	c3=net.addController(name='c3',
					controller=InbandController,
					ip='10.0.0.3',
					protocol='tcp',
					port=6653)

	c4=net.addController(name='c4',
					controller=InbandController,
					ip='10.0.0.4',
					protocol='tcp',
					port=6653)

	c2=net.addController(name='c2',
					controller=InbandController,
					ip='10.0.0.2',
					protocol='tcp',
					port=6653)

	c5=net.addController(name='c5',
					controller=InbandController,
					ip='10.0.0.5',
					protocol='tcp',
					port=6653)

	c6=net.addController(name='c6',
					controller=InbandController,
					ip='10.0.0.6',
					protocol='tcp',
					port=6653)

	c7=net.addController(name='c7',
					controller=InbandController,
					ip='10.0.0.7',
					protocol='tcp',
					port=6653)

	info( '*** Add switches\n')
	s1 = net.addSwitch('s1', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s2 = net.addSwitch('s2', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s3 = net.addSwitch('s3', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s4 = net.addSwitch('s4', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
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
	s21 = net.addSwitch('s21', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s22 = net.addSwitch('s22', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s23 = net.addSwitch('s23', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s24 = net.addSwitch('s24', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s25 = net.addSwitch('s25', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s26 = net.addSwitch('s26', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s27 = net.addSwitch('s27', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s28 = net.addSwitch('s28', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s29 = net.addSwitch('s29', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s30 = net.addSwitch('s30', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s31 = net.addSwitch('s31', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s32 = net.addSwitch('s32', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s33 = net.addSwitch('s33', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s34 = net.addSwitch('s34', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s35 = net.addSwitch('s35', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s36 = net.addSwitch('s36', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s37 = net.addSwitch('s37', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s38 = net.addSwitch('s38', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s39 = net.addSwitch('s39', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s40 = net.addSwitch('s40', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s41 = net.addSwitch('s41', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s42 = net.addSwitch('s42', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s43 = net.addSwitch('s43', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s44 = net.addSwitch('s44', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s45 = net.addSwitch('s45', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s46 = net.addSwitch('s46', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s47 = net.addSwitch('s47', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s48 = net.addSwitch('s48', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s49 = net.addSwitch('s49', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s50 = net.addSwitch('s50', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s51 = net.addSwitch('s51', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s52 = net.addSwitch('s52', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s53 = net.addSwitch('s53', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s54 = net.addSwitch('s54', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s55 = net.addSwitch('s55', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s56 = net.addSwitch('s56', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s57 = net.addSwitch('s57', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	
	


	info( '*** Add hosts\n')
	h1 = net.addHost('h1', cls=Host, ip='10.0.0.1', defaultRoute=None)
	h2 = net.addHost('h2', cls=Host, ip='10.0.0.2', defaultRoute=None)
	h3 = net.addHost('h3', cls=Host, ip='10.0.0.3', defaultRoute=None)
	h4 = net.addHost('h4', cls=Host, ip='10.0.0.4', defaultRoute=None)
	h5 = net.addHost('h5', cls=Host, ip='10.0.0.5', defaultRoute=None)
	h6 = net.addHost('h6', cls=Host, ip='10.0.0.6', defaultRoute=None)
	h7 = net.addHost('h7', cls=Host, ip='10.0.0.7', defaultRoute=None)


	# Add in link options for different experiments
	#linkopts = dict(delay='10ms', bw=1000)

	info( '*** Add links\n')
	net.addLink(s1, h1) #s1 Perth: 5 single switches attached
	net.addLink(s50, h2)
	net.addLink(s22, h3)
	net.addLink(s7, h4)
	net.addLink(s14, h5)
	net.addLink(s30, h6)
	net.addLink(s39, h7)
	net.addLink(s1, s2)
	net.addLink(s1, s3)
	net.addLink(s1, s4)
	net.addLink(s1, s5)
	net.addLink(s1, s6)
	net.addLink(s1, s7) #s7 Darwin: 0
	net.addLink(s1, s8) #s8 Adelaide: 11
	net.addLink(s8, s7)
	net.addLink(s8, s9)
	net.addLink(s8, s10)
	net.addLink(s8, s11)
	net.addLink(s8, s12)
	net.addLink(s8, s13)
	net.addLink(s8, s14)
	net.addLink(s8, s15)#
	net.addLink(s8, s16)
	net.addLink(s8, s17)
	net.addLink(s8, s18)
	net.addLink(s8, s19)
	net.addLink(s8, s20) #s20 Melbourne: 8
	net.addLink(s20, s21)
	net.addLink(s20, s22)
	net.addLink(s20, s23)
	net.addLink(s20, s24)
	net.addLink(s20, s25)
	net.addLink(s20, s26)
	net.addLink(s20, s27)
	net.addLink(s20, s28)
	net.addLink(s20, s29) #s29 Hobart: 2
	net.addLink(s20, s30) #s30 Canberra: 0
	net.addLink(s29, s31)
	net.addLink(s29, s32)
	net.addLink(s30, s33) #s33 Sydney: 14
	net.addLink(s33, s34)
	net.addLink(s33, s35)
	net.addLink(s33, s36)
	net.addLink(s33, s37)
	net.addLink(s33, s38)
	net.addLink(s33, s39)
	net.addLink(s33, s40)
	net.addLink(s33, s41)
	net.addLink(s33, s42)
	net.addLink(s33, s43)
	net.addLink(s33, s44)
	net.addLink(s33, s45)
	net.addLink(s33, s46)
	net.addLink(s33, s47)
	net.addLink(s33, s48) #s48 Brisbane: 9
	net.addLink(s48, s49)
	net.addLink(s48, s50)
	net.addLink(s48, s51)
	net.addLink(s48, s52)
	net.addLink(s48, s53)
	net.addLink(s48, s54)
	net.addLink(s48, s55)
	net.addLink(s48, s56)
	net.addLink(s48, s57)


	info( '*** Starting network\n')
	net.build()
	info( '*** Starting controllers\n')
	c0.start()
	c1.start()
	c2.start()
	c3.start()
	c4.start()
	c5.start()
	c6.start()
	c7.start()

	info( '*** Starting switches\n')
	net.get('s1').start([c0, c1])
	net.get('s2').start([c0])
	net.get('s3').start([c0])
	net.get('s4').start([c0])
	net.get('s5').start([c0])
	net.get('s6').start([c0])
	net.get('s7').start([c0, c4])
	net.get('s8').start([c0])
	net.get('s9').start([c0])
	net.get('s10').start([c0])
	net.get('s11').start([c0])
	net.get('s12').start([c0])
	net.get('s13').start([c0])
	net.get('s14').start([c0, c5])
	net.get('s15').start([c0])
	net.get('s16').start([c0])
	net.get('s17').start([c0])
	net.get('s18').start([c0])
	net.get('s19').start([c0])
	net.get('s20').start([c0])
	net.get('s21').start([c0])
	net.get('s22').start([c0, c3])
	net.get('s23').start([c0])
	net.get('s24').start([c0])
	net.get('s25').start([c0])
	net.get('s26').start([c0])
	net.get('s27').start([c0])
	net.get('s28').start([c0])
	net.get('s29').start([c0])
	net.get('s30').start([c0, c6])
	net.get('s31').start([c0])
	net.get('s32').start([c0])
	net.get('s33').start([c0])
	net.get('s34').start([c0])
	net.get('s35').start([c0])
	net.get('s36').start([c0])
	net.get('s37').start([c0])
	net.get('s38').start([c0])
	net.get('s39').start([c0, c7])
	net.get('s40').start([c0])
	net.get('s41').start([c0])
	net.get('s42').start([c0])
	net.get('s43').start([c0])
	net.get('s44').start([c0])
	net.get('s45').start([c0])
	net.get('s46').start([c0])
	net.get('s47').start([c0])
	net.get('s48').start([c0])
	net.get('s49').start([c0])
	net.get('s50').start([c0, c2])
	net.get('s51').start([c0])
	net.get('s52').start([c0])
	net.get('s53').start([c0])
	net.get('s54').start([c0])
	net.get('s55').start([c0])
	net.get('s56').start([c0])
	net.get('s57').start([c0])


	info( '*** Post configure switches and hosts\n')
	s1.cmd('ifconfig s1 10.0.0.21 up')
	s50.cmd('ifconfig s50 10.0.0.60 up')
	s22.cmd('ifconfig s22 10.0.0.42 up')
	s7.cmd('ifconfig s7 10.0.0.27 up')
	s14.cmd('ifconfig s14 10.0.0.34 up')
	s30.cmd('ifconfig s30 10.0.0.40 up')
	s39.cmd('ifconfig s39 10.0.0.49 up')

	s39.cmd('route add 10.0.0.7 dev s39')
	s30.cmd('route add 10.0.0.6 dev s30')
	s14.cmd('route add 10.0.0.5 dev s14')
	s7.cmd('route add 10.0.0.4 dev s7')
	s22.cmd('route add 10.0.0.3 dev s22')
	s50.cmd('route add 10.0.0.2 dev s50')
	s1.cmd('route add 10.0.0.1 dev s1')
	
	rootdir = '/sys/class/net'
	h1.cmd('ifconfig h1-eth0 mtu 10000')
	h2.cmd('ifconfig h2-eth0 mtu 10000')
	h3.cmd('ifconfig h3-eth0 mtu 10000')
	h4.cmd('ifconfig h4-eth0 mtu 10000')
	h5.cmd('ifconfig h5-eth0 mtu 10000')
	h6.cmd('ifconfig h6-eth0 mtu 10000')
	h7.cmd('ifconfig h7-eth0 mtu 10000')
	for switch in net.switches:
		for subdir, dirs, files in os.walk(rootdir):
			for dir in dirs:
				if(str(switch) == dir.split("-")[0]):
					switch.cmd('ifconfig ' + dir + ' mtu 10000')
	
	#time.sleep(30)
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
	#h4.cmd('cd ../controllers_used_in_labs/floodlight_global4')
	#h4.cmd('java -jar target/floodlight.jar &')
	info("C4 started")
	#h5.cmd('cd ../controllers_used_in_labs/floodlight_global5')
	#h5.cmd('java -jar target/floodlight.jar &')
	info("C5 started")
	#h6.cmd('cd ../controllers_used_in_labs/floodlight_global6')
	#h6.cmd('java -jar target/floodlight.jar &')
	info("C6 started")
	#h7.cmd('cd ../controllers_used_in_labs/floodlight_global7')
	#h7.cmd('java -jar target/floodlight.jar &')
	info("C7 started")
	

	net.staticArp()
	CLI(net)
	#s50.cmd('ifconfig s50 10.0.0.60 down')
	#s1.cmd('ifconfig s1 10.0.0.21 down')
	net.stop()

if __name__ == '__main__':
	setLogLevel( 'info' )
	myNetwork()



