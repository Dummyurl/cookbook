package com.robotemplates.cookbook.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.robotemplates.cookbook.activity.SplashActivity;
import com.robotemplates.cookbook.database.DatabaseHelper;
import com.robotemplates.cookbook.database.model.RecipeModel;
import com.robotemplates.cookbook.interfaces.Constant;
import com.robotemplates.cookbook.interfaces.RestClient;
import com.robotemplates.cookbook.pojo.Favourite;
import com.robotemplates.cookbook.pojo.Viewer;
import com.robotemplates.cookbook.preferences.Preference;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SendDataService extends Service implements Constant {
    private static final String TAG = SendDataService.class.getSimpleName();
    private DatabaseHelper databaseHelper = null;
    private Dao<RecipeModel, Long> mRecipeDao = null;
    private ArrayList<String> listRecipeID;
    private ArrayList<String> listViewID;
    private String recipeIDStr = "";
    private String recipeViewIDStr = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /* DatabaseHelper */
    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(SplashActivity.getInstance(), DatabaseHelper.class);
        }
        return databaseHelper;
    }

    private void initData() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        checkFavouriteInDataBase(gson);
    }

    /* Check Favourites In DataBase */
    private void checkFavouriteInDataBase(Gson gson) {
        try {
            mRecipeDao = getHelper().getRecipeDao();

            if (mRecipeDao.isTableExists()) {
                long numRows = mRecipeDao.countOf();
                if (numRows != 0) {
                    listRecipeID = new ArrayList<>();
                    listViewID = new ArrayList<>();

                    QueryBuilder<RecipeModel, Long> queryBuilder = mRecipeDao.queryBuilder();

                    // Get List Of RecipeId Where viewer is true(1)
                    List<RecipeModel> resultsView = queryBuilder.where().eq(RecipeModel.COLUMN_VIEWER, true).query();

                    if (resultsView.size() > 0) {

                        for (RecipeModel recipeModel : resultsView) {
                            Log.i("recipe_view", "id?" + recipeModel.getId());
                            listViewID.add(String.valueOf(recipeModel.getId()));
                        }

                        recipeViewIDStr = buildCommaSeparatedString(listViewID);
                        Log.i("Viewers", "Str??" + recipeViewIDStr);
                    } else {
                        Log.i("Viewers", "Str2??" + recipeViewIDStr);
                    }


                    // Get List Of RecipeId Where favourite is true(1)
                    List<RecipeModel> results = queryBuilder.where().eq(RecipeModel.COLUMN_FAVORITE, true).query();
                    if (results.size() > 0) {

                        for (RecipeModel recipeModel : results) {
                            Log.i("recipe_fav", "id?" + recipeModel.getId());
                            listRecipeID.add(String.valueOf(recipeModel.getId()));
                        }

                        recipeIDStr = buildCommaSeparatedString(listRecipeID);
                        Log.i("favourite", "Str??" + recipeIDStr);
                        setFavouriteWithId(recipeIDStr, gson);

                    } else {
                        Log.i("favourite", "Str2??" + recipeIDStr);
                        setFavouriteWithId(recipeIDStr, gson);
                    }

                } else {
                    Log.i("JSON", "BlankStringFav??" + recipeIDStr);
                    Log.i("JSON", "BlankStringViewer??" + recipeViewIDStr);
                    setFavouriteWithId(recipeIDStr, gson);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static <T> String buildCommaSeparatedString(Collection<T> values) {
        if (values == null || values.isEmpty()) return "";
        StringBuilder result = new StringBuilder();
        for (T val : values) {
            result.append(val);
            result.append(",");
        }
        return result.substring(0, result.length() - 1);
    }

    /* Set Favourite With RecipeId */
    private void setFavouriteWithId(String recipeIDStr, Gson gson) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ROOT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestClient restClientAPI = retrofit.create(RestClient.class);
        Log.i("UserID", "UserID??" + Preference.getLoginId(this));
        String userId = "";
        if (Preference.getLoginId(this) != null && !Preference.getLoginId(this).equals("")) {
            userId = Preference.getLoginId(this);
        }
        Call<Favourite> call = restClientAPI.setFavouriteRequest(userId,
                recipeIDStr);

        Callback<Favourite> callback = new Callback<Favourite>() {
            @Override
            public void onResponse(Call<Favourite> call, Response<Favourite> response) {
                Favourite favouritePOJO = response.body();

                int code = response.code();
                if (code == RESPONSE_CODE) {
                    if (favouritePOJO.getSuccess() == SUCCESS) {

                        Log.i(TAG, "Favourite_TRUE??" + favouritePOJO.getSuccess());
                        setViewersWithId(recipeViewIDStr, gson);

                    } else {
                        Log.i(TAG, "Favourite_FALSE??" + favouritePOJO.getSuccess());
                        setViewersWithId(recipeViewIDStr, gson);
                    }

                } else {
                    Log.e(TAG, "Favourite_FAILED_CODE_ERROR_404,505??" + code);
                }
            }

            @Override
            public void onFailure(Call<Favourite> call, Throwable t) {
                Log.e(TAG, "<<<Failure With FavouritePOJO>>>" + t.getMessage());
            }
        };

        call.enqueue(callback);
    }

    /* Set Favourite With RecipeId */
    private void setViewersWithId(String recipeIDStr, Gson gson) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ROOT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestClient restClientAPI = retrofit.create(RestClient.class);
        String userId = "";
        if (Preference.getLoginId(this) != null && !Preference.getLoginId(this).equals("")) {
            userId = Preference.getLoginId(this);
        }
        Call<Viewer> call = restClientAPI.setViewerRequest(userId, recipeIDStr);

        Callback<Viewer> callback = new Callback<Viewer>() {
            @Override
            public void onResponse(Call<Viewer> call, Response<Viewer> response) {
                Viewer viewerPOJO = response.body();

                int code = response.code();
                if (code == RESPONSE_CODE) {
                    if (viewerPOJO.getSuccess() == SUCCESS) {

                        Log.i(TAG, "Viewer_TRUE??" + viewerPOJO.getSuccess());
                        stopSelf();

                    } else {
                        Log.i(TAG, "Viewer_FALSE??" + viewerPOJO.getSuccess());
                        stopSelf();
                    }

                } else {
                    Log.e(TAG, "Viewer_FAILED_CODE_ERROR_404,505??" + code);
                    stopSelf();
                }
            }

            @Override
            public void onFailure(Call<Viewer> call, Throwable t) {
                Log.e(TAG, "<<<Viewer With ViewerPOJO>>>" + t.getMessage());
                stopSelf();
            }
        };

        call.enqueue(callback);
    }

}
