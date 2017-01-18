package com.robotemplates.cookbook.database.query;

import com.robotemplates.cookbook.database.dao.CategoryDAO;
import com.robotemplates.cookbook.database.data.Data;
import com.robotemplates.cookbook.database.model.CategoryModel;

import java.sql.SQLException;


public class CategoryReadQuery extends Query
{
	private long mId;


	public CategoryReadQuery(long id)
	{
		mId = id;
	}


	@Override
	public Data<CategoryModel> processData() throws SQLException
	{
		Data<CategoryModel> data = new Data<>();
		data.setDataObject(CategoryDAO.read(mId));
		return data;
	}
}
