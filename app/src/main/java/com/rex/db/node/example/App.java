package com.rex.db.node.example;

import android.app.Application;
import android.content.Context;
import com.rex.db.node.NodeApp;

public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		NodeApp.initialize(this);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

}