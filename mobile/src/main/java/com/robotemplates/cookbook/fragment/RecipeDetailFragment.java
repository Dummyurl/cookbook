package com.robotemplates.cookbook.fragment;

import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.robotemplates.cookbook.CookbookApplication;
import com.robotemplates.cookbook.CookbookConfig;
import com.robotemplates.cookbook.R;
import com.robotemplates.cookbook.activity.RecipeDetailActivity;
import com.robotemplates.cookbook.database.DatabaseCallListener;
import com.robotemplates.cookbook.database.DatabaseCallManager;
import com.robotemplates.cookbook.database.DatabaseCallTask;
import com.robotemplates.cookbook.database.dao.RecipeDAO;
import com.robotemplates.cookbook.database.data.Data;
import com.robotemplates.cookbook.database.model.IngredientModel;
import com.robotemplates.cookbook.database.model.RecipeModel;
import com.robotemplates.cookbook.database.query.IngredientReadByRecipeQuery;
import com.robotemplates.cookbook.database.query.Query;
import com.robotemplates.cookbook.database.query.RecipeReadAllQuery;
import com.robotemplates.cookbook.database.query.RecipeReadQuery;
import com.robotemplates.cookbook.dialog.AboutDialogFragment;
import com.robotemplates.cookbook.dialog.ServingsDialogFragment;
import com.robotemplates.cookbook.listener.AnimateImageLoadingListener;
import com.robotemplates.cookbook.preferences.Preference;
import com.robotemplates.cookbook.utility.ImageLoaderUtility;
import com.robotemplates.cookbook.utility.Logcat;
import com.robotemplates.cookbook.utility.NetworkUtility;
import com.robotemplates.cookbook.utility.PermissionUtility;
import com.robotemplates.cookbook.utility.ResourcesUtility;
import com.robotemplates.cookbook.view.ObservableStickyScrollView;
import com.robotemplates.cookbook.view.StatefulLayout;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.List;


public class RecipeDetailFragment extends TaskFragment implements DatabaseCallListener, ServingsDialogFragment.ServingsDialogListener {
    private static final String DIALOG_ABOUT = "about";
    private static final String DIALOG_SERVINGS = "servings";
    private static final float CAL_TO_JOULE = 4.184F;

