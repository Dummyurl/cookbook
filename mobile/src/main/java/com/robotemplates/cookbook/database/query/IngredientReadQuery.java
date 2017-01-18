package com.robotemplates.cookbook.database.query;

import com.robotemplates.cookbook.database.dao.IngredientDAO;
import com.robotemplates.cookbook.database.data.Data;
import com.robotemplates.cookbook.database.model.IngredientModel;

import java.sql.SQLException;


public class IngredientReadQuery extends Query
{
	private long mId;


	public IngredientReadQuery(long id)
	{
		mId = id;
	}


	@Override
	public Data<IngredientModel> processData() throws SQLException
	{
		Data<IngredientModel> data = new Data<>();
		data.setDataObject(IngredientDAO.read(mId));
		return data;
	}
}
