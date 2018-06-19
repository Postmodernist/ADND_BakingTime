package com.alexbaryzhikov.bakingtime.api;

import com.alexbaryzhikov.bakingtime.datamodel.response.Recipe;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.GET;

public interface RecipeApi {
  @GET("android-baking-app-json")
  Observable<Result<List<Recipe>>> getRecipes();
}
