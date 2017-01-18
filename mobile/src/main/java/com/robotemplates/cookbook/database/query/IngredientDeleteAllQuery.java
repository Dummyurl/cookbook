package com.robotemplates.cookbook.database.query;

import com.robotemplates.cookbook.database.dao.IngredientDAO;
import com.robotemplates.cookbook.database.data.Data;

import java.sql.SQLException;


public class IngredientDeleteAllQuery extends Query
{
	public IngredientDeleteAllQuery()
	{
	}


	@Override
	public Data<Integer> processData() throws SQLException
	{
		Data<Integer> data = new Data<>();
		data.setDataObject(IngredientDAO.deleteAll());
		return data;
	}
}
