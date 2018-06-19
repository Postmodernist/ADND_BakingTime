package com.alexbaryzhikov.bakingtime.datamodel.view;

import com.alexbaryzhikov.bakingtime.datamodel.response.Ingredient;

import java.util.List;

public class RecipeItem {

  private final String title;
  private final List<Ingredient> ingredients;
  private String ingredientsStr;

  public RecipeItem(String title, List<Ingredient> ingredients) {
    this.title = title;
    this.ingredients = ingredients;
  }

  public String getTitle() {
    return title;
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
