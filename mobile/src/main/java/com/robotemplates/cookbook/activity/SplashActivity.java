package com.robotemplates.cookbook.activity;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;
import com.robotemplates.cookbook.CookbookApplication;
import com.robotemplates.cookbook.R;
import com.robotemplates.cookbook.database.DatabaseHelper;
import com.robotemplates.cookbook.database.model.CategoryModel;
import com.robotemplates.cookbook.database.model.IngredientModel;
import com.robotemplates.cookbook.database.model.RecipeModel;
import com.robotemplates.cookbook.interfaces.Constant;
import com.robotemplates.cookbook.modal.Download;
import com.robotemplates.cookbook.preferences.Preference;
import com.robotemplates.cookbook.service.DownloadService;

import java.sql.SQLException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity implements Constant {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private static SplashActivity mActivity;

    public static SplashActivity getInstance() {
        return mActivity;
    }

    private DatabaseHelper databaseHelper = null;

    private LocalBroadcastManager bManager;

    @BindView(R.id.progress_text)
    TextView mProgressText;
    @BindView(R.id.circularFillableLoaders)
    CircularFillableLoaders circularFillableLoaders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mActivity = SplashActivity.this;
        ButterKnife.bind(this);

        // init analytics tracker
        ((CookbookApplication) getApplication()).getTracker();

        setUpDao();
        registerReceiver();
    }

    /* SetUp Dao ORM */
    private void setUpDao() {
        try {
            Dao<CategoryModel, Long> mCategoryDao = getHelper().getCategoryDao();
            Dao<RecipeModel, Long> mRecipeDao = getHelper().getRecipeDao();
            Dao<IngredientModel, Long> mIngredientDao = getHelper().getIngredientDao();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* DatabaseHelper */
    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    public void onResume() {
        super.onResume();

        // At activity startup we manually check the internet status and change
        // the text status
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (checkPermission()) {
                mProgressText.setText(getString(R.string.loading));
                if (Preference.getLoginId(SplashActivity.this) != null && !Preference.getLoginId(SplashActivity.this).equals("")) {
                    startDownload();
                }else{
                    checkLogInPreferences();
                }
            } else {
                requestPermission();
            }
        } else {
            mProgressText.setText(getString(R.string.loading));
            Snackbar.make(findViewById(R.id.coordinatorLayout),
                    this.getString(R.string.internet_error), Snackbar.LENGTH_LONG).show();
            checkLogInPreferences();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // analytics
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // analytics
        GoogleAnalytics.getInstance(this).reportActivityStop(this);

    }

    @Override
    public void onPause() {
        super.onPause();
    }


    private void startDownload() {
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra(ACTIVITY_NAME, TAG);
        startService(intent);
    }

    private void registerReceiver() {

        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MESSAGE_PROGRESS_SPLASH);
        bManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(MESSAGE_PROGRESS_SPLASH)) {

                Download download = intent.getParcelableExtra("download");

                if (download.getProgress() == 100) {

                    stopService(new Intent(context, DownloadService.class));
                    bManager.unregisterReceiver(broadcastReceiver);
                    checkLogInPreferences();

                } else {

                    //mProgressText.setText(String.format("Downloaded ", download.getProgress() + "%"));

                }
            }
        }
    };

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            return false;
        }
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startDownload();

                } else {

                    Snackbar.make(findViewById(R.id.coordinatorLayout), "Permission Denied, Please allow to proceed !", Snackbar.LENGTH_LONG).show();

                }
                break;
        }
    }

    private void checkLogInPreferences() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Preference.getLoginId(SplashActivity.this) != null && !Preference.getLoginId(SplashActivity.this).equals("")) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashActivity.this, SocialLoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                finish();
            }
        }, SPLASH_TIME_OUT); // every 1 second
    }
}
