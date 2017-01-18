package com.robotemplates.cookbook.database.dao;

import com.robotemplates.cookbook.database.DatabaseHelper;
import com.robotemplates.cookbook.utility.Logcat;

import java.sql.SQLException;


public class DAO
{
	public static void printDatabaseInfo()
	{
		DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
		try
		{
			Logcat.d("%d categories", databaseHelper.getCategoryDao().countOf());
			Logcat.d("%d recipes", databaseHelper.getRecipeDao().countOf());
			Logcat.d("%d ingredients", databaseHelper.getIngredientDao().countOf());
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
}
