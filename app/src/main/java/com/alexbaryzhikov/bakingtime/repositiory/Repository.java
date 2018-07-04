package com.alexbaryzhikov.bakingtime.repositiory;

import com.alexbaryzhikov.bakingtime.api.RecipeApi;
import com.alexbaryzhikov.bakingtime.datamodel.response.Recipe;
import com.alexbaryzhikov.bakingtime.di.scopes.MainActivityScope;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import retrofit2.adapter.rxjava2.Result;

public class Repository {

  private final RecipeApi recipeApi;

  public Repository(RecipeApi recipeApi) {
    this.recipeApi = recipeApi;
  }

  public Observable<Result<List<Recipe>>> getRecipes() {
    return recipeApi.getRecipes();
  }
}
