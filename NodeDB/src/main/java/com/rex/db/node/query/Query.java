package com.rex.db.node.query;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

public class Query {

	private ArrayList<Object> data;
	private Object map;
	private String type;

	public Query() {
	}

	public void setData(ArrayList<Object> data) {
		this.data = data;
	}

	public void setData(Object map) {
		this.map = map;
	}

	public ArrayList<Object> getQuery() {
		return data;
	}

	public Object getData() {
		return map;
	}

	public String toJson() {
		return new JSONArray(data).toString();
	}

}