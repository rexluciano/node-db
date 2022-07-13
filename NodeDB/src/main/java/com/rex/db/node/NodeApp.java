package com.rex.db.node;

import android.app.Application;
import android.content.Context;
import com.rex.db.node.utils.FileUtil;
import java.io.File;

public class NodeApp {

	private static Context context;
	private static File dbPath;
	private boolean isInt;

	private static NodeApp mInstance;

	protected NodeApp(Context mContext) {
		context = mContext;
		dbPath = getPath();
		init();
	}

	public static NodeApp initialize(Context context) {
		if (mInstance == null) {
			mInstance = new NodeApp(context);
		}
		return mInstance;
	}

	public static Context getContext() {
		return context;
	}

	public static File getPath() {
		return new File(context.getFilesDir().getPath(), "node_database");
	}

	private void init() {
		if (context != null) {
			if (!dbPath.exists()) {
				dbPath.mkdir();
				dbPath.mkdirs();
			}
		}
	}
}