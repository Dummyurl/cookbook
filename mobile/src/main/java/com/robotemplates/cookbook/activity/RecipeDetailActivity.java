package com.robotemplates.cookbook.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.robotemplates.cookbook.CookbookApplication;
import com.robotemplates.cookbook.R;


public class RecipeDetailActivity extends AppCompatActivity {
    public static final String EXTRA_RECIPE_ID = "recipe_id";


    public static Intent newIntent(Context context, long recipeId) {
        Intent intent = new Intent(context, RecipeDetailActivity.class);

        // extras
        intent.putExtra(EXTRA_RECIPE_ID, recipeId);

        return intent;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        setupActionBar();

        // init analytics tracker
        ((CookbookApplication) getApplication()).getTracker();
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
    }


    @Override
    public void onPause() {
        super.onPause();
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
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // action bar menu behavior
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
        bar.setTitle(null);
    }
}