    private View mRootView;
    private StatefulLayout mStatefulLayout;
    private DatabaseCallManager mDatabaseCallManager = new DatabaseCallManager();
    private ImageLoader mImageLoader = ImageLoader.getInstance();
    private DisplayImageOptions mDisplayImageOptions;
    private ImageLoadingListener mImageLoadingListener;
    private long mRecipeId;
    private RecipeModel mRecipe;
    private List<IngredientModel> mIngredientList;
    private boolean[] mIngredientCheckArray;
    private int mRecalculatedServings;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        // handle intent extras
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            handleExtras(extras);
        }

        // image caching options
        setupImageLoader();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        return mRootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // setup stateful layout
        setupStatefulLayout(savedInstanceState);

        // load data
        if (mRecipe == null || mIngredientList == null) loadData();

        // check permissions
        PermissionUtility.checkPermissionWriteExternalStorage(this);
    }


    @Override
    public void onStart() {
        super.onStart();
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
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRootView = null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // cancel async tasks
        mDatabaseCallManager.cancelAllTasks();
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save current instance state
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);

        // stateful layout state
        if (mStatefulLayout != null) mStatefulLayout.saveInstanceState(outState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // action bar menu
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_recipe_detail, menu);

        if (mRecipe != null) {
            if (mRecipe.getLink() == null || mRecipe.getLink().trim().equals("")) {
                MenuItem linkMenuItem = menu.findItem(R.id.menu_fragment_recipe_detail_link);
                linkMenuItem.setVisible(false);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)

    {
        // action bar menu behavior
        switch (item.getItemId()) {
            case R.id.menu_fragment_recipe_detail_share:
                if (mRecipe != null && mIngredientList != null) {
                    startShareActivity(getString(R.string.fragment_recipe_detail_share_subject), getRecipeText());
                }
                return true;

            case R.id.menu_fragment_recipe_detail_shopping_list:
                if (mRecipe != null && mIngredientList != null) {
                    startShareActivity(getString(R.string.fragment_recipe_detail_shopping_list_subject), getShoppingListText());
                }
                return true;

            case R.id.menu_fragment_recipe_detail_link:
                if (mRecipe != null && !mRecipe.getLink().trim().equals("")) {
                    startWebActivity(mRecipe.getLink());
                }
                return true;

            case R.id.menu_rate:
                startWebActivity(getString(R.string.app_store_uri, CookbookApplication.getContext().getPackageName()));
                return true;

            case R.id.menu_about:
                showAboutDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionUtility.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE: {
                // if request is cancelled, the result arrays are empty
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            // permission granted
                            String permission = permissions[i];
                            if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                synchronized (this) {
                                    ImageLoader.getInstance().destroy();
                                    ImageLoaderUtility.init(getActivity().getApplicationContext());
                                }
                            }
                        } else {
                            // permission denied
                        }
                    }
                } else {
                    // all permissions denied
                }
                break;
            }
        }
    }


    @Override
    public void onServingsDialogPositiveClick(final DialogFragment dialog, final int servings) {
        runTaskCallback(new Runnable() {
            @Override
            public void run() {
                mRecalculatedServings = servings;
                bindDataIngredients();
            }
        });
    }


    @Override
    public void onDatabaseCallRespond(final DatabaseCallTask task, final Data<?> data) {
        runTaskCallback(new Runnable() {
            public void run() {
                if (mRootView == null) return; // view was destroyed

                if (task.getQuery().getClass().equals(RecipeReadQuery.class)) {
                    Logcat.d("RecipeReadQuery");

                    // get data
                    Data<RecipeModel> recipeReadData = (Data<RecipeModel>) data;
                    mRecipe = recipeReadData.getDataObject();
                    mRecalculatedServings = mRecipe.getServings();
                } else if (task.getQuery().getClass().equals(IngredientReadByRecipeQuery.class)) {
                    Logcat.d("IngredientReadByRecipeQuery");

                    // get data
                    Data<List<IngredientModel>> ingredientReadByRecipeData = (Data<List<IngredientModel>>) data;
                    mIngredientList = ingredientReadByRecipeData.getDataObject();
                    mIngredientCheckArray = new boolean[mIngredientList.size()];
                }

                // hide progress and bind data
                if (mRecipe != null && mIngredientList != null) mStatefulLayout.showContent();
                else if (mRecipe == null && mIngredientList == null) mStatefulLayout.showEmpty();

                // finish query
                mDatabaseCallManager.finishTask(task);
            }
        });
    }


    @Override
    public void onDatabaseCallFail(final DatabaseCallTask task, final Exception exception) {
        runTaskCallback(new Runnable() {
            public void run() {
                if (mRootView == null) return; // view was destroyed

                if (task.getQuery().getClass().equals(RecipeReadQuery.class)) {
                    Logcat.d("RecipeReadQuery / exception " + exception.getClass().getSimpleName() + " / " + exception.getMessage());
                } else if (task.getQuery().getClass().equals(IngredientReadByRecipeQuery.class)) {
                    Logcat.d("IngredientReadByRecipeQuery / exception " + exception.getClass().getSimpleName() + " / " + exception.getMessage());
                }

                // hide progress
                if (mRecipe != null && mIngredientList != null) mStatefulLayout.showContent();
                else mStatefulLayout.showEmpty();

                // handle fail
                handleFail();

                // finish query
                mDatabaseCallManager.finishTask(task);
            }
        });
    }


    private void handleFail() {
        Toast.makeText(getActivity(), R.string.global_database_fail_toast, Toast.LENGTH_LONG).show();
    }


    private void handleExtras(Bundle extras) {
        mRecipeId = extras.getLong(RecipeDetailActivity.EXTRA_RECIPE_ID);
    }


    private void loadData() {
        // load recipe
        if (!mDatabaseCallManager.hasRunningTask(RecipeReadQuery.class)) {
            // show progress
            mStatefulLayout.showProgress();

            // run async task
            Query query = new RecipeReadQuery(mRecipeId);
            mDatabaseCallManager.executeTask(query, this);

        }

        // load ingredients
        if (!mDatabaseCallManager.hasRunningTask(IngredientReadByRecipeQuery.class)) {
            // show progress
            mStatefulLayout.showProgress();

            // run async task
            Query query = new IngredientReadByRecipeQuery(mRecipeId);
            mDatabaseCallManager.executeTask(query, this);
        }
    }


    private void showFloatingActionButton(boolean visible) {
        final FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        if (visible) {
            fab.animate()
                    .scaleX(1)
                    .scaleY(1)
                    .setDuration(300)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            fab.show(false);
                            fab.setVisibility(View.VISIBLE);
                            fab.setEnabled(false);
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            fab.setEnabled(true);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    });
        } else {
            fab.animate()
                    .alpha(0F)
                    .setDuration(50)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            fab.setEnabled(false);
                        }


                        @Override
                        public void onAnimationEnd(Animator animator) {
                            fab.setScaleX(0);
                            fab.setScaleY(0);
                            fab.setAlpha(1F);
                            fab.hide(false);
                            fab.setVisibility(View.GONE);
                            fab.setEnabled(true);
                        }


                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }


                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    });
        }
    }


    private void bindData() {
        bindDataToolbar();
        bindDataIntro();
        bindDataBanner();
        bindDataIngredients();
        bindDataInstruction();
        bindDataGap();
        getActivity().invalidateOptionsMenu();
    }


    private void bindDataToolbar() {
        // reference
        final ObservableStickyScrollView observableStickyScrollView = (ObservableStickyScrollView) mRootView.findViewById(R.id.container_content);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        final View panelTopView = mRootView.findViewById(R.id.toolbar_image_panel_top);
        final View panelBottomView = mRootView.findViewById(R.id.toolbar_image_panel_bottom);
        final ImageView imageView = (ImageView) mRootView.findViewById(R.id.toolbar_image_imageview);
        final TextView titleTextView = (TextView) mRootView.findViewById(R.id.toolbar_image_title);

        // Set Viewers
        mRecipe.setViewer(true);
        try {
            RecipeDAO.getDao().update(mRecipe);
            RecipeDAO.getDao().refresh(mRecipe);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // title
        titleTextView.setText(mRecipe.getName());
        // image
        mImageLoader.displayImage(mRecipe.getImage(), imageView, mDisplayImageOptions, mImageLoadingListener);

        // scroll view
        observableStickyScrollView.setOnScrollViewListener(new ObservableStickyScrollView.ScrollViewListener() {
            private final int THRESHOLD = RecipeDetailFragment.this.getResources().getDimensionPixelSize(R.dimen.toolbar_image_gap_height);
            private final int PADDING_LEFT = RecipeDetailFragment.this.getResources().getDimensionPixelSize(R.dimen.toolbar_image_title_padding_right);
            private final int PADDING_BOTTOM = RecipeDetailFragment.this.getResources().getDimensionPixelSize(R.dimen.global_spacing_xs);
            private final float SHADOW_RADIUS = 16;

            private int mPreviousY = 0;
            private ColorDrawable mTopColorDrawable = new ColorDrawable();
            private ColorDrawable mBottomColorDrawable = new ColorDrawable();


            @SuppressWarnings("deprecation")
            @Override
            public void onScrollChanged(ObservableStickyScrollView scrollView, int x, int y, int oldx, int oldy) {
                // floating action button
                if (y > THRESHOLD) {
                    if (floatingActionButton.getVisibility() == View.GONE && floatingActionButton.isEnabled()) {
                        showFloatingActionButton(true);
                    }
                } else {
                    if (floatingActionButton.getVisibility() == View.VISIBLE && floatingActionButton.isEnabled()) {
                        showFloatingActionButton(false);
                    }
                }

                // do not calculate if header is hidden
                if (y > THRESHOLD && mPreviousY > THRESHOLD) return;

                // calculate panel alpha
                int alpha = (int) (y * (255F / (float) THRESHOLD));
                if (alpha > 255) alpha = 255;

                // set color drawables
                mTopColorDrawable.setColor(ResourcesUtility.getValueOfAttribute(getActivity(), R.attr.colorPrimary));
                mTopColorDrawable.setAlpha(alpha);
                mBottomColorDrawable.setColor(ResourcesUtility.getValueOfAttribute(getActivity(), R.attr.colorPrimary));
                mBottomColorDrawable.setAlpha(alpha);

                // set panel background
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    panelTopView.setBackground(mTopColorDrawable);
                    panelBottomView.setBackground(mBottomColorDrawable);
                } else {
                    panelTopView.setBackgroundDrawable(mTopColorDrawable);
                    panelBottomView.setBackgroundDrawable(mBottomColorDrawable);
                }

                // calculate image translation
                float translation = y / 2;

                // set image translation
                imageView.setTranslationY(translation);

                // calculate title padding
                int paddingLeft = (int) (y * (float) PADDING_LEFT / (float) THRESHOLD);
                if (paddingLeft > PADDING_LEFT) paddingLeft = PADDING_LEFT;

                int paddingRight = PADDING_LEFT - paddingLeft;

                int paddingBottom = (int) ((THRESHOLD - y) * (float) PADDING_BOTTOM / (float) THRESHOLD);
                if (paddingBottom < 0) paddingBottom = 0;

                // set title padding
                titleTextView.setPadding(paddingLeft, 0, paddingRight, paddingBottom);

                // calculate title shadow
                float radius = ((THRESHOLD - y) * SHADOW_RADIUS / (float) THRESHOLD);

                // set title shadow
                titleTextView.setShadowLayer(radius, 0F, 0F, ContextCompat.getColor(getActivity(), android.R.color.black));

                // previous y
                mPreviousY = y;
            }
        });


        // invoke scroll event because of orientation change toolbar refresh
        observableStickyScrollView.post(new Runnable() {
            @Override
            public void run() {
                observableStickyScrollView.scrollTo(0, observableStickyScrollView.getScrollY() - 1);
            }
        });

        // floating action button
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) floatingActionButton.getLayoutParams();
        params.topMargin = getResources().getDimensionPixelSize(R.dimen.toolbar_image_collapsed_height) - getResources().getDimensionPixelSize(R.dimen.fab_mini_size) / 2;
        floatingActionButton.setLayoutParams(params);
        floatingActionButton.setImageDrawable(mRecipe.isFavorite() ? ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_favorite_checked) : ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_favorite_unchecked));
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mRecipe.setFavorite(!mRecipe.isFavorite());
                    RecipeDAO.update(mRecipe);
                    floatingActionButton.setImageDrawable(mRecipe.isFavorite() ? ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_favorite_checked) : ContextCompat.getDrawable(getActivity(), R.drawable.ic_menu_favorite_unchecked));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void bindDataIntro() {
        // reference
        TextView introTextView = (TextView) mRootView.findViewById(R.id.fragment_recipe_detail_intro_text);
        View dividerView = mRootView.findViewById(R.id.fragment_recipe_detail_intro_divider);
        TextView timeTextView = (TextView) mRootView.findViewById(R.id.fragment_recipe_detail_intro_time);
        TextView servingsTextView = (TextView) mRootView.findViewById(R.id.fragment_recipe_detail_intro_servings);
        TextView caloriesTextView = (TextView) mRootView.findViewById(R.id.fragment_recipe_detail_intro_calories);

        // text
        introTextView.setText(mRecipe.getIntro());
        timeTextView.setText(getString(R.string.fragment_recipe_detail_intro_time, mRecipe.getTime()));
        servingsTextView.setText(MessageFormat.format(getString(R.string.fragment_recipe_detail_intro_servings), mRecipe.getServings()));
        caloriesTextView.setText(getString(R.string.fragment_recipe_detail_intro_calories, mRecipe.getCalories()));

        // visibility
        if (mRecipe.getIntro() != null && !mRecipe.getIntro().trim().equals("")) {
            introTextView.setVisibility(View.VISIBLE);
            dividerView.setVisibility(View.VISIBLE);
        } else {
            introTextView.setVisibility(View.GONE);
            dividerView.setVisibility(View.GONE);
        }

        if (mRecipe.getTime() > 0) {
            timeTextView.setVisibility(View.VISIBLE);
        } else {
            timeTextView.setVisibility(View.GONE);
        }

        if (mRecipe.getServings() > 0) {
            servingsTextView.setVisibility(View.VISIBLE);
        } else {
            servingsTextView.setVisibility(View.GONE);
        }

        if (mRecipe.getCalories() > 0) {
            caloriesTextView.setVisibility(View.VISIBLE);
        } else {
            caloriesTextView.setVisibility(View.GONE);
        }

        // time
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecipe.getTime() > 0) {
                    startTimerActivity(mRecipe.getTime() * 60);
                }
            }
        });

        // servings
        servingsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showServingsDialog(mRecalculatedServings);
            }
        });

        // calories
        caloriesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecipe.getCalories() > 0) {
                    int joules = (int) (mRecipe.getCalories() * CAL_TO_JOULE);
                    StringBuilder builder = new StringBuilder();
                    builder.append(getString(R.string.fragment_recipe_detail_intro_energy));
                    builder.append("\n");
                    builder.append(getString(R.string.fragment_recipe_detail_intro_calories, mRecipe.getCalories()));
                    builder.append(" = ");
                    builder.append(getString(R.string.fragment_recipe_detail_intro_joules, joules));
                    Toast.makeText(getActivity(), builder.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void bindDataBanner() {
        // reference
        final ViewGroup bannerViewGroup = (ViewGroup) mRootView.findViewById(R.id.fragment_recipe_detail_banner);

        if (CookbookConfig.ADMOB_UNIT_ID_RECIPE_DETAIL != null && !CookbookConfig.ADMOB_UNIT_ID_RECIPE_DETAIL.equals("") && NetworkUtility.isOnline(getActivity())) {
            // layout params
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;

            // create ad view
            AdView adView = new AdView(getActivity());
            adView.setId(R.id.adview);
            adView.setLayoutParams(params);
            adView.setAdSize(AdSize.LARGE_BANNER);
            adView.setAdUnitId(CookbookConfig.ADMOB_UNIT_ID_RECIPE_DETAIL);

            // add to layout
            bannerViewGroup.removeView(getActivity().findViewById(R.id.adview));
            bannerViewGroup.addView(adView);
            bannerViewGroup.setVisibility(View.VISIBLE);

            // call ad request
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(CookbookConfig.ADMOB_TEST_DEVICE_ID)
                    .build();
            adView.loadAd(adRequest);
        } else {
            bannerViewGroup.setVisibility(View.GONE);
        }
    }


    private void bindDataIngredients() {
        // reference
        final TextView titleTextView = (TextView) mRootView.findViewById(R.id.fragment_recipe_detail_ingredients_title);
        final ViewGroup containerViewGroup = (ViewGroup) mRootView.findViewById(R.id.fragment_recipe_detail_ingredients_container);
        final Button clearButton = (Button) mRootView.findViewById(R.id.fragment_recipe_detail_ingredients_clear);
        final Button recalculateButton = (Button) mRootView.findViewById(R.id.fragment_recipe_detail_ingredients_recalculate);

        // title
        if (mRecalculatedServings == mRecipe.getServings()) {
            titleTextView.setText(R.string.fragment_recipe_detail_ingredients_title);
        } else {
            titleTextView.setText(getString(R.string.fragment_recipe_detail_ingredients_title_recalculated, mRecalculatedServings));
        }

        // items
        float coeficient = (float) mRecalculatedServings / (float) mRecipe.getServings();
        containerViewGroup.removeAllViews();
        for (int i = 0; i < mIngredientList.size(); i++) {
            IngredientModel ingredient = mIngredientList.get(i);
            final int index = i;

            boolean quantityAvailable = ingredient.getQuantity() > 0;
            boolean unitAvailable = ingredient.getUnit() != null && !ingredient.getUnit().trim().equals("");

            StringBuilder builder = new StringBuilder();
            if (quantityAvailable) {
                builder.append(new DecimalFormat("0.##").format(ingredient.getQuantity() * coeficient));
                if (unitAvailable) {
                    builder.append(" ");
                } else {
                    builder.append("   ");
                }
            }
            if (unitAvailable) {
                builder.append(ingredient.getUnit());
                builder.append("   ");
            }
            builder.append(ingredient.getName());

            CheckBox item = (CheckBox) getLayoutInflater(null).inflate(R.layout.fragment_recipe_detail_content_ingredients_item, containerViewGroup, false);
            item.setText(builder.toString());
            item.setChecked(mIngredientCheckArray[i]);
            item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mIngredientCheckArray[index] = isChecked;
                }
            });
            containerViewGroup.addView(item);
        }

        // clear
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < containerViewGroup.getChildCount(); i++) {
                    CheckBox item = (CheckBox) containerViewGroup.getChildAt(i);
                    item.setChecked(false);
                }
            }
        });

        // recalculate
        recalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showServingsDialog(mRecalculatedServings);
            }
        });
    }


    private void bindDataInstruction() {
        // reference
        TextView instructionTextView = (TextView) mRootView.findViewById(R.id.fragment_recipe_detail_instruction_text);

        // content
        instructionTextView.setText(mRecipe.getInstruction());
    }


    private void bindDataGap() {
        // reference
        final View gapView = mRootView.findViewById(R.id.fragment_recipe_detail_gap);
        final CardView ingredientsCardView = (CardView) mRootView.findViewById(R.id.fragment_recipe_detail_ingredients);

        // add gap in scroll view so favorite floating action button can be shown on tablet
        if (gapView != null) {
            ingredientsCardView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    // cardview height
                    int cardHeight = ingredientsCardView.getHeight();

                    // toolbar height
                    int toolbarHeight = getResources().getDimensionPixelSize(R.dimen.toolbar_image_collapsed_height);

                    // screen height
                    Display display = getActivity().getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int screenHeight = size.y;

                    // calculate gap height
                    int gapHeight = screenHeight - cardHeight - toolbarHeight;
                    if (gapHeight > 0) {
                        ViewGroup.LayoutParams params = gapView.getLayoutParams();
                        params.height = gapHeight;
                        gapView.setLayoutParams(params);
                    }

                    // remove layout listener
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ingredientsCardView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        ingredientsCardView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });
        }
    }


    private void setupStatefulLayout(Bundle savedInstanceState) {
        // reference
        mStatefulLayout = (StatefulLayout) mRootView;

        // state change listener
        mStatefulLayout.setOnStateChangeListener(new StatefulLayout.OnStateChangeListener() {
            @Override
            public void onStateChange(View v, StatefulLayout.State state) {
                Logcat.d("" + (state == null ? "null" : state.toString()));

                // bind data
                if (state == StatefulLayout.State.CONTENT) {
                    if (mRecipe != null) bindData();
                }

                // floating action button
                if (state != StatefulLayout.State.CONTENT) {
                    showFloatingActionButton(false);
                }

                // toolbar background and visibility
                Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

                if (state == StatefulLayout.State.CONTENT) {
                    toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.transparent));
                } else {
                    toolbar.setBackgroundColor(ResourcesUtility.getValueOfAttribute(getActivity(), R.attr.colorPrimary));
                }

                if (state == StatefulLayout.State.PROGRESS) {
                    toolbar.setVisibility(View.GONE);
                } else {
                    toolbar.setVisibility(View.VISIBLE);
                }
            }
        });

        // restore state
        mStatefulLayout.restoreInstanceState(savedInstanceState);
    }


    private void setupImageLoader() {
        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(android.R.color.transparent)
                .showImageForEmptyUri(R.drawable.placeholder_photo)
                .showImageOnFail(R.drawable.placeholder_photo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new SimpleBitmapDisplayer())
                .build();
        mImageLoadingListener = new AnimateImageLoadingListener();
    }


    private void showAboutDialog() {
        // create and show the dialog
        DialogFragment newFragment = AboutDialogFragment.newInstance();
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), DIALOG_ABOUT);
    }


    private void showServingsDialog(int servings) {
        // create and show the dialog
        DialogFragment newFragment = ServingsDialogFragment.newInstance(servings);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), DIALOG_SERVINGS);
    }


    private String getRecipeText() {
        StringBuilder builder = new StringBuilder();
        builder.append(mRecipe.getName());
        builder.append("\n\n");
        if (mRecipe.getIntro() != null && !mRecipe.getIntro().trim().equals("")) {
            builder.append(mRecipe.getIntro());
            builder.append("\n\n");
        }
        builder.append(getShoppingListText());
        builder.append("\n");
        builder.append(mRecipe.getInstruction());
        builder.append("\n\n");
        if (mRecipe.getLink() != null && !mRecipe.getLink().trim().equals("")) {
            builder.append(mRecipe.getLink());
        }
        return builder.toString();
    }


    private String getShoppingListText() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mIngredientList.size(); i++) {
            IngredientModel ingredient = mIngredientList.get(i);
            boolean quantityAvailable = ingredient.getQuantity() > 0;
            boolean unitAvailable = ingredient.getUnit() != null && !ingredient.getUnit().trim().equals("");
            if (quantityAvailable) {
                builder.append(new DecimalFormat("0.##").format(ingredient.getQuantity()));
                if (unitAvailable) {
                    builder.append(" ");
                } else {
                    builder.append("   ");
                }
            }
            if (unitAvailable) {
                builder.append(ingredient.getUnit());
                builder.append("   ");
            }
            builder.append(ingredient.getName());
            builder.append("\n");
        }
        return builder.toString();
    }


    private void startShareActivity(String subject, String text) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(android.content.Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(intent, getResources().getText(R.string.app_name)));
        } catch (android.content.ActivityNotFoundException e) {
            // can't start activity
        }
    }


    private void startWebActivity(String url) {
        try {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            // can't start activity
        }
    }


    private void startTimerActivity(int length) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Intent intent = new Intent(AlarmClock.ACTION_SET_TIMER);
                intent.putExtra(AlarmClock.EXTRA_LENGTH, length);
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                // can't start activity
            }
        }
    }
}
