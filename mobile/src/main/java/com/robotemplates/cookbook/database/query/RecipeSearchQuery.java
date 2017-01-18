package com.robotemplates.cookbook.database.query;

import com.robotemplates.cookbook.database.dao.RecipeDAO;
import com.robotemplates.cookbook.database.data.Data;
import com.robotemplates.cookbook.database.model.RecipeModel;

import java.sql.SQLException;
import java.util.List;


public class RecipeSearchQuery extends Query
{
	private String mQuery;
	private long mSkip = -1L;
	private long mTake = -1L;


	public RecipeSearchQuery(String query)
	{
		mQuery = query;
	}


	public RecipeSearchQuery(String query, long skip, long take)
	{
		mQuery = query;
		mSkip = skip;
		mTake = take;
	}


	@Override
	public Data<List<RecipeModel>> processData() throws SQLException
	{
		Data<List<RecipeModel>> data = new Data<>();
		data.setDataObject(RecipeDAO.search(mQuery, mSkip, mTake));
		return data;
	}
}
