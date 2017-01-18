package com.robotemplates.cookbook.database.query;

import com.robotemplates.cookbook.database.dao.RecipeDAO;
import com.robotemplates.cookbook.database.data.Data;
import com.robotemplates.cookbook.database.model.RecipeModel;

import java.sql.SQLException;


public class RecipeReadQuery extends Query
{
	private long mId;


	public RecipeReadQuery(long id)
	{
		mId = id;
	}


	@Override
	public Data<RecipeModel> processData() throws SQLException
	{
		Data<RecipeModel> data = new Data<>();
		data.setDataObject(RecipeDAO.read(mId));
		return data;
	}
}
