package com.robotemplates.cookbook.database.query;

import android.content.Context;
import android.util.Log;

import com.robotemplates.cookbook.R;
import com.robotemplates.cookbook.database.dao.RecipeDAO;
import com.robotemplates.cookbook.database.data.Data;
import com.robotemplates.cookbook.database.model.RecipeModel;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;


public class RecipeSortByCategoryViewerQuery extends Query {
    private long mCategoryId;
    private long mSkip = -1L;
    private long mTake = -1L;
    private String mQuery;


    public RecipeSortByCategoryViewerQuery(long categoryId) {
        mCategoryId = categoryId;
    }


    public RecipeSortByCategoryViewerQuery(String query, long categoryId, long skip, long take) {
        mQuery = query;
        mCategoryId = categoryId;
        mSkip = skip;
        mTake = take;

    }

    @Override
    public Data<List<RecipeModel>> processData() throws SQLException {
        Data<List<RecipeModel>> data = new Data<>();
        List<RecipeModel> listSort;
        if (mCategoryId == -1L){
            Log.e("1_view","1");
            listSort = RecipeDAO.readAll(mSkip, mTake);

        }else if(mCategoryId == -2L){
            Log.e("2_view","2");
            listSort = RecipeDAO.readFavorites(mSkip, mTake);

        }else if(mCategoryId == -3L){
            Log.e("3_view","3");
            listSort = RecipeDAO.search(mQuery, mSkip, mTake);

        }else{
            Log.e("4_view","4");
            listSort = RecipeDAO.readByCategory(mCategoryId, mSkip, mTake);
        }
        Collections.sort(listSort, RecipeModel.ASCENDING_COMPARATOR_VIEW);
        data.setDataObject(listSort);
        return data;
    }
}
