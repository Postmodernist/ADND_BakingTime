package com.alexbaryzhikov.bakingtime.datamodel;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Recipe {

  @SerializedName("id")
  @Expose
  public Integer id;
  @SerializedName("name")
  @Expose
  public String name;
  @SerializedName("ingredients")
  @Expose
  public List<Ingredient> ingredients = null;
  @SerializedName("steps")
  @Expose
  public List<Step> steps = null;
  @SerializedName("servings")
  @Expose
  public Double servings;
  @SerializedName("image")
  @Expose
  public String image;

}
