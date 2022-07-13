package com.rex.db.node.listener;

import com.rex.db.node.query.QueryError;
import com.rex.db.node.query.Query;

public interface ValueEventListener {
	void onQuery(Query q);

	void onError(QueryError e);
}