package com.alexbaryzhikov.bakingtime.datamodel.view;

public class PlayerState {

  private final int recipeId;
  private final int stepId;
  private final int playbackState;
  private final long positionMs;
  private boolean playWhenReady;

  public PlayerState(int recipeId, int stepId, int playbackState, long positionMs, boolean playWhenReady) {
    this.recipeId = recipeId;
    this.stepId = stepId;
    this.playbackState = playbackState;
    this.positionMs = positionMs;
    this.playWhenReady = playWhenReady;
  }

  public int getRecipeId() {
    return recipeId;
  }

  public int getStepId() {
    return stepId;
  }

  public int getPlaybackState() {
    return playbackState;
  }

  public long getPositionMs() {
    return positionMs;
  }

  public boolean isPlayWhenReady() {
    return playWhenReady;
  }
}
