package com.alexbaryzhikov.bakingtime.datamodel.view;

import android.support.annotation.NonNull;

import java.util.List;

public final class DetailItem {

  private final String name;
  private final int position;
  private final String ingredients;
  private final List<String> steps;

  public DetailItem(String name, int position, @NonNull String ingredients, @NonNull List<String> steps) {
    this.name = name;
    this.position = position;
    this.ingredients = ingredients;
    this.steps = steps;
  }

  public String getName() {
    return name;
  }

  public int getPosition() {
    return position;
  }

  public String getIngredients() {
    return ingredients;
  }

  public List<String> getSteps() {
    return steps;
  }
}
