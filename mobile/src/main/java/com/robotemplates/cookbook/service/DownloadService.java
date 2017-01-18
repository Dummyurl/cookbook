package com.robotemplates.cookbook.service;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;
import com.robotemplates.cookbook.activity.MainActivity;
import com.robotemplates.cookbook.activity.SplashActivity;
import com.robotemplates.cookbook.database.DatabaseHelper;
import com.robotemplates.cookbook.database.model.CategoryModel;
import com.robotemplates.cookbook.database.model.IngredientModel;
import com.robotemplates.cookbook.database.model.RecipeModel;
import com.robotemplates.cookbook.interfaces.Constant;
import com.robotemplates.cookbook.interfaces.RestClient;
import com.robotemplates.cookbook.modal.Download;
import com.robotemplates.cookbook.pojo.CookbookDataPOJO;
import com.robotemplates.cookbook.preferences.Preference;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DownloadService extends IntentService implements Constant, Callback<CookbookDataPOJO> {

    private static final String TAG = DownloadService.class.getSimpleName();

    public DownloadService() {
        super("Download Service");
    }

    private int totalFileSize;

    private DatabaseHelper databaseHelper = null;
    private Dao<CategoryModel, Long> mCategoryDao = null;
    private Dao<RecipeModel, Long> mRecipeDao = null;
    private Dao<IngredientModel, Long> mIngredientDao = null;
    private String activityName;

    @Override
    protected void onHandleIntent(Intent intent) {
        activityName = intent.getStringExtra(ACTIVITY_NAME);
        initDownload();
    }

    @Override
    public void onCreate() {
        super.onCreate();
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

    private void initDownload() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        getAllDataForDataBase(gson);
    }


    /* Get All Data For DataBase From Server */
    private void getAllDataForDataBase(Gson gson) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ROOT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RestClient restClientAPI = retrofit.create(RestClient.class);
        String userId = "";
        if (Preference.getLoginId(this) != null && !Preference.getLoginId(this).equals("")) {
            userId = Preference.getLoginId(this);
        }
        Call<CookbookDataPOJO> call = restClientAPI.getDataRequest(userId);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<CookbookDataPOJO> call, Response<CookbookDataPOJO> response) {
        CookbookDataPOJO cookbookDataPOJO = response.body();

        int code = response.code();
        if (code == RESPONSE_CODE) {
            if (cookbookDataPOJO.getSuccess() == SUCCESS) {

                Log.i(TAG, "CookbookData_TRUE??" + cookbookDataPOJO.getSuccess());
                setUpDataBase(cookbookDataPOJO.getCategories(),
                        cookbookDataPOJO.getRecipes(),
                        cookbookDataPOJO.getIngredients());

            } else {

                Log.i(TAG, "CookbookData_FALSE??" + cookbookDataPOJO.getSuccess());

            }
        } else {
            Log.e(TAG, "CookbookData_FAILED_CODE_ERROR_404,505??" + code);
        }
    }

    /* SetUp DataBase */
    private void setUpDataBase(List<CookbookDataPOJO.Category> categories, List<CookbookDataPOJO.Recipe> recipes, List<CookbookDataPOJO.Ingredient> ingredients) {

        try {

            mCategoryDao = getHelper().getCategoryDao();
            mRecipeDao = getHelper().getRecipeDao();
            mIngredientDao = getHelper().getIngredientDao();

            if (mCategoryDao.isTableExists()) {

                if (DatabaseHelper.getInstance() != null) {
                    TableUtils.dropTable(DatabaseHelper.getInstance().getConnectionSource(), CategoryModel.class, true);
                    TableUtils.createTable(DatabaseHelper.getInstance().getConnectionSource(), CategoryModel.class);
                }
            }

            if (mRecipeDao.isTableExists()) {

                if (DatabaseHelper.getInstance() != null) {
                    TableUtils.dropTable(DatabaseHelper.getInstance().getConnectionSource(), RecipeModel.class, true);
                    TableUtils.createTable(DatabaseHelper.getInstance().getConnectionSource(), RecipeModel.class);
                }
            }

            if (mIngredientDao.isTableExists()) {

                if (DatabaseHelper.getInstance() != null) {
                    TableUtils.dropTable(DatabaseHelper.getInstance().getConnectionSource(), IngredientModel.class, true);
                    TableUtils.createTable(DatabaseHelper.getInstance().getConnectionSource(), IngredientModel.class);
                }
            }

            if (mCategoryDao.isTableExists()) {
                long numRows = mCategoryDao.countOf();
                if (numRows != 0) {
                    QueryBuilder<CategoryModel, Long> queryBuilder = mCategoryDao.queryBuilder();

                    for (int i = 0; i < mCategoryDao.queryForAll().size(); i++) {

                        long id = mCategoryDao.queryForAll().get(i).getId();

                        List<CategoryModel> results = queryBuilder.where().eq(CategoryModel.COLUMN_ID, id).query();

                        if (results.size() == 0) {

                            setCategoryData(i, categories);

                        } else {
                            updateCategoryData(id, categories);
                        }

                    }

                } else {
                    Log.i("else1", "else1??");
                    if (categories != null && categories.size() > 0) {
                        for (int i = 0; i < categories.size(); i++) {

                            setCategoryData(i, categories);

                        }
                    }
                }
            }

            if (mRecipeDao.isTableExists()) {
                long numRows = mRecipeDao.countOf();
                if (numRows != 0) {
                    QueryBuilder<RecipeModel, Long> queryBuilder = mRecipeDao.queryBuilder();
                    if (recipes != null && recipes.size() > 0) {

                        for (int i = 0; i < recipes.size(); i++) {

                            long id = Long.parseLong(String.valueOf(recipes.get(i).getId()));

                            List<RecipeModel> results = queryBuilder.where().eq(RecipeModel.COLUMN_ID, id).query();

                            if (results.size() == 0) {

                                setRecipeData(i, recipes);

                            } else {

                                updateRecipeData(id, recipes);
                            }
                        }
                    }

                } else {
                    Log.i("else2", "else2??");
                    if (recipes != null && recipes.size() > 0) {

                        for (int i = 0; i < recipes.size(); i++) {

                            setRecipeData(i, recipes);

                        }
                    }

                }
            }

            if (mIngredientDao.isTableExists()) {
                long numRows = mIngredientDao.countOf();
                if (numRows != 0) {
                    QueryBuilder<IngredientModel, Long> queryBuilder = mIngredientDao.queryBuilder();
                    if (ingredients != null && ingredients.size() > 0) {

                        for (int i = 0; i < mIngredientDao.queryForAll().size(); i++) {

                            long id = mIngredientDao.queryForAll().get(i).getId();

                            List<IngredientModel> results = queryBuilder.where().eq(IngredientModel.COLUMN_ID, id).query();

                            if (results.size() == 0) {

                                setIngredientData(i, ingredients);

                            } else {

                                updateIngredientData(id, ingredients);
                            }
                        }
                    }
                } else {
                    Log.i("else3", "else3??");
                    if (ingredients != null && ingredients.size() > 0) {

                        for (int i = 0; i < ingredients.size(); i++) {

                            setIngredientData(i, ingredients);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        onDownloadComplete();
    }

    @Override
    public void onFailure(Call<CookbookDataPOJO> call, Throwable t) {
        Log.e(TAG, "<<<Failure With CookbookDataPOJO>>>" + t.getMessage());
    }

    private void setCategoryData(int i, List<CookbookDataPOJO.Category> categories) {
        try {

            CategoryModel categoryModel = new CategoryModel();
            //categoryModel.setId(i + 1);
            categoryModel.setName(categories.get(i).getName());
            categoryModel.setImage(categories.get(i).getImage());
            mCategoryDao.create(categoryModel);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* Not Used Now */
    private void updateCategoryData(long id, List<CookbookDataPOJO.Category> categories) {
        try {

            CategoryModel categoryModel = mCategoryDao.queryForId(id);
            categoryModel.setName(categories.get(Integer.parseInt(String.valueOf(id)) - 1).getName());
            categoryModel.setImage(categories.get(Integer.parseInt(String.valueOf(id)) - 1).getImage());
            mCategoryDao.update(categoryModel);
            mCategoryDao.refresh(categoryModel);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setRecipeData(int i, List<CookbookDataPOJO.Recipe> recipe) {
        try {
            RecipeModel recipeModel = new RecipeModel();
            recipeModel.setId(Long.parseLong(recipe.get(i).getId()));
            recipeModel.setCategory_id(Long.parseLong(recipe.get(i).getCategoryId()));
            recipeModel.setName(recipe.get(i).getName());
            recipeModel.setIntro(recipe.get(i).getIntro());
            recipeModel.setInstruction(recipe.get(i).getInstruction());
            recipeModel.setImage(recipe.get(i).getImage());
            recipeModel.setLink(recipe.get(i).getLink());
            recipeModel.setTime(Integer.parseInt(recipe.get(i).getTime()));
            recipeModel.setServings(Integer.parseInt(recipe.get(i).getServings()));
            recipeModel.setCalories(Integer.parseInt(recipe.get(i).getCalories()));
            recipeModel.setFavorite_count(Long.parseLong(recipe.get(i).getFavoriteCount()));
            recipeModel.setViewers_count(Long.parseLong(recipe.get(i).getViewerCount()));
            recipeModel.setViewer(FALSE);
            if (recipe.get(i).getFavorite().equals("0")) {
                recipeModel.setFavorite(FALSE);
            } else {
                recipeModel.setFavorite(TRUE);
            }
            mRecipeDao.create(recipeModel);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* Not Used Now */
    private void updateRecipeData(long id, List<CookbookDataPOJO.Recipe> recipe) {
        try {

            RecipeModel recipeModel = mRecipeDao.queryForId(id);
            recipeModel.setId(Long.parseLong(recipe.get(Integer.parseInt(String.valueOf(id)) - 1).getId()));
            recipeModel.setCategory_id(Long.parseLong(recipe.get(Integer.parseInt(String.valueOf(id)) - 1).getCategoryId()));
            recipeModel.setName(recipe.get(Integer.parseInt(String.valueOf(id)) - 1).getName());
            recipeModel.setIntro(recipe.get(Integer.parseInt(String.valueOf(id)) - 1).getIntro());
            recipeModel.setInstruction(recipe.get(Integer.parseInt(String.valueOf(id)) - 1).getInstruction());
            recipeModel.setImage(recipe.get(Integer.parseInt(String.valueOf(id)) - 1).getImage());
            recipeModel.setLink(recipe.get(Integer.parseInt(String.valueOf(id)) - 1).getLink());
            recipeModel.setTime(Integer.parseInt(recipe.get(Integer.parseInt(String.valueOf(id)) - 1).getTime()));
            recipeModel.setServings(Integer.parseInt(recipe.get(Integer.parseInt(String.valueOf(id)) - 1).getServings()));
            recipeModel.setCalories(Integer.parseInt(recipe.get(Integer.parseInt(String.valueOf(id)) - 1).getCalories()));
            if (recipe.get(Integer.parseInt(String.valueOf(id)) - 1).getFavorite().equals("0")) {
                recipeModel.setFavorite(FALSE);
            } else {
                recipeModel.setFavorite(TRUE);
            }
            mRecipeDao.update(recipeModel);
            mRecipeDao.refresh(recipeModel);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setIngredientData(int i, List<CookbookDataPOJO.Ingredient> ingredient) {
        try {

            IngredientModel ingredientModel = new IngredientModel();
            //ingredientModel.setId(i + 1);
            ingredientModel.setRecipe_id(Long.parseLong(ingredient.get(i).getRecipeId()));
            ingredientModel.setName(ingredient.get(i).getName());
            if (!ingredient.get(i).getQuantity().equals("")) {
                ingredientModel.setQuantity(Float.parseFloat(ingredient.get(i).getQuantity()));
            } else {
                ingredientModel.setQuantity(0);
            }
            ingredientModel.setUnit(ingredient.get(i).getUnit());
            mIngredientDao.create(ingredientModel);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* Not Used Now */
    private void updateIngredientData(long id, List<CookbookDataPOJO.Ingredient> ingredient) {
        try {

            IngredientModel ingredientModel = mIngredientDao.queryForId(id);
            ingredientModel.setName(ingredient.get(Integer.parseInt(String.valueOf(id)) - 1).getName());
            ingredientModel.setQuantity(Float.parseFloat(ingredient.get(Integer.parseInt(String.valueOf(id)) - 1).getQuantity()));
            ingredientModel.setUnit(ingredient.get(Integer.parseInt(String.valueOf(id)) - 1).getUnit());
            mIngredientDao.update(ingredientModel);
            mIngredientDao.refresh(ingredientModel);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void downloadFile(ResponseBody body) throws IOException {

        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
        //File outputFile = new File(SplashActivity.DATABASE_PATH + SplashActivity.DATABASE_NAME);
        //OutputStream output = new FileOutputStream(outputFile);
        long total = 0;
        long startTime = System.currentTimeMillis();
        int timeCount = 1;
        while ((count = bis.read(data)) != -1) {
            total += count;
            totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
            double current = Math.round(total / (Math.pow(1024, 2)));

            int progress = (int) ((total * 100) / fileSize);

            long currentTime = System.currentTimeMillis() - startTime;

            Download download = new Download();
            download.setTotalFileSize(totalFileSize);

            if (currentTime > 1000 * timeCount) {

                download.setCurrentFileSize((int) current);
                download.setProgress(progress);
                sendNotification(download);
                timeCount++;
            }

            //output.write(data, 0, count);
        }
        onDownloadComplete();
        //output.flush();
        //output.close();
        bis.close();

    }

    private void sendNotification(Download download) {

        sendIntent(download);

    }

    private void sendIntent(Download download) {
        Intent intent;
        if (download.getActivityName().equals("SplashActivity")) {
            Log.i("Pass To:-", "SplashActivity");
            intent = new Intent(SplashActivity.MESSAGE_PROGRESS_SPLASH);
        } else if (download.getActivityName().equals("MainActivity")) {
            Log.i("Pass To:-", "MainActivity");
            intent = new Intent(MainActivity.MESSAGE_PROGRESS_MAIN);
        } else {
            Log.i("Pass To:-", "SocialLoginActivity");
            intent = new Intent(MainActivity.MESSAGE_PROGRESS_LOGIN);
        }
        intent.putExtra("download", download);
        LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
    }

    private void onDownloadComplete() {
        Download download = new Download();
        download.setProgress(100);
        download.setActivityName(activityName);
        sendNotification(download);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

    }

    /* getDeviceId */
    public String getDeviceId(Context ctx) {
        return Settings.Secure.getString(ctx.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }


}
