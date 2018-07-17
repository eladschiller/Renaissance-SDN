package net.floodlightcontroller.globalcontroller;

public class SwitchLink {
	
	private String dpid1;
	private String dpid2;
	private String port1;
	private String port2;
	
	public SwitchLink(String dpid1, String port1, String dpid2, String port2){
		this.setDpid1(dpid1);
		this.setPort1(port1);
		this.setDpid2(dpid2);
		this.setPort2(port2);
	}

	public String getDpid1() {
		return dpid1;
	}

	public void setDpid1(String dpid1) {
		this.dpid1 = dpid1;
	}

	public String getDpid2() {
		return dpid2;
	}

	public void setDpid2(String dpid2) {
		this.dpid2 = dpid2;
	}

	public String getPort1() {
		return port1;
	}

	public void setPort1(String port1) {
		this.port1 = port1;
	}

	public String getPort2() {
		return port2;
	}

	public void setPort2(String port2) {
		this.port2 = port2;
	}
	
	@Override
	public String toString() {
		return dpid1 + "-" + port1 + "<->" + dpid2 + "-" + port2;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SwitchLink){
			String pair1 = ((SwitchLink) obj).getDpid1() + "-" + ((SwitchLink) obj).getPort1();
			String pair2 = ((SwitchLink) obj).getDpid2() + "-" + ((SwitchLink) obj).getPort2();
			return this.toString().equals(pair1 + "<->" + pair2) || this.toString().equals(pair2 + "<->" + pair1);
		}
		
		return false;
	}
	
	public boolean contains(String dpid){
		return dpid.equals(dpid1) || dpid.equals(dpid2);
	}

}
