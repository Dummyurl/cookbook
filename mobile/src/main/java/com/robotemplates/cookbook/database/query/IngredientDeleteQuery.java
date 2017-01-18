package com.robotemplates.cookbook.database.query;

import com.robotemplates.cookbook.database.dao.IngredientDAO;
import com.robotemplates.cookbook.database.data.Data;

import java.sql.SQLException;


public class IngredientDeleteQuery extends Query
{
	private long mId;


	public IngredientDeleteQuery(long id)
	{
		mId = id;
	}


	@Override
	public Data<Integer> processData() throws SQLException
	{
		Data<Integer> data = new Data<>();
		data.setDataObject(IngredientDAO.delete(mId));
		return data;
	}
}
