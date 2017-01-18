package com.robotemplates.cookbook.database.query;

import com.robotemplates.cookbook.database.dao.RecipeDAO;
import com.robotemplates.cookbook.database.data.Data;
import com.robotemplates.cookbook.database.model.RecipeModel;

import java.sql.SQLException;


public class RecipeUpdateQuery extends Query
{
	private RecipeModel mRecipe;


	public RecipeUpdateQuery(RecipeModel recipe)
	{
		mRecipe = recipe;
	}


	@Override
	public Data<Integer> processData() throws SQLException
	{
		Data<Integer> data = new Data<>();
		data.setDataObject(RecipeDAO.update(mRecipe));
		return data;
	}
}
