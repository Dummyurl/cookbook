package com.robotemplates.cookbook.database.query;

import com.robotemplates.cookbook.database.dao.RecipeDAO;
import com.robotemplates.cookbook.database.data.Data;

import java.sql.SQLException;


public class RecipeDeleteAllQuery extends Query
{
	public RecipeDeleteAllQuery()
	{
	}


	@Override
	public Data<Integer> processData() throws SQLException
	{
		Data<Integer> data = new Data<>();
		data.setDataObject(RecipeDAO.deleteAll());
		return data;
	}
}
