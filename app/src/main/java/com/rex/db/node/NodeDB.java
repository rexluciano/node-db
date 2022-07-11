package com.rex.db.node;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.LogPrinter;
import android.widget.Toast;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.rex.db.node.listener.QueryEventListener;
import com.rex.db.node.query.Query;
import com.rex.db.node.query.QueryError;
import com.rex.db.node.utils.FileUtil;
import java.io.File;
import com.rex.db.node.NodeApp;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;

public class NodeDB {

	private String nodeName;
	private String name;
	private int id;
	private File dbPath;
	private LogPrinter printer;
	private ArrayList<HashMap<String, Object>> list = new ArrayList<>();
	private ArrayList<HashMap<String, Object>> listAdd = new ArrayList<>();
	private HashMap<String, Object> map = new HashMap<>();
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
					new Handler(Looper.getMainLooper()).postDelayed(() -> {
						query();
					}, 500);
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
		list.add(map);
		q.setData(list);
		return this;
	}

	public NodeDB put(HashMap<String, Object> hashMap) {
		list.add(hashMap);
		q.setData(list);
		return this;
	}

	public NodeDB push() {
		FileUtil.writeFile(NodeApp.getPath().toString() + "/" + nodeName + ".node", new Gson().toJson(list).toString());
		if (listener != null) {
			listener.onQuery(q);
		}
		map.clear();
		list.clear();
		refresh();
		return this;
	}

	private void refresh() {
		list = new Gson().fromJson(FileUtil.readFile(dbPath.getAbsolutePath()),
				new TypeToken<ArrayList<HashMap<String, Object>>>() {
				}.getType());
	}

	public NodeDB query() {
		try {
			q.setData(list);
			if (listener != null) {
				listener.onQuery(q);
			}
		} catch (Exception e) {
			printer.println("NodeDB Query Error : " + e.getLocalizedMessage());
			qe.setError("No result found.");
			if (listener != null) {
				listener.onError(qe);
			}
		}
		return this;
	}

	public int size() {
		return list.size();
	}

	public NodeDB get(int id) {
		HashMap<String, Object> map = new HashMap<>();
		try {
			map = new Gson().fromJson(new Gson().toJson(list.get(id)), new TypeToken<HashMap<String, Object>>() {
			}.getType());
			q2.setData(map);
			if (mListener != null) {
				mListener.onQuery(q2);
			}
		} catch (Exception e) {
			qe.setError(list.get(id).toString());
			if (mListener != null) {
				mListener.onError(qe);
			}
		}
		return this;
	}

	public String getKey() {
		return String.valueOf((long) (list.size()));
	}

	public String get() {
		return new Gson().toJson(list).toString();
	}
	
	public NodeDB child(int id) {
		this.id = id;
		return this;
	}
	
	public void removeValue(String value) {
		list.get(id).remove(value);
		push().refresh();
	}

	public void addQueryEventListener(QueryEventListener mQuery) {
		this.listener = mQuery;
	}

	public void addValueEventListener(QueryEventListener mQuery) {
		this.mListener = mQuery;
	}

}