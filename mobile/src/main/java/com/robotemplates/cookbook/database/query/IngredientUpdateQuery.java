package com.robotemplates.cookbook.database.query;

import com.robotemplates.cookbook.database.dao.IngredientDAO;
import com.robotemplates.cookbook.database.data.Data;
import com.robotemplates.cookbook.database.model.IngredientModel;

import java.sql.SQLException;


public class IngredientUpdateQuery extends Query
{
	private IngredientModel mIngredient;


	public IngredientUpdateQuery(IngredientModel ingredient)
	{
		mIngredient = ingredient;
	}


	@Override
	public Data<Integer> processData() throws SQLException
	{
		Data<Integer> data = new Data<>();
		data.setDataObject(IngredientDAO.update(mIngredient));
		return data;
	}
}
