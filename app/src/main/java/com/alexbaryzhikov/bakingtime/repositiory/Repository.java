package com.alexbaryzhikov.bakingtime.repositiory;

import com.alexbaryzhikov.bakingtime.datamodel.RecipeItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

@Singleton
public class Repository {

  private List<RecipeItem> recipeData = new ArrayList<>(Arrays.asList(
      new RecipeItem("Nutella Pie"),
      new RecipeItem("Brownies"),
      new RecipeItem("Yellow Cake")));

  @Inject
  Repository() {
  }

  public Observable<List<RecipeItem>> getRecipes() {
    return Observable.just(recipeData)
        .delay(3, TimeUnit.SECONDS);
  }
}
