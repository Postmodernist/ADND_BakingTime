package com.alexbaryzhikov.bakingtime.repositiory;

import com.alexbaryzhikov.bakingtime.api.RecipeApi;
import com.alexbaryzhikov.bakingtime.datamodel.Recipe;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import retrofit2.Response;

@Singleton
public class Repository {

  @SuppressWarnings("WeakerAccess")
  @Inject
  RecipeApi recipeApi;

  @Inject
  Repository() {
  }

  public Observable<Response<List<Recipe>>> getRecipes() {
    return Observable.fromCallable(recipeApi.getRecipes()::execute);
  }
}
