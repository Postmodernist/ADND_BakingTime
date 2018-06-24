package com.alexbaryzhikov.bakingtime.datamodel.view;

public final class StepItem {

  private final String description;
  private final String videoUrl;
  private final boolean first;
  private final boolean last;

  public StepItem(String description, String videoUrl, boolean first, boolean last) {
    this.description = description;
    this.videoUrl = videoUrl;
    this.first = first;
    this.last = last;
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
