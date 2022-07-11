package com.rex.db.node;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.LogPrinter;
import android.widget.Toast;
import com.rex.db.node.listener.QueryEventListener;
import com.rex.db.node.query.Query;
import com.rex.db.node.query.QueryError;
import com.rex.db.node.query.SortingList;
import com.rex.db.node.utils.FileUtil;
import com.rex.db.node.utils.Utils;
import java.io.File;
import com.rex.db.node.NodeApp;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NodeDB {

	private String nodeName;
	private String name;
	private String id = "";
	private File dbPath;
	private LogPrinter printer;

	private ArrayList<Object> list = new ArrayList<>();
	private HashMap<String, Object> map = new HashMap<>();
	private JSONObject object = new JSONObject();
	private JSONObject data = new JSONObject();

	private QueryEventListener listener = null;
	private QueryEventListener mListener = null;
	private Query q = new Query();
	private Query q2 = new Query();
	private QueryError qe = new QueryError();

	public NodeDB(String nodeName) {
		this.nodeName = nodeName;
		printer = new LogPrinter(Log.DEBUG, "logs");
		if (NodeApp.getContext() != null) {
			dbPath = new File(NodeApp.getPath().toString() + "/" + nodeName + ".node");
			if (dbPath.exists()) {
				try {
					refresh();
					new Handler(Looper.getMainLooper()).postDelayed(() -> query(), 200);
				} catch (Exception exception) {
					printer.println("NodeDB : " + exception.getLocalizedMessage());
				}
			} else {
				printer.println("NodeDB : database path not yet created");
			}
		} else {
			throw new NullPointerException(
					"You must called NodeApp.initialize(Context) first before calling any NodeDB query.");
		}
	}

	public NodeDB put(String key, Object value) {
		try {
			if (!id.trim().equalsIgnoreCase("")) {
				data.put(key, value);
				object.put(id, data);
			} else {
				object.put(getKey(), map);
			}
		} catch (JSONException e) {
		}
		list.add(object);
		q.setData(list);
		return this;
	}

	public NodeDB put(Object hashMap) {
		try {
			if (!id.trim().equalsIgnoreCase("")) {
				object.put(id, new JSONObject((Map) hashMap));
			} else {
				object.put(getKey(), new JSONObject((Map) hashMap));
			}
		} catch (JSONException e) {
			qe.setError(e.getLocalizedMessage());
			if (listener != null) {
				listener.onError(qe);
			}
		}
		list.add(object);
		q.setData(list);
		return this;
	}

	public NodeDB push() {
		JSONObject output = new JSONObject();
		JSONArray jSONArray = new JSONArray(list);
		try {
			output.put("nodes", jSONArray);
			FileUtil.writeFile(NodeApp.getPath().toString() + "/" + nodeName + ".node", output.toString());
			list.clear();
			refresh();
			if (listener != null) {
				listener.onQuery(q);
			}
		} catch (JSONException e) {
			qe.setError(e.getLocalizedMessage());
			if (listener != null) {
				listener.onError(qe);
			}
		}
		if (list.contains(id)) {
			list.remove(id);
		}
		map.clear();
		return this;
	}

	private void refresh() {
		try {
			JSONObject input = new JSONObject(FileUtil.readFile(dbPath.getAbsolutePath()));
			JSONArray jsonArray = input.getJSONArray("nodes");
			list.clear();
			for (int i = 0; i < jsonArray.length(); i++) {
				list.add(jsonArray.get(i));
			}
		} catch (JSONException e) {
			qe.setError(e.getLocalizedMessage());
			if (listener != null) {
				listener.onError(qe);
			}
		}
	}

	public NodeDB query() {
		try {
			q.setData(list);
			if (listener != null) {
				listener.onQuery(q);
			}
		} catch (Exception e) {
			printer.println("NodeDB Query Error : " + e.getLocalizedMessage());
			qe.setError(e.getLocalizedMessage());
			if (listener != null) {
				listener.onError(qe);
			}
		}
		return this;
	}

	public int size() {
		return list.size();
	}

	public NodeDB get() {
		HashMap<String, Object> map = new HashMap<>();
		try {
			JSONObject jSONObject = new JSONObject(list.get(list.indexOf(id)).toString());
			q2.setData(jSONObject);
			if (mListener != null) {
				mListener.onQuery(q2);
			}
		} catch (Exception e) {
			qe.setError("Specified id is not found.");
			if (mListener != null) {
				mListener.onError(qe);
			}
		}
		return this;
	}

	public NodeDB orderByKey() {
		Collections.reverse(list);
		return this;
	}

	public String getKey() {
		this.id = Utils.generateKey(7);
		return id;
	}

	public String toString() {
		return list.toString();
	}

	public NodeDB child(String id) {
		this.id = id;
		return this;
	}

	public void remove(String value) {
		list.remove(value);
		push().refresh();
	}

	public void remove() {
		list.remove(id);
		push().refresh();
	}

	public void update(String key, String value) {
		map.put(key, value);
		try {
			object.put(id, map);
		} catch (JSONException e) {
			qe.setError(e.getLocalizedMessage());
			if (listener != null) {
				listener.onError(qe);
			}
		}
		push().refresh();
	}

	public void update(HashMap<String, Object> map) {
		try {
			object.put(id, map);
		} catch (JSONException e) {
			qe.setError(e.getLocalizedMessage());
			if (listener != null) {
				listener.onError(qe);
			}
		}
		push().refresh();
	}

	public void delete() {
		FileUtil.deleteFile(NodeApp.getPath().toString() + "/" + nodeName + ".node");
	}

	public void addQueryEventListener(QueryEventListener mQuery) {
		this.listener = mQuery;
	}

	public void addValueEventListener(QueryEventListener mQuery) {
		this.mListener = mQuery;
	}

}