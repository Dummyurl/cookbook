package com.robotemplates.cookbook;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.robotemplates.cookbook.utility.ImageLoaderUtility;


public class CookbookApplication extends Application
{
	private static CookbookApplication sInstance;

	private Tracker mTracker;

	public static Context getContext()
	{
		return sInstance;
	}


	public CookbookApplication()
	{
		sInstance = this;
	}


	@Override
	public void onCreate()
	{
		super.onCreate();
		
		// force AsyncTask to be initialized in the main thread due to the bug:
		// http://stackoverflow.com/questions/4280330/onpostexecute-not-being-called-in-asynctask-handler-runtime-exception
		try
		{
			Class.forName("android.os.AsyncTask");
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		// init image caching
		ImageLoaderUtility.init(getApplicationContext());
		FacebookSdk.sdkInitialize(getApplicationContext());
		AppEventsLogger.activateApp(this);
	}


	public synchronized Tracker getTracker()
	{
		if(mTracker==null)
		{
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			analytics.setDryRun(CookbookConfig.ANALYTICS_TRACKING_ID == null || CookbookConfig.ANALYTICS_TRACKING_ID.equals(""));
			mTracker = analytics.newTracker(R.xml.analytics_app_tracker);
			mTracker.set("&tid", CookbookConfig.ANALYTICS_TRACKING_ID);
		}
		return mTracker;
	}

	// Gloabl declaration of variable to use in whole app

	public static boolean activityVisible; // Variable that will check the
	// current activity state

	public static boolean isActivityVisible() {
		return activityVisible; // return true or false
	}

	public static void activityResumed() {
		activityVisible = true;// this will set true when activity resumed

	}

	public static void activityPaused() {
		activityVisible = false;// this will set false when activity paused

	}
}
