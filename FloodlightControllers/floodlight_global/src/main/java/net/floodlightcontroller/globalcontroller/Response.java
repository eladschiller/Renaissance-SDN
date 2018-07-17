package net.floodlightcontroller.globalcontroller;

public class Response {
	
	private String dpid;
	private String neighbours;
	private String managers;
	private int tag;
	
	public Response(String dpid, String neighbours, String managers, int tag){
		this.dpid = dpid;
		this.neighbours = neighbours;
		this.managers = managers;
		this.tag = tag;
	}
	
	public Response(String dpid){
		this.dpid = dpid;
		this.neighbours = null;
		this.managers = null;
		this.tag = -1;
	}
	
	public String getDpid() {
		return dpid;
	}
	
	public void setDpid(String dpid) {
		this.dpid = dpid;
	}
	
	public String getNeighbours() {
		return neighbours;
	}
	
	public void setNeighbours(String neighbours) {
		this.neighbours = neighbours;
	}
	
	public String getManagers() {
		return managers;
	}
	
	public void setManagers(String managers) {
		this.managers = managers;
	}
	
	public int getTag() {
		return tag;
	}
	
	public void setTag(int tag) {
		this.tag = tag;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Response){
			return this.dpid.equals(((Response) obj).getDpid());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return dpid.hashCode();
	}

}
