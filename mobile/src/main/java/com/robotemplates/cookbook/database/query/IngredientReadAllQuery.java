package com.robotemplates.cookbook.database.query;

import com.robotemplates.cookbook.database.dao.IngredientDAO;
import com.robotemplates.cookbook.database.data.Data;
import com.robotemplates.cookbook.database.model.IngredientModel;

import java.sql.SQLException;
import java.util.List;


public class IngredientReadAllQuery extends Query
{
	private long mSkip = -1L;
	private long mTake = -1L;


	public IngredientReadAllQuery()
	{
	}


	public IngredientReadAllQuery(long skip, long take)
	{
		mSkip = skip;
		mTake = take;
	}


	@Override
	public Data<List<IngredientModel>> processData() throws SQLException
	{
		Data<List<IngredientModel>> data = new Data<>();
		data.setDataObject(IngredientDAO.readAll(mSkip, mTake));
		return data;
	}
}
