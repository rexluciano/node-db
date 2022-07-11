package com.rex.db.node.query;

import java.util.ArrayList;
import java.util.HashMap;

public class Query {

	private ArrayList<HashMap<String, Object>> data;
	private HashMap<String, Object> map;

	public Query() {
	}

	public void setData(ArrayList<HashMap<String, Object>> data) {
		this.data = data;
	}

	public void setData(HashMap<String, Object> map) {
		this.map = map;
	}

	public ArrayList<HashMap<String, Object>> getQuery() {
		return data;
	}

	public HashMap<String, Object> getData() {
		return map;
	}
}