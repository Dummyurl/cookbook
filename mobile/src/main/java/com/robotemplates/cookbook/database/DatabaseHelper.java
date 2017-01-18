package com.robotemplates.cookbook.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.robotemplates.cookbook.CookbookApplication;
import com.robotemplates.cookbook.CookbookConfig;
import com.robotemplates.cookbook.database.model.CategoryModel;
import com.robotemplates.cookbook.database.model.IngredientModel;
import com.robotemplates.cookbook.database.model.RecipeModel;
import com.robotemplates.cookbook.utility.Logcat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = CookbookConfig.DATABASE_NAME;
    private static final String DATABASE_PATH = "/data/data/" + CookbookApplication.getContext().getPackageName() + "/databases/";
    private static final int DATABASE_VERSION = CookbookConfig.DATABASE_VERSION;
    private static final String PREFS_KEY_DATABASE_VERSION = "database_version";

    private static DatabaseHelper sInstance;

    private Dao<CategoryModel, Long> mCategoryDao = null;
    private Dao<RecipeModel, Long> mRecipeDao = null;
    private Dao<IngredientModel, Long> mIngredientDao = null;


    // singleton
//    public static synchronized DatabaseHelper getInstance() {
//        if (sInstance == null) sInstance = new DatabaseHelper();
//        return sInstance;
//    }


//    private DatabaseHelper() {
//        super(CookbookApplication.getContext(), DATABASE_PATH + DATABASE_NAME, null, DATABASE_VERSION);
//        sInstance = DatabaseHelper.this;
//        if (!databaseExists() || DATABASE_VERSION > getVersion()) {
//            synchronized (this) {
//                Log.e("getVersion","getVersion??"+getVersion());
//                boolean success = copyPrepopulatedDatabase();
//                Log.e("success","success b??"+success);
//                if (success) {
//                    Log.e("success","success??"+success);
//                    setVersion(DATABASE_VERSION);
//                }
//            }
//        }
//    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        sInstance = DatabaseHelper.this;
        if (!databaseExists()) {
            synchronized (this) {
                boolean success = copyPrepopulatedDatabase();
                Log.e("copyDatabase","success??"+success);
            }
        }
    }

    public static DatabaseHelper getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Logcat.e("onCreate");

            TableUtils.createTable(connectionSource, CategoryModel.class);
            TableUtils.createTable(connectionSource, RecipeModel.class);
            TableUtils.createTable(connectionSource, IngredientModel.class);

        } catch (android.database.SQLException e) {
            Logcat.e(e, "can't create database");
            e.printStackTrace();
        }catch (java.sql.SQLException e) {
            Logcat.e(e, "can't create database");
            e.printStackTrace();
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {

            Logcat.e("onUpgrade");
            TableUtils.dropTable(connectionSource, CategoryModel.class, true);
            TableUtils.dropTable(connectionSource, RecipeModel.class, true);
            TableUtils.dropTable(connectionSource, IngredientModel.class, true);
            onCreate(db, connectionSource);

        } catch (android.database.SQLException e) {
            Logcat.e(e, "can't upgrade database");
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            Logcat.e(e, "can't clear database");
            e.printStackTrace();
        }
    }


    @Override
    public void close() {
        super.close();
        mCategoryDao = null;
        mRecipeDao = null;
        mIngredientDao = null;
    }


    public synchronized void clearDatabase() {
        try {
            Logcat.e("clearDatabase");

            TableUtils.dropTable(getConnectionSource(), CategoryModel.class, true);
            TableUtils.dropTable(getConnectionSource(), RecipeModel.class, true);
            TableUtils.dropTable(getConnectionSource(), IngredientModel.class, true);

            TableUtils.createTable(getConnectionSource(), CategoryModel.class);
            TableUtils.createTable(getConnectionSource(), RecipeModel.class);
            TableUtils.createTable(getConnectionSource(), IngredientModel.class);
        } catch (android.database.SQLException e) {
            Logcat.e(e, "can't clear database");
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            Logcat.e(e, "can't clear database");
            e.printStackTrace();
        }
    }


    public synchronized Dao<CategoryModel, Long> getCategoryDao() throws java.sql.SQLException {
        if (mCategoryDao == null) {
            mCategoryDao = getDao(CategoryModel.class);
        }
        return mCategoryDao;
    }


    public synchronized Dao<RecipeModel, Long> getRecipeDao() throws java.sql.SQLException {
        if (mRecipeDao == null) {
            mRecipeDao = getDao(RecipeModel.class);
        }
        return mRecipeDao;
    }


    public synchronized Dao<IngredientModel, Long> getIngredientDao() throws java.sql.SQLException {
        if (mIngredientDao == null) {
            mIngredientDao = getDao(IngredientModel.class);
        }
        return mIngredientDao;
    }


    public boolean databaseExists() {
        File file = new File(DATABASE_PATH + DATABASE_NAME);
        boolean exists = file.exists();
        return exists;
    }


    private boolean assetExists(String fileName) {
        AssetManager assetManager = CookbookApplication.getContext().getAssets();
        try {
            List<String> list = Arrays.asList(assetManager.list(""));
            return list.contains(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private boolean copyPrepopulatedDatabase() {
        // copy database from assets
        try {
            // create directories
            File dir = new File(DATABASE_PATH);
            dir.mkdirs();

            // input file name
            String inputFileName = DATABASE_NAME;
            String lang = Locale.getDefault().getLanguage();
            String translatedFileName = lang + "_" + DATABASE_NAME;
            if (assetExists(translatedFileName)) {
                inputFileName = translatedFileName;
            }

            // output file name
            String outputFileName = DATABASE_PATH + DATABASE_NAME;

            // create streams
            InputStream inputStream = CookbookApplication.getContext().getAssets().open(inputFileName);
            OutputStream outputStream = new FileOutputStream(outputFileName);

            // write input to output
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            // close streams
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    private int getVersion() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CookbookApplication.getContext());
        return sharedPreferences.getInt(PREFS_KEY_DATABASE_VERSION, 0);
    }


    private void setVersion(int version) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CookbookApplication.getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREFS_KEY_DATABASE_VERSION, version);
        editor.commit();
    }
}
