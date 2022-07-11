package com.rex.db.node;

import android.content.Context;
import com.rex.db.node.utils.FileUtil;
import java.io.File;

public class NodeApp {

	private static Context context;
	private static File dbPath;

	private static NodeApp mInstance;

	protected NodeApp(Context mContext) {
		context = mContext;
		dbPath = new File(context.getFilesDir().getPath(), "node_database");
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
		return dbPath;
	}

	protected void init() {
		if (context != null) {
			if (!dbPath.exists()) {
				dbPath.mkdir();
				dbPath.mkdirs();
			}
		}
	}
}