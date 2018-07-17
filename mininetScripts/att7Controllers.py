#!/usr/bin/python

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

	c2=net.addController(name='c2',
					controller=InbandController,
					ip='10.0.0.2',
					protocol='tcp',
					port=6653)

	c4=net.addController(name='c4',
					controller=InbandController,
					ip='10.0.0.4',
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

	
	info( '*** Adding controller\n' )
	info( '*** Add switches\n')
	s14 = net.addSwitch('s14', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s50 = net.addSwitch('s50', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s72 = net.addSwitch('s72', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s91 = net.addSwitch('s91', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s80 = net.addSwitch('s80', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s65 = net.addSwitch('s65', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s92 = net.addSwitch('s92', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s81 = net.addSwitch('s81', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s90 = net.addSwitch('s90', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s47 = net.addSwitch('s47', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s82 = net.addSwitch('s82', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s16 = net.addSwitch('s16', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s83 = net.addSwitch('s83', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s66 = net.addSwitch('s66', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s33 = net.addSwitch('s33', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s95 = net.addSwitch('s95', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s54 = net.addSwitch('s54', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s58 = net.addSwitch('s58', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s32 = net.addSwitch('s32', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s51 = net.addSwitch('s51', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s17 = net.addSwitch('s17', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s26 = net.addSwitch('s26', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s84 = net.addSwitch('s84', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s52 = net.addSwitch('s52', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s18 = net.addSwitch('s18', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s27 = net.addSwitch('s27', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s88 = net.addSwitch('s88', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s53 = net.addSwitch('s53', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s34 = net.addSwitch('s34', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s87 = net.addSwitch('s87', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s74 = net.addSwitch('s74', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s68 = net.addSwitch('s68', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s57 = net.addSwitch('s57', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s69 = net.addSwitch('s69', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s35 = net.addSwitch('s35', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s93 = net.addSwitch('s93', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s70 = net.addSwitch('s70', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s62 = net.addSwitch('s62', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s89 = net.addSwitch('s89', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s41 = net.addSwitch('s41', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s71 = net.addSwitch('s71', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s75 = net.addSwitch('s75', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s37 = net.addSwitch('s37', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s2 = net.addSwitch('s2', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s76 = net.addSwitch('s76', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s3 = net.addSwitch('s3', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s85 = net.addSwitch('s85', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s38 = net.addSwitch('s38', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s11 = net.addSwitch('s11', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s77 = net.addSwitch('s77', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s39 = net.addSwitch('s39', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s45 = net.addSwitch('s45', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s8 = net.addSwitch('s8', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s73 = net.addSwitch('s73', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s42 = net.addSwitch('s42', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s21 = net.addSwitch('s21', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s4 = net.addSwitch('s4', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s13 = net.addSwitch('s13', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s43 = net.addSwitch('s43', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s20 = net.addSwitch('s20', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s36 = net.addSwitch('s36', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s9 = net.addSwitch('s9', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s44 = net.addSwitch('s44', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s59 = net.addSwitch('s59', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s10 = net.addSwitch('s10', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s55 = net.addSwitch('s55', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s5 = net.addSwitch('s5', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s49 = net.addSwitch('s49', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s28 = net.addSwitch('s28', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s48 = net.addSwitch('s48', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s67 = net.addSwitch('s67', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s6 = net.addSwitch('s6', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s22 = net.addSwitch('s22', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s1 = net.addSwitch('s1', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s60 = net.addSwitch('s60', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s86 = net.addSwitch('s86', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s7 = net.addSwitch('s7', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s40 = net.addSwitch('s40', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s19 = net.addSwitch('s19', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s23 = net.addSwitch('s23', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s46 = net.addSwitch('s46', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s61 = net.addSwitch('s61', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s78 = net.addSwitch('s78', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s96 = net.addSwitch('s96', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s24 = net.addSwitch('s24', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s12 = net.addSwitch('s12', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s29 = net.addSwitch('s29', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s79 = net.addSwitch('s79', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s15 = net.addSwitch('s15', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s30 = net.addSwitch('s30', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s63 = net.addSwitch('s63', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s94 = net.addSwitch('s94', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s56 = net.addSwitch('s56', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s25 = net.addSwitch('s25', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s31 = net.addSwitch('s31', cls=OVSSwitch, inband=True, protocols='OpenFlow13')
	s64 = net.addSwitch('s64', cls=OVSSwitch, inband=True, protocols='OpenFlow13')

	info( '*** Add hosts\n')
	h1 = net.addHost('h1', cls=Host, ip='10.0.0.1', defaultRoute=None)
	h2 = net.addHost('h2', cls=Host, ip='10.0.0.2', defaultRoute=None)
	h3 = net.addHost('h3', cls=Host, ip='10.0.0.3', defaultRoute=None)
	h4 = net.addHost('h4', cls=Host, ip='10.0.0.4', defaultRoute=None)
	h5 = net.addHost('h5', cls=Host, ip='10.0.0.5', defaultRoute=None)
	h6 = net.addHost('h6', cls=Host, ip='10.0.0.6', defaultRoute=None)
	h7 = net.addHost('h7', cls=Host, ip='10.0.0.7', defaultRoute=None)


	# Add in link options for different experiments
	linkopts = dict(delay='10ms', bw=1000)

	info( '*** Add links\n')
	net.addLink(s1, h1) #s1 Perth: 5 single switches attached
	net.addLink(s74, h2)
	net.addLink(s54, h3)
	net.addLink(s7, h4)
	net.addLink(s14, h5)
	net.addLink(s30, h6)
	net.addLink(s39, h7)
	net.addLink(s1, s3)
	net.addLink(s1, s2)
	net.addLink(s1, s4)
	net.addLink(s4, s5)
	net.addLink(s5, s6)
	net.addLink(s6, s7)
	net.addLink(s7, s4)
	net.addLink(s7, s8)
	net.addLink(s6, s9)
	net.addLink(s9, s8)
	net.addLink(s8, s11)
	net.addLink(s9, s10)
	net.addLink(s8, s12)
	net.addLink(s11, s13)
	net.addLink(s12, s14)
	net.addLink(s14, s15)
	net.addLink(s6, s16)
	net.addLink(s16, s17)
	net.addLink(s17, s18)
	net.addLink(s14, s19)
	net.addLink(s14, s21)
	net.addLink(s10, s20)
	net.addLink(s20, s25)
	net.addLink(s20, s26)
	net.addLink(s26, s25)
	net.addLink(s25, s24)
	net.addLink(s26, s27)
	net.addLink(s20, s23)
	net.addLink(s20, s22)
	net.addLink(s20, s28)
	net.addLink(s28, s29)
	net.addLink(s29, s30)
	net.addLink(s30, s31)
	net.addLink(s31, s32)
	net.addLink(s30, s32)
	net.addLink(s32, s33)
	net.addLink(s32, s34)
	net.addLink(s32, s35)
	net.addLink(s32, s36)
	net.addLink(s32, s37)
	net.addLink(s32, s38)
	net.addLink(s32, s40)
	net.addLink(s32, s39)
	net.addLink(s32, s41)
	net.addLink(s28, s41)
	net.addLink(s41, s42)
	net.addLink(s41, s43)
	net.addLink(s41, s44)
	net.addLink(s41, s45)
	net.addLink(s41, s46)
	net.addLink(s27, s47)
	net.addLink(s47, s48)
	net.addLink(s48, s52)
	net.addLink(s52, s49)
	net.addLink(s52, s51)
	net.addLink(s47, s50)
	net.addLink(s47, s53)
	net.addLink(s47, s54)
	net.addLink(s54, s56)
	net.addLink(s54, s55)
	net.addLink(s54, s57)
	net.addLink(s20, s54)
	net.addLink(s55, s60)
	net.addLink(s60, s58)
	net.addLink(s55, s59)
	net.addLink(s58, s63)
	net.addLink(s58, s65)
	net.addLink(s55, s58)
	net.addLink(s58, s62)
	net.addLink(s58, s61)
	net.addLink(s32, s66)
	net.addLink(s32, s67)
	net.addLink(s66, s67)
	net.addLink(s67, s68)
	net.addLink(s67, s69)
	net.addLink(s67, s70)
	net.addLink(s67, s71)
	net.addLink(s67, s72)
	net.addLink(s74, s75)
	net.addLink(s74, s73)
	net.addLink(s74, s76)
	net.addLink(s74, s77)
	net.addLink(s74, s82)
	net.addLink(s82, s81)
	net.addLink(s82, s80)
	net.addLink(s82, s78)
	net.addLink(s82, s79)
	net.addLink(s82, s83)
	net.addLink(s83, s92)
	net.addLink(s83, s93)
	net.addLink(s83, s94)
	net.addLink(s94, s81)
	net.addLink(s83, s89)
	net.addLink(s83, s88)
	net.addLink(s83, s87)
	net.addLink(s83, s86)
	net.addLink(s83, s84)
	net.addLink(s84, s90)
	net.addLink(s90, s85)
	net.addLink(s90, s91)
	net.addLink(s58, s83)
	net.addLink(s67, s58)
	net.addLink(s67, s83)
	net.addLink(s1, s32)
	net.addLink(s17, s20)
	net.addLink(s54, s96)
	net.addLink(s54, s95)
	net.addLink(s29, s62)

	info( '*** Starting network\n')
	net.build()
	info( '*** Starting controllers\n')
	for controller in net.controllers:
		controller.start()

	info( '*** Starting switches\n')
	net.get('s14').start([c0,c5])
	net.get('s50').start([c0])
	net.get('s72').start([c0])
	net.get('s91').start([c0])
	net.get('s80').start([c0])
	net.get('s65').start([c0])
	net.get('s92').start([c0])
	net.get('s81').start([c0])
	net.get('s90').start([c0])
	net.get('s47').start([c0])
	net.get('s82').start([c0])
	net.get('s16').start([c0])
	net.get('s83').start([c0])
	net.get('s66').start([c0])
	net.get('s33').start([c0])
	net.get('s95').start([c0])
	net.get('s54').start([c0,c3])
	net.get('s58').start([c0])
	net.get('s32').start([c0])
	net.get('s51').start([c0])
	net.get('s17').start([c0])
	net.get('s26').start([c0])
	net.get('s84').start([c0])
	net.get('s52').start([c0])
	net.get('s18').start([c0])
	net.get('s27').start([c0])
	net.get('s88').start([c0])
	net.get('s53').start([c0])
	net.get('s34').start([c0])
	net.get('s87').start([c0])
	net.get('s74').start([c0, c2])
	net.get('s68').start([c0])
	net.get('s57').start([c0])
	net.get('s69').start([c0])
	net.get('s35').start([c0])
	net.get('s93').start([c0])
	net.get('s70').start([c0])
	net.get('s62').start([c0])
	net.get('s89').start([c0])
	net.get('s41').start([c0])
	net.get('s71').start([c0])
	net.get('s75').start([c0])
	net.get('s37').start([c0])
	net.get('s2').start([c0])
	net.get('s76').start([c0])
	net.get('s3').start([c0])
	net.get('s85').start([c0])
	net.get('s38').start([c0])
	net.get('s11').start([c0])
	net.get('s77').start([c0])
	net.get('s39').start([c0,c7])
	net.get('s45').start([c0])
	net.get('s8').start([c0])
	net.get('s73').start([c0])
	net.get('s42').start([c0])
	net.get('s21').start([c0])
	net.get('s4').start([c0])
	net.get('s13').start([c0])
	net.get('s43').start([c0])
	net.get('s20').start([c0])
	net.get('s36').start([c0])
	net.get('s9').start([c0])
	net.get('s44').start([c0])
	net.get('s59').start([c0])
	net.get('s10').start([c0])
	net.get('s55').start([c0])
	net.get('s5').start([c0])
	net.get('s49').start([c0])
	net.get('s28').start([c0])
	net.get('s48').start([c0])
	net.get('s67').start([c0])
	net.get('s6').start([c0])
	net.get('s22').start([c0])
	net.get('s1').start([c0, c1])
	net.get('s60').start([c0])
	net.get('s86').start([c0])
	net.get('s7').start([c0,c4])
	net.get('s40').start([c0])
	net.get('s19').start([c0])
	net.get('s23').start([c0])
	net.get('s46').start([c0])
	net.get('s61').start([c0])
	net.get('s78').start([c0])
	net.get('s96').start([c0])
	net.get('s24').start([c0])
	net.get('s12').start([c0])
	net.get('s29').start([c0])
	net.get('s79').start([c0])
	net.get('s15').start([c0])
	net.get('s30').start([c0,c6])
	net.get('s63').start([c0])
	net.get('s94').start([c0])
	net.get('s56').start([c0])
	net.get('s25').start([c0])
	net.get('s31').start([c0])
	net.get('s64').start([c0])

	info( '*** Post configure switches and hosts\n')
	s1.cmd('ifconfig s1 10.0.0.21 up')
	s74.cmd('ifconfig s74 10.0.0.60 up')
	s54.cmd('ifconfig s54 10.0.0.42 up')
	s7.cmd('ifconfig s7 10.0.0.27 up')
	s14.cmd('ifconfig s14 10.0.0.34 up')
	s30.cmd('ifconfig s30 10.0.0.40 up')
	s39.cmd('ifconfig s39 10.0.0.49 up')

	s54.cmd('route add 10.0.0.3 dev s54')
	s74.cmd('route add 10.0.0.2 dev s74')
	s1.cmd('route add 10.0.0.1 dev s1')
	s39.cmd('route add 10.0.0.7 dev s39')
	s30.cmd('route add 10.0.0.6 dev s30')
	s14.cmd('route add 10.0.0.5 dev s14')
	s7.cmd('route add 10.0.0.4 dev s7')

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


	net.staticArp()

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

	CLI(net)
	#info("Stopping Network")
	net.stop()


if __name__ == '__main__':
    setLogLevel( 'info' )
    myNetwork()

