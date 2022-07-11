package com.rex.db.node.example;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.rex.db.node.NodeApp;
import com.rex.db.node.NodeDB;
import com.rex.db.node.query.QueryError;
import com.rex.db.node.query.Query;
import com.rex.db.node.listener.QueryEventListener;
import java.util.HashMap;
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
		NodeApp.initialize(this);

		dB = new NodeDB("users");
		dB.addQueryEventListener(eventListener);
		button1.setOnClickListener((v) -> {
			HashMap<String, Object> map = new HashMap<>();
			map.put("id", dB.getKey());
			map.put("name", value.getText().toString());
			map.put("age", key.getText().toString());
			dB.put(map).push();
		});

		button2.setOnClickListener((v) -> {
			dB.get(Integer.parseInt(key.getText().toString())).addValueEventListener(eventListener2);
		});

		button3.setOnClickListener((v) -> {
			dB.child(Integer.parseInt(key.getText().toString())).removeValue(value.getText().toString());
		});
	}

	private QueryEventListener eventListener = new QueryEventListener() {
		@Override
		public void onQuery(Query q) {
			if (q.getQuery() != null) {
				textView.setText(q.getQuery().toString());
			} else {
				textView.setText(q.getData().toString());
			}
			Toast.makeText(MainActivity.this, "New record added.", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onError(QueryError e) {
			Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
		}

	};

	private QueryEventListener eventListener2 = new QueryEventListener() {
		@Override
		public void onQuery(Query query) {
			HashMap<String, Object> map = query.getData();
			textView.setText(map.get("name").toString());
			Toast.makeText(MainActivity.this, "Record retreived.", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onError(QueryError e) {
			Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
		}

	};
};
