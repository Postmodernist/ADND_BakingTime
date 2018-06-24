package com.alexbaryzhikov.bakingtime.viewmodel;

public final class StepRequest {

  private final int recipePosition;
  private final int stepPosition;

  StepRequest(int recipePosition, int stepPosition) {
    this.recipePosition = recipePosition;
    this.stepPosition = stepPosition;
  }

  public int getRecipePosition() {
    return recipePosition;
  }

  public int getStepPosition() {
    return stepPosition;
  }
}
