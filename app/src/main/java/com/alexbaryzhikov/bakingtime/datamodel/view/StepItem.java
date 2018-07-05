package com.alexbaryzhikov.bakingtime.datamodel.view;

public final class StepItem {

  private final int recipeId;
  private final int stepId;
  private final String recipeName;
  private final String description;
  private final String videoUrl;
  private final boolean first;
  private final boolean last;

  public StepItem(int recipeId, int stepId, String recipeName, String description, String videoUrl, boolean first, boolean last) {
    this.recipeId = recipeId;
    this.stepId = stepId;
    this.recipeName = recipeName;
    this.description = description;
    this.videoUrl = videoUrl;
    this.first = first;
    this.last = last;
  }

  public int getRecipeId() {
    return recipeId;
  }

  public int getStepId() {
    return stepId;
  }

  public String getRecipeName() {
    return recipeName;
  }

  public String getDescription() {
    return description;
  }

  public String getVideoUrl() {
    return videoUrl;
  }

  public boolean isFirst() {
    return first;
  }

  public boolean isLast() {
    return last;
  }
}
