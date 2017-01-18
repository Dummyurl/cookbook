package com.robotemplates.cookbook.content;

import android.content.SearchRecentSuggestionsProvider;


public class RecipeSearchRecentSuggestionsProvider extends SearchRecentSuggestionsProvider
{
	public final static String AUTHORITY = "com.robotemplates.cookbook.content.RecipeSearchRecentSuggestionsProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;


	public RecipeSearchRecentSuggestionsProvider()
	{
		setupSuggestions(AUTHORITY, MODE);
	}
}
