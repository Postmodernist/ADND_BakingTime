package com.alexbaryzhikov.bakingtime;

import android.app.Application;
import android.content.Context;

import com.alexbaryzhikov.bakingtime.di.components.ApplicationComponent;
import com.alexbaryzhikov.bakingtime.di.components.DaggerApplicationComponent;

public class BakingApp extends Application {

  private ApplicationComponent appComponent;

  public static ApplicationComponent getAppComponent(Context context) {
    return ((BakingApp) context.getApplicationContext()).appComponent;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    appComponent = DaggerApplicationComponent.builder()
        .application(this)
        .build();
  }
}
