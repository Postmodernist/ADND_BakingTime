package com.alexbaryzhikov.bakingtime.datamodel.view;

import java.util.List;

public final class DetailItem {

  private final String name;
  private final int position;
  private final String ingredients;
  private final List<String> descriptions;

  public DetailItem(String name, int position, String ingredients, List<String> descriptions) {
    this.name = name;
    this.position = position;
    this.ingredients = ingredients;
    this.descriptions = descriptions;
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

  public List<String> getDescriptions() {
    return descriptions;
  }
}
