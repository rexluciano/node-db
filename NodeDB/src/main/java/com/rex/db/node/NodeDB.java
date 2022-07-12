package com.rex.db.node;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.LogPrinter;
import com.rex.db.node.listener.QueryEventListener;
import com.rex.db.node.query.Query;
import com.rex.db.node.query.QueryError;
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
	private boolean isDescending;

	private ArrayList<Object> list = new ArrayList<>();
	private List<String> listStr = new ArrayList<>();
	private HashMap<String, Object> map = new HashMap<>();
	private JSONObject object = new JSONObject();
	private JSONObject data = new JSONObject();
	private JSONArray array = new JSONArray();

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
		map.put(key, value);
		return this;
	}

	public NodeDB put(HashMap<String, Object> hashMap) {
		if (hashMap.containsKey("id")) {
			hashMap.remove("id");
		}
		map.putAll(hashMap);
		return this;
	}

	public NodeDB put(NodeObject object) {
		map.put(object.key, object.value);
		return this;
	}

	public NodeDB prepare() {
		HashMap<String, Object> map2 = new HashMap<>();
		try {
			if (!id.trim().equalsIgnoreCase("")) {
				map2.put("id", id);
			} else {
				map2.put("id", getKey());
			}
		} catch (Exception e) {
			qe.setError(e.getLocalizedMessage());
			if (listener != null) {
				listener.onError(qe);
			}
		}
		map2.putAll(map);
		list.add(map2);
		q.setData(list);
		return this;
	}

	public NodeDB push() {
		JSONObject output = new JSONObject();
		JSONArray jSONArray = new JSONArray(list);
		try {
			output.put(nodeName.replace("", "").trim(), jSONArray);
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
		map.clear();
		return this;
	}

	private void refresh() {
		try {
			JSONObject input = new JSONObject(FileUtil.readFile(dbPath.getAbsolutePath()));
			JSONArray jsonArray = input.getJSONArray(nodeName.replace("", "").trim());
			list.clear();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject ob1 = jsonArray.getJSONObject(i);
				list.add(jsonArray.get(i));
				listStr.add(ob1.getString("id"));
			}
		} catch (JSONException e) {
			qe.setError(e.getLocalizedMessage());
			if (listener != null) {
				listener.onError(qe);
			}
		}
	}

	protected NodeDB query() {
		try {
			if (isDescending) {
				Collections.reverse(list);
				Collections.reverse(listStr);
			}
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
		try {
			JSONArray arr = new JSONArray(list);
			JSONObject j = arr.getJSONObject(Utils.getIndexOf(listStr, id));
			Map<String, Object> hash = Utils.toMap(j);
			q2.setData(hash);
			if (mListener != null) {
				mListener.onQuery(q2);
			}
		} catch (Exception e) {
			qe.setError("Specified key is not found.");
			if (mListener != null) {
				mListener.onError(qe);
			}
		}
		return this;
	}

	public NodeDB setDesc(boolean value) {
		this.isDescending = value;
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

	public void remove(String key) {
		JSONArray array2 = new JSONArray(list);
		try {
			JSONObject ub = array2.getJSONObject(Utils.getIndexOf(listStr, id));
			ub.remove(key);
			list.add(array2.toString());
			push().refresh();
		} catch (JSONException e) {
			qe.setError("Requested value doesn\'t exist at the table.");
			if (listener != null) {
				listener.onError(qe);
			}
		}
	}

	public void remove() {
		if (Utils.getIndexOf(listStr, id) != -1) {
			list.remove(Utils.getIndexOf(listStr, id));
			push().refresh();
		} else {
			qe.setError("Requested value doesn\'t exist at the table.");
			if (listener != null) {
				listener.onError(qe);
			}
		}
	}

	public NodeDB update(NodeObject ob) {
		array = new JSONArray(list);
		try {
			JSONObject u = array.getJSONObject(Utils.getIndexOf(listStr, id));
			u.put(ob.key, ob.value);
			list.add(array.toString());
		} catch (JSONException e) {
		}
		return this;
	}

	public void delete() {
		FileUtil.deleteFile(NodeApp.getPath().toString() + "/" + nodeName + ".node");
		q.setData(list);
		if (listener != null) {
			listener.onQuery(q);
		}
	}

	public void addQueryEventListener(QueryEventListener mQuery) {
		this.listener = mQuery;
	}

	public void addValueEventListener(QueryEventListener mQuery) {
		this.mListener = mQuery;
	}

}