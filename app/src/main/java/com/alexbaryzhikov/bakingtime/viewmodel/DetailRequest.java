package com.alexbaryzhikov.bakingtime.viewmodel;

public final class DetailRequest {

  private final int position;

  DetailRequest(int position) {
    this.position = position;
  }

  public int getPosition() {
    return position;
  }
}
