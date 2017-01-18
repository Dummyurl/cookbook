package com.robotemplates.cookbook.interfaces;


import com.robotemplates.cookbook.pojo.CookbookDataPOJO;
import com.robotemplates.cookbook.pojo.Favourite;
import com.robotemplates.cookbook.pojo.Viewer;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

@SuppressWarnings("all")
public interface RestClient {

    @GET("park_io/man/cookbook.db")
    @Streaming
    Call<ResponseBody> downloadFile();

    @GET("alldata.php")
    @Streaming
    Call<CookbookDataPOJO> getDataRequest(@Query("userId") String userId);

    @GET("get_fav.php")
    Call<Favourite> setFavouriteRequest(@Query("userId") String userId, @Query("RecipeId") String recipeId);

    @GET("get_viewer.php")
    Call<Viewer> setViewerRequest(@Query("userid") String userId, @Query("recipe_id") String recipeId);
}
