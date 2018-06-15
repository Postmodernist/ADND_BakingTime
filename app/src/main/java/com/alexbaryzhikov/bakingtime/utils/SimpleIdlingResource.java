package com.alexbaryzhikov.bakingtime.utils;

import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SimpleIdlingResource implements IdlingResource {

  @Nullable private volatile ResourceCallback callback;
  private AtomicBoolean isIdle = new AtomicBoolean(true);

  @Inject
  SimpleIdlingResource() {
  }

  @Override
  public String getName() {
    return getClass().getName();
  }

  @Override
  public boolean isIdleNow() {
    return isIdle.get();
  }

  @Override
  public void registerIdleTransitionCallback(ResourceCallback callback) {
    this.callback = callback;
  }

  public void setIdleState(boolean isIdle) {
    this.isIdle.set(isIdle);
    final ResourceCallback callback = this.callback;
    if (isIdle && callback != null) {
      callback.onTransitionToIdle();
    }
  }
}
