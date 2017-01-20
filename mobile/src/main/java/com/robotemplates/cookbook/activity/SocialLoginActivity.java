package com.robotemplates.cookbook.activity;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.robotemplates.cookbook.CookbookApplication;
import com.robotemplates.cookbook.R;
import com.robotemplates.cookbook.interfaces.Constant;
import com.robotemplates.cookbook.modal.Download;
import com.robotemplates.cookbook.preferences.Preference;
import com.robotemplates.cookbook.service.DownloadService;
import com.robotemplates.cookbook.utility.NetworkUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class SocialLoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, Constant {

    private static final String TAG = SocialLoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private CallbackManager callbackManager;

    private LocalBroadcastManager bManager;

    private static SocialLoginActivity mActivity;

    public static SocialLoginActivity getInstance() {
        return mActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLogInPreferences(this);
        //Log.e("Key", "Hash??" + printHashKey(this));
        setContentView(R.layout.activity_social_login);
        mActivity = SocialLoginActivity.this;
        // init analytics tracker
        ((CookbookApplication) getApplication()).getTracker();
        //setupActionBar();
        setupUIButtons();
        setupGoogleConfiguration();
        setupFacebookConfiguration();
        registerReceiver();
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

    // Setup Facebook Configuration
    private void setupFacebookConfiguration() {
        // [START FACEBOOK]
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {

                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        // Get facebook data from login
                                        Bundle bFacebookData = getFacebookData(object);

                                        Preference.setLogInPreference(SocialLoginActivity.this, TAG_FACEBOOK,
                                                bFacebookData.get("idFacebook").toString(),
                                                bFacebookData.get("first_name").toString() + " " +
                                                        bFacebookData.get("last_name").toString(),
                                                bFacebookData.get("email").toString(),
                                                bFacebookData.get("profile_pic").toString());

                                        if (checkPermission()) {
                                            startDownload();
                                        } else {
                                            requestPermission();
                                        }
                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        hideProgressDialog();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.e("FacebookException", "onError??" + exception.getMessage());
                        hideProgressDialog();
                    }
                });

        // [END FACEBOOK]

    }

    // Setup Google Configuration
    private void setupGoogleConfiguration() {
        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]
    }

    // Setup UI Buttons
    private void setupUIButtons() {
        Button fbLoginButton = (Button) findViewById(R.id.fbLoginButton);
        fbLoginButton.setOnClickListener(view -> {
            loginByFacebook();
        });

        Button googleLoginButton = (Button) findViewById(R.id.googleLoginButton);
        googleLoginButton.setOnClickListener(view -> {
            loginByGoogle();
        });
    }


    /**
     * Generates the hash key used for Facebook console to register app. It can also be used for other sdks) Method copied from: https://developers.facebook.com/docs/android/getting-started/
     */
    public static String printHashKey(Context ctx) {
        // Add code to print out the key hash
        try {
            PackageInfo info = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
        } catch (PackageManager.NameNotFoundException e) {
            return "SHA-1 generation: the key count not be generated: NameNotFoundException thrown";
        } catch (NoSuchAlgorithmException e) {
            return "SHA-1 generation: the key count not be generated: NoSuchAlgorithmException thrown";
        }

        return "SHA-1 generation: epic failed";

    }

    private void checkLogInPreferences(Context ctx) {
        hideProgressDialog();
        if (Preference.getLoginId(ctx) != null && !Preference.getLoginId(ctx).equals("")) {
            Intent intent = new Intent(ctx, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void startDownload() {
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra(ACTIVITY_NAME, TAG);
        startService(intent);
    }

    private void loginByFacebook() {
        if (NetworkUtility.isOnline(this)) {
            showProgressDialog();
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        } else {
            Snackbar.make(findViewById(R.id.coordinatorLayout),
                    this.getString(R.string.internet_error), Snackbar.LENGTH_LONG).show();
        }

    }

    private void loginByGoogle() {
        if (NetworkUtility.isOnline(this)) {
            signIn();
        } else {
            Snackbar.make(findViewById(R.id.coordinatorLayout),
                    this.getString(R.string.internet_error), Snackbar.LENGTH_LONG).show();
        }

    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayUseLogoEnabled(true);
        bar.setDisplayShowTitleEnabled(true);
        bar.setDisplayShowHomeEnabled(false);
        bar.setDisplayHomeAsUpEnabled(false);
        bar.setHomeButtonEnabled(false);
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        // Facebook CallBack
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //Log.e("Name", "" + acct.getDisplayName());
            //Log.e("id", "" + acct.getId());
            //Log.e("email", "" + acct.getEmail());
            //Log.e("photo", "" + acct.getPhotoUrl().toString());

            String photoUrl = "";
            if(acct.getPhotoUrl() != null){
                photoUrl = acct.getPhotoUrl().toString();
            }
            Preference.setLogInPreference(SocialLoginActivity.this, TAG_GOOGLE,
                    acct.getId(),
                    acct.getDisplayName(),
                    acct.getEmail(),
                    photoUrl);

            if (checkPermission()) {
                startDownload();
            } else {
                requestPermission();
            }

        } else {
            hideProgressDialog();
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        showProgressDialog();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]


    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]

                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        hideProgressDialog();
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private Bundle getFacebookData(JSONObject object) {

        Bundle bundle = null;
        try {
            bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));


        } catch (JSONException e) {
            Log.d(TAG, "Error parsing JSON");
        }

        return bundle;
    }


    private void registerReceiver() {

        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MESSAGE_PROGRESS_LOGIN);
        bManager.registerReceiver(broadcastReceiver, intentFilter);

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(MESSAGE_PROGRESS_LOGIN)) {

                Download download = intent.getParcelableExtra("download");

                if (download.getProgress() == 100) {

                    stopService(new Intent(context, DownloadService.class));
                    bManager.unregisterReceiver(broadcastReceiver);
                    checkLogInPreferences(SocialLoginActivity.this);

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
}
