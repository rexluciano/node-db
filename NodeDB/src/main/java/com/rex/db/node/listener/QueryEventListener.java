package com.rex.db.node.listener;

import com.rex.db.node.query.Query;
import com.rex.db.node.query.QueryError;

public interface QueryEventListener {
	void onQuery(Query q);

	void onError(QueryError e);
}