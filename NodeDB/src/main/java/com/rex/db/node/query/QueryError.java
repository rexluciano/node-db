package com.rex.db.node.query;

public class QueryError {
	private String s;

	public QueryError() {
	}

	public void setError(String s) {
		this.s = s;
	}
	
	public String toString() {
		return s;
	}
}