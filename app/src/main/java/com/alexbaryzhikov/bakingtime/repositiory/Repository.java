package com.alexbaryzhikov.bakingtime.repositiory;

import com.alexbaryzhikov.bakingtime.api.RecipeApi;
import com.alexbaryzhikov.bakingtime.datamodel.response.Recipe;
import com.alexbaryzhikov.bakingtime.di.scopes.MainActivityScope;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import retrofit2.adapter.rxjava2.Result;

@MainActivityScope
public class Repository {

  @SuppressWarnings("WeakerAccess")
  @Inject RecipeApi recipeApi;

  @Inject
  Repository() {
  }

  public Observable<Result<List<Recipe>>> getRecipes() {
    return recipeApi.getRecipes();
  }
}
