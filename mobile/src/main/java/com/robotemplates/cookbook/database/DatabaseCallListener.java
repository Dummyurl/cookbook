package com.robotemplates.cookbook.database;

import com.robotemplates.cookbook.database.data.Data;


public interface DatabaseCallListener
{
	void onDatabaseCallRespond(DatabaseCallTask task, Data<?> data);
	void onDatabaseCallFail(DatabaseCallTask task, Exception exception);
}
