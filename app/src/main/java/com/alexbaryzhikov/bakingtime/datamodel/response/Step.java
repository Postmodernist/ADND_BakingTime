package com.alexbaryzhikov.bakingtime.datamodel.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Step {

  @SerializedName("id")
  @Expose
  public Integer id;
  @SerializedName("shortDescription")
  @Expose
  public String shortDescription;
  @SerializedName("description")
  @Expose
  public String description;
  @SerializedName("videoURL")
  @Expose
  public String videoURL;
  @SerializedName("thumbnailURL")
  @Expose
  public String thumbnailURL;

}
