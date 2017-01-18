package com.robotemplates.cookbook.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.robotemplates.cookbook.CookbookApplication;
import com.robotemplates.cookbook.activity.MainActivity;

public class InternetConnector_Receiver extends BroadcastReceiver {
    public InternetConnector_Receiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            boolean isVisible = CookbookApplication.isActivityVisible();// Check if
            // activity
            // is
            // visible
            // or not
            Log.i("Activity is Visible ", "Is activity visible : " + isVisible);

            // If it is visible then trigger the task else do nothing
            if (isVisible == true) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager
                        .getActiveNetworkInfo();

                // Check internet connection and accrding to state change the
                // text of activity by calling method
                if (networkInfo != null && networkInfo.isConnected()) {

                    new MainActivity().changeTextStatus(context, true);
                    //new SplashActivity().changeTextStatus(context, true);

                } else {

                    new MainActivity().changeTextStatus(context, false);
                    //new SplashActivity().changeTextStatus(context, false);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
