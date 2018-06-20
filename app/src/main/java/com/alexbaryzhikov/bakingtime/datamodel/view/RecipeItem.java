package com.alexbaryzhikov.bakingtime.datamodel.view;

import com.alexbaryzhikov.bakingtime.datamodel.response.Ingredient;

import java.util.List;

public class RecipeItem {

  private final int id;
  private final String name;
  private final List<Ingredient> ingredients;
  private String ingredientsStr;

  public RecipeItem(int id, String name, List<Ingredient> ingredients) {
    this.id = id;
    this.name = name;
    this.ingredients = ingredients;
  }

  public int getId() {
    return id;
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
