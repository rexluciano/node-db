package com.rex.db.node.example;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.rex.db.node.NodeApp;
import com.rex.db.node.NodeDB;
import com.rex.db.node.NodeObject;
import com.rex.db.node.query.QueryError;
import com.rex.db.node.query.Query;
import com.rex.db.node.listener.QueryEventListener;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import com.rex.db.node.example.R;

public class MainActivity extends Activity {

	private NodeDB dB;
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView = (TextView) findViewById(R.id.textView);
		final EditText key = (EditText) findViewById(R.id.key);
		final EditText value = (EditText) findViewById(R.id.value);
		final Button button1 = (Button) findViewById(R.id.button1);
		final Button button2 = (Button) findViewById(R.id.button2);
		final Button button3 = (Button) findViewById(R.id.button3);
		final Button button4 = (Button) findViewById(R.id.button4);
		NodeApp.initialize(this);

		dB = new NodeDB("users").setDesc(true);
		dB.addQueryEventListener(eventListener);
		dB.addValueEventListener(eventListener2);
		button1.setText("Update");
		button1.setOnClickListener((v) -> {
			NodeObject ob = new NodeObject();
			ob.updateObject("name", value.getText().toString());
			dB.child(key.getText().toString()).update(ob).push();
		});
		button2.setText("Add");
		button2.setOnClickListener((v) -> {
			NodeObject obj = new NodeObject();
			obj.putObject(key.getText().toString(), value.getText().toString());
			dB.put(obj).prepare().push();
		});
		button3.setText("Remove");
		button3.setOnClickListener((v) -> {
			dB.child(key.getText().toString()).remove();
		});

		button4.setText("Get");
		button4.setOnClickListener((v) -> {
			dB.child(key.getText().toString()).get();
		});
	}

	private QueryEventListener eventListener = new QueryEventListener() {
		@Override
		public void onQuery(Query q) {
			Toast.makeText(MainActivity.this, "New record added.", Toast.LENGTH_LONG).show();
			textView.setText(q.getQuery().toString());
		}

		@Override
		public void onError(QueryError e) {
			Toast.makeText(MainActivity.this, "Query : " + e.toString(), Toast.LENGTH_LONG).show();
		}

	};

	private QueryEventListener eventListener2 = new QueryEventListener() {
		@Override
		public void onQuery(Query query) {
			Map map = (Map) query.getData();
			textView.setText(map.get("name").toString());
			Toast.makeText(MainActivity.this, "Record retreived.", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onError(QueryError e) {
			Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
		}

	};
};