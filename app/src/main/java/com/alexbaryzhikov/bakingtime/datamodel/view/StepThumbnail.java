package com.alexbaryzhikov.bakingtime.datamodel.view;

import android.graphics.drawable.Drawable;

public class StepThumbnail {

  private final Drawable drawable;
  private final int stepId;

  public StepThumbnail(Drawable drawable, int stepId) {
    this.drawable = drawable;
    this.stepId = stepId;
  }

  public Drawable getDrawable() {
    return drawable;
  }

  public int getStepId() {
    return stepId;
  }
}
