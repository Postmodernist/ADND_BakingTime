package com.alexbaryzhikov.bakingtime.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.alexbaryzhikov.bakingtime.datamodel.RecipeItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;

public class RecipeViewModel extends ViewModel {

  private List<RecipeItem> recipeData = new ArrayList<>(Arrays.asList(
      new RecipeItem("Nutella Pie"),
      new RecipeItem("Brownies"),
      new RecipeItem("Yellow Cake")));

  public Observable<List<RecipeItem>> getRecipeData() {
    return Observable.just(recipeData);
  }
}
