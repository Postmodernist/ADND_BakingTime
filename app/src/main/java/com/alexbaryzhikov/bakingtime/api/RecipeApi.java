package com.alexbaryzhikov.bakingtime.api;

import com.alexbaryzhikov.bakingtime.datamodel.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipeApi {
  @GET("android-baking-app-json")
  Call<List<Recipe>> getRecipes();
}
