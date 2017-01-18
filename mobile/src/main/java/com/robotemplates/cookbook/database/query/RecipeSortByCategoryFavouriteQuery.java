package com.robotemplates.cookbook.database.query;

import android.util.Log;

import com.robotemplates.cookbook.database.dao.RecipeDAO;
import com.robotemplates.cookbook.database.data.Data;
import com.robotemplates.cookbook.database.model.RecipeModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RecipeSortByCategoryFavouriteQuery extends Query {
    private long mCategoryId;
    private long mSkip = -1L;
    private long mTake = -1L;
    private String mQuery;


    public RecipeSortByCategoryFavouriteQuery(long categoryId) {
        mCategoryId = categoryId;
    }


    public RecipeSortByCategoryFavouriteQuery(String query, long categoryId, long skip, long take) {
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
            Log.e("1_fav","1");
            listSort = RecipeDAO.readAll(mSkip, mTake);

        }else if(mCategoryId == -2L){
            Log.e("2_fav","2");
            listSort = RecipeDAO.readFavorites(mSkip, mTake);

        }else if(mCategoryId == -3L){
            Log.e("3_fav","3");
            listSort = RecipeDAO.search(mQuery, mSkip, mTake);

        }else{
            Log.e("4_fav","4");
            listSort = RecipeDAO.readByCategory(mCategoryId, mSkip, mTake);
        }
        Collections.sort(listSort, RecipeModel.ASCENDING_COMPARATOR);
        data.setDataObject(listSort);
        return data;
    }
}
