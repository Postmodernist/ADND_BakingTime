package com.alexbaryzhikov.bakingtime.widget;

import android.support.annotation.NonNull;

class WidgetConfigureItem {

  @NonNull private final String name;
  @NonNull private final String ingredients;

  WidgetConfigureItem(@NonNull String name, @NonNull String ingredients) {
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
