package com.robotemplates.cookbook.database.query;

import com.robotemplates.cookbook.database.dao.RecipeDAO;
import com.robotemplates.cookbook.database.data.Data;

import java.sql.SQLException;


public class RecipeDeleteQuery extends Query
{
	private long mId;


	public RecipeDeleteQuery(long id)
	{
		mId = id;
	}


	@Override
	public Data<Integer> processData() throws SQLException
	{
		Data<Integer> data = new Data<>();
		data.setDataObject(RecipeDAO.delete(mId));
		return data;
	}
}
