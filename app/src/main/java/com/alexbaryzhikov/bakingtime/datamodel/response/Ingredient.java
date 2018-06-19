package com.alexbaryzhikov.bakingtime.datamodel.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ingredient {

  @SerializedName("quantity")
  @Expose
  public Double quantity;
  @SerializedName("measure")
  @Expose
  public String measure;
  @SerializedName("ingredient")
  @Expose
  public String ingredient;

}
