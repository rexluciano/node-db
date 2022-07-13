package com.rex.db.node;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.LogPrinter;
import com.rex.db.node.listener.QueryEventListener;
import com.rex.db.node.listener.ValueEventListener;
import com.rex.db.node.query.Query;
import com.rex.db.node.query.QueryError;
import com.rex.db.node.utils.FileUtil;
import com.rex.db.node.utils.Utils;
import java.io.File;
import com.rex.db.node.NodeApp;
import java.io.FileNotFoundException;
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
	private LogPrinter printer;
	private boolean isDescending;
	private String tableName;
	private static String dbName;
	private String pathName;

	private ArrayList<Object> list = new ArrayList<>();
	private List<String> listStr = new ArrayList<>();
	private HashMap<String, Object> map = new HashMap<>();
	private JSONObject object = new JSONObject();
	private JSONObject data = new JSONObject();
	private JSONArray array = new JSONArray();

	private QueryEventListener listener = null;
	private ValueEventListener mListener = null;
	private Query q = new Query();
	private Query q2 = new Query();
	private QueryError qe = new QueryError();

	private static NodeDB mInstance;

	protected NodeDB() {
		printer = new LogPrinter(Log.DEBUG, "logs");
	}

	public static NodeDB getInstance() {
		if (mInstance == null) {
			mInstance = new NodeDB();
		}
		return mInstance;
	}

	public NodeDB create() {
		if (Utils.isEmpty(dbName))
			throw new NullPointerException("You must set a database name first!");
		if (NodeApp.getPath() == null)
			throw new NullPointerException("Invalid directory or access denied.");
		return this;
	}

	public NodeDB build() {
		init();
		return this;
	}

	protected void init() {
		if (NodeApp.getContext() != null) {
			if (!Utils.isEmpty(dbName)) {
				refresh();
				new Handler(Looper.getMainLooper()).postDelayed(() -> read(), 200);
			} else {
				printer.println("NodeDB Error : No database has found.");
			}
		} else {
			throw new NullPointerException(
					"You must called NodeApp.initialize(Context) first before performing any NodeDB queries.");
		}
	}

	public NodeDB table(String tbleName) {
		this.tableName = tbleName;
		return this;
	}

	public NodeDB getDatabase(String name) {
		dbName = name;
		return this;
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
			if (id != null && !id.trim().equalsIgnoreCase("")) {
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
		if (isExistTable(tableName.trim())) {
			list.clear();
			listStr.clear();
			refresh();
			list.add(map2);
		} else {
			list.clear();
			listStr.clear();
			list.add(map2);
		}
		q.setData(list);
		return this;
	}

	public NodeDB insert() {
		JSONObject output = new JSONObject();
		JSONArray jSONArray = new JSONArray(list);
		try {
			output.put(tableName.replace("", "").trim(), jSONArray);
			FileUtil.writeFile(
					NodeApp.getPath().toString() + "/" + dbName + File.separator + tableName + "/" + "1.node",
					new JSONObject(output.toString()).toString(4));
			if (listener != null) {
				listener.onQuery(q);
			}
			list.clear();
			listStr.clear();
			refresh();
		} catch (JSONException e) {
			qe.setError(e.getLocalizedMessage());
			if (listener != null) {
				listener.onError(qe);
			}
		}
		map.clear();
		return this;
	}

	protected boolean isExistTable(String table) {
		try {
			JSONObject input = new JSONObject(FileUtil.readFile(
					NodeApp.getPath().toString() + "/" + dbName + File.separator + tableName + "/" + "1.node"));
			JSONArray jsonArray = input.getJSONArray(table.replace("", "").trim());
			if (input.has(table.replace("", "").trim()))
				return true;
		} catch (JSONException e) {
			return false;
		}
		return false;
	}

	private void refresh() {
		try {
			JSONObject input = new JSONObject(FileUtil.readFile(
					NodeApp.getPath().toString() + "/" + dbName + File.separator + tableName + "/" + "1.node"));
			JSONArray jsonArray = input.getJSONArray(tableName.replace("", "").trim());
			list.clear();
			listStr.clear();
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

	protected void read() {
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
			qe.setError("Requested value doesn/'t exist.");
			if (mListener != null) {
				mListener.onError(qe);
			}
		}
		return this;
	}

	public NodeDB isDesc(boolean value) {
		this.isDescending = value;
		return this;
	}

	public String getKey() {
		return Utils.generateKey(7);
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
			insert().refresh();
		} catch (JSONException e) {
			qe.setError("Requested value doesn\'t exist.");
			if (listener != null) {
				listener.onError(qe);
			}
		}
	}

	public void remove() {
		if (Utils.getIndexOf(listStr, id) != -1) {
			list.remove(Utils.getIndexOf(listStr, id));
			insert().refresh();
		} else {
			qe.setError("Requested value doesn\'t exist.");
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
		FileUtil.deleteFile(NodeApp.getPath().toString() + "/" + dbName + ".node");
		q.setData(list);
		if (listener != null) {
			listener.onQuery(q);
		}
	}

	public void addQueryEventListener(QueryEventListener mQuery) {
		this.listener = mQuery;
	}

	public void addValueEventListener(ValueEventListener mQuery) {
		this.mListener = mQuery;
	}

}