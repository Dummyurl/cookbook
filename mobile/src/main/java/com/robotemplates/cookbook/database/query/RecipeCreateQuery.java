package com.robotemplates.cookbook.database.query;

import com.robotemplates.cookbook.database.dao.RecipeDAO;
import com.robotemplates.cookbook.database.data.Data;
import com.robotemplates.cookbook.database.model.RecipeModel;

import java.sql.SQLException;


public class RecipeCreateQuery extends Query {
    private RecipeModel mRecipe;


    public RecipeCreateQuery(RecipeModel recipe) {
        mRecipe = recipe;
    }


    @Override
    public Data<Integer> processData() throws SQLException {
        Data<Integer> data = new Data<>();
        data.setDataObject(RecipeDAO.create(mRecipe));
        return data;
    }
}
