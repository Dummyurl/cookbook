package com.robotemplates.cookbook.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.robotemplates.cookbook.CookbookApplication;
import com.robotemplates.cookbook.R;
import com.robotemplates.cookbook.adapter.DrawerAdapter;
import com.robotemplates.cookbook.database.DatabaseHelper;
import com.robotemplates.cookbook.database.dao.CategoryDAO;
import com.robotemplates.cookbook.database.dao.IngredientDAO;
import com.robotemplates.cookbook.database.dao.RecipeDAO;
import com.robotemplates.cookbook.database.model.CategoryModel;
import com.robotemplates.cookbook.fragment.RecipeListFragment;
import com.robotemplates.cookbook.interfaces.Constant;
import com.robotemplates.cookbook.listener.OnSearchListener;
import com.robotemplates.cookbook.listener.OnSortByFavouriteListener;
import com.robotemplates.cookbook.modal.Download;
import com.robotemplates.cookbook.service.DownloadService;
import com.robotemplates.cookbook.service.SendDataService;
import com.robotemplates.cookbook.utility.NetworkUtility;
import com.robotemplates.cookbook.utility.ResourcesUtility;
import com.robotemplates.cookbook.view.DrawerDividerItemDecoration;
import com.robotemplates.cookbook.view.ScrimInsetsFrameLayout;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements DrawerAdapter.CategoryViewHolder.OnItemClickListener, OnSearchListener, OnSortByFavouriteListener, Constant {
    private static final String TAG = MainActivity.class.getSimpleName();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ScrimInsetsFrameLayout mDrawerScrimInsetsFrameLayout;
    private DrawerAdapter mDrawerAdapter;
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private List<CategoryModel> mCategoryList;
    private Bundle savedInstanceState;
    private LocalBroadcastManager bManager;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.savedInstanceState = savedInstanceState;
        setupActionBar();
        setupRecyclerView();
        setupDrawer(savedInstanceState);

        // init analytics tracker
        ((CookbookApplication) getApplication()).getTracker();

        // At activity startup we manually check the internet status and change
        // the text status
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            changeTextStatus(this, true);
        } else {
            changeTextStatus(this, false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // analytics
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        CookbookApplication.activityResumed();// On Resume notify the Application
    }


    @Override
    public void onPause() {
        super.onPause();
        CookbookApplication.activityPaused();// On Pause notify the Application
    }


    @Override
    public void onStop() {
        super.onStop();

        // analytics
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (NetworkUtility.isOnline(this)) {
            startService(new Intent(this, SendDataService.class));
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // open or close the drawer if home button is pressed
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // action bar menu behavior
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfiguration) {
        super.onConfigurationChanged(newConfiguration);
        mDrawerToggle.onConfigurationChanged(newConfiguration);
    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            finish();
            super.onBackPressed();
        }
    }


    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }


    @Override
    public void onItemClick(View view, int position, long id, int viewType) {
        // position
        int categoryPosition = mDrawerAdapter.getCategoryPosition(position);
        selectDrawerItem(categoryPosition);
    }


    @Override
    public void onSearch(String query) {
        Fragment fragment = RecipeListFragment.newInstance(query);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_drawer_content, fragment).commitAllowingStateLoss();

        mDrawerAdapter.setSelected(mDrawerAdapter.getRecyclerPositionByCategory(0));
        setTitle(getString(R.string.title_search) + ": " + query);
    }

    @Override
    public void onSortByFav(long id, String tag) {
        Fragment fragment = RecipeListFragment.newInstance(id, tag);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_drawer_content, fragment).commitAllowingStateLoss();

        //setTitle(tag);
    }


    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayUseLogoEnabled(false);
        bar.setDisplayShowTitleEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
    }


    private void setupRecyclerView() {
        // reference
        RecyclerView recyclerView = getRecyclerView();

        // set layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        // load categories from database
        mCategoryList = new ArrayList<>();
        loadCategoryList();

        // set adapter
        if (recyclerView.getAdapter() == null) {
            // create adapter
            mDrawerAdapter = new DrawerAdapter(this, mCategoryList, this);
        } else {
            // refill adapter
            mDrawerAdapter.refill(mCategoryList, this);
        }
        recyclerView.setAdapter(mDrawerAdapter);

        // add decoration
        List<Integer> dividerPositions = new ArrayList<>();
        dividerPositions.add(3);
        RecyclerView.ItemDecoration itemDecoration = new DrawerDividerItemDecoration(
                this,
                null,
                dividerPositions,
                getResources().getDimensionPixelSize(R.dimen.global_spacing_xxs));
        recyclerView.addItemDecoration(itemDecoration);
    }


    private void setupDrawer(Bundle savedInstanceState) {
        mTitle = getTitle();
        mDrawerTitle = getTitle();

        // reference
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        mDrawerScrimInsetsFrameLayout = (ScrimInsetsFrameLayout) findViewById(R.id.activity_main_drawer_scrim_layout);

        // set drawer
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.setStatusBarBackgroundColor(ResourcesUtility.getValueOfAttribute(this, R.attr.colorPrimaryDark));
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // show initial fragment
        if (savedInstanceState == null) {
            selectDrawerItem(0);
        }
    }


    private void selectDrawerItem(int position) {
        Fragment fragment = RecipeListFragment.newInstance(mCategoryList.get(position).getId(), "");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_drawer_content, fragment).commitAllowingStateLoss();

        mDrawerAdapter.setSelected(mDrawerAdapter.getRecyclerPositionByCategory(position));
        setTitle(mCategoryList.get(position).getName());
        mDrawerLayout.closeDrawer(mDrawerScrimInsetsFrameLayout);
    }


    private void loadCategoryList() {
        try {
            mCategoryList = CategoryDAO.readAll(-1L, -1L);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        CategoryModel all = new CategoryModel();
        all.setId(RecipeListFragment.CATEGORY_ID_ALL);
        all.setName(getResources().getString(R.string.drawer_category_all));
        all.setImage("drawable://" + R.drawable.ic_category_all);

        CategoryModel favorites = new CategoryModel();
        favorites.setId(RecipeListFragment.CATEGORY_ID_FAVORITES);
        favorites.setName(getResources().getString(R.string.drawer_category_favorites));
        favorites.setImage("drawable://" + R.drawable.ic_category_favorites);

        mCategoryList.add(0, all);
        mCategoryList.add(1, favorites);
    }


    private RecyclerView getRecyclerView() {
        return (RecyclerView) findViewById(R.id.activity_main_drawer_recycler);
    }

    public void changeTextStatus(Context ctx, boolean isConnected) {
        // Change status according to boolean value
        if (isConnected) {
            //Toast.makeText(ctx, "Internet Connected.", Toast.LENGTH_LONG).show();
            //getDataBaseFromServer();

        } else {
            //Toast.makeText(ctx, "Internet Disconnected.", Toast.LENGTH_LONG).show();
            //Snackbar.make(findViewById(R.id.coordinatorLayout), ctx.getString(R.string.internet_error), Snackbar.LENGTH_LONG).show();
        }
    }

    /* Get DataBase From Server For First Time */
    private void getDataBaseFromServer() {
        if (DatabaseHelper.getInstance() != null) {
            if (DatabaseHelper.getInstance().databaseExists()) {
                try {
                    if (CategoryDAO.getDao().isTableExists() && RecipeDAO.getDao().isTableExists() && IngredientDAO.getDao().isTableExists()) {
                        long numRows = CategoryDAO.getDao().countOf();
                        long numRows1 = RecipeDAO.getDao().countOf();
                        long numRows2 = IngredientDAO.getDao().countOf();
                        if (numRows == 0 && numRows1 == 0 && numRows2 == 0) {
                            registerReceiver();
                            if (checkPermission()) {
                                startDownload();
                            } else {
                                requestPermission();
                            }
                        } else {
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startDownload() {
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra(ACTIVITY_NAME, TAG);
        startService(intent);
    }

    private void registerReceiver() {
        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MESSAGE_PROGRESS_MAIN);
        bManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(MESSAGE_PROGRESS_MAIN)) {

                Download download = intent.getParcelableExtra("download");

                if (download.getProgress() == 100) {

                    stopService(new Intent(context, DownloadService.class));
                    bManager.unregisterReceiver(broadcastReceiver);
                    setupRecyclerView();
                    setupDrawer(savedInstanceState);
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
}
