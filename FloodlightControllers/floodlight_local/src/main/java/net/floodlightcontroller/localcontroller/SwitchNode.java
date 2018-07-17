package net.floodlightcontroller.localcontroller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SwitchNode {
	
	private String dpid;
	private Map<SwitchNode, SwitchLink> links;
	private Set<SwitchNode> children;
	
	public SwitchNode(String dpid){
		this.dpid = dpid;
		this.links = new HashMap<SwitchNode, SwitchLink>();
		this.children = new HashSet<SwitchNode>();
	}
	
	public void addLink(SwitchNode switchNode, SwitchLink switchLink){
		links.put(switchNode, switchLink);
	}
	
	public void removeLink(SwitchNode switchNode){
		links.remove(switchNode);
	}
	
	public boolean addChild(SwitchNode switchNode){
		return children.add(switchNode);
	}
	
	public boolean removeChild(SwitchNode switchNode){
		return children.remove(switchNode);
	}
	
	public String getDpid(){
		return dpid;
	}
	
	public Map<SwitchNode, SwitchLink> getLinks(){
		return links;
	}
	
	public Set<SwitchNode> getChildren(){
		return children;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SwitchNode){
			return this.dpid.equals(((SwitchNode) obj).getDpid());
		}
		else if(obj instanceof String){
			return this.dpid.equals((String) obj);
		}
		return false;
	}

}
