package com.alexbaryzhikov.bakingtime.datamodel.view;

import com.alexbaryzhikov.bakingtime.datamodel.response.Ingredient;

import java.util.List;

public class RecipeItem {

  private final String name;
  private final List<Ingredient> ingredients;
  private String ingredientsStr;

  public RecipeItem(String name, List<Ingredient> ingredients) {
    this.name = name;
    this.ingredients = ingredients;
  }

  public String getName() {
    return name;
  }

  public List<Ingredient> getIngredients() {
    return ingredients;
  }

  public String getIngredientsStr() {
    return ingredientsStr;
  }

  public void setIngredientsStr(String ingredientsStr) {
    this.ingredientsStr = ingredientsStr;
  }
}
