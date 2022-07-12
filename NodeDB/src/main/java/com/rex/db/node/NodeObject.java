package com.rex.db.node;

public class NodeObject {

	public String key;
	public Object value;

	public NodeObject() {
		super();
	}

	public void putObject(String key, Object value) {
		this.key = key;
		this.value = value;
	}
	
	public void updateObject(String key, Object value) {
		this.key = key;
		this.value = value;
	}
}