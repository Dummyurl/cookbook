package com.robotemplates.cookbook.database.query;

import com.robotemplates.cookbook.database.dao.RecipeDAO;
import com.robotemplates.cookbook.database.data.Data;
import com.robotemplates.cookbook.database.model.RecipeModel;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;


public class RecipeSortByViewersQuery extends Query
{
	private long mSkip = -1L;
	private long mTake = -1L;


	public RecipeSortByViewersQuery()
	{
	}


	public RecipeSortByViewersQuery(long skip, long take)
	{
		mSkip = skip;
		mTake = take;
	}


	@Override
	public Data<List<RecipeModel>> processData() throws SQLException
	{
		Data<List<RecipeModel>> data = new Data<>();
		List<RecipeModel> listSort = RecipeDAO.sortByViewers(mSkip, mTake);
		Collections.sort(listSort, RecipeModel.ASCENDING_COMPARATOR);
		data.setDataObject(listSort);
		return data;
	}
}
