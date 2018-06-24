package com.alexbaryzhikov.bakingtime.datamodel.view;

import android.support.annotation.NonNull;

public class BrowseItem {

  @NonNull private final String name;
  @NonNull private final String ingredients;

  public BrowseItem(@NonNull String name, @NonNull String ingredients) {
    this.name = name;
    this.ingredients = ingredients;
  }

  @NonNull
  public String getName() {
    return name;
  }

  @NonNull
  public String getIngredients() {
    return ingredients;
  }
}
