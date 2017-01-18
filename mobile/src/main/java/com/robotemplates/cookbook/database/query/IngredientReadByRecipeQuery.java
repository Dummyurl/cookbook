package com.robotemplates.cookbook.database.query;

import com.robotemplates.cookbook.database.dao.IngredientDAO;
import com.robotemplates.cookbook.database.data.Data;
import com.robotemplates.cookbook.database.model.IngredientModel;

import java.sql.SQLException;
import java.util.List;


public class IngredientReadByRecipeQuery extends Query
{
	private long mRecipeId;
	private long mSkip = -1L;
	private long mTake = -1L;


	public IngredientReadByRecipeQuery(long recipeId)
	{
		mRecipeId = recipeId;
	}


	public IngredientReadByRecipeQuery(long recipeId, long skip, long take)
	{
		mRecipeId = recipeId;
		mSkip = skip;
		mTake = take;
	}


	@Override
	public Data<List<IngredientModel>> processData() throws SQLException
	{
		Data<List<IngredientModel>> data = new Data<>();
		data.setDataObject(IngredientDAO.readByRecipe(mRecipeId, mSkip, mTake));
		return data;
	}
}
