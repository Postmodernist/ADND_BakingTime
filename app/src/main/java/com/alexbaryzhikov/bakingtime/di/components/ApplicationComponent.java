package com.alexbaryzhikov.bakingtime.di.components;

import android.app.Application;

import com.alexbaryzhikov.bakingtime.BakingApp;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Component
@Singleton
public interface ApplicationComponent {

  void inject(BakingApp app);

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder application(Application application);

    ApplicationComponent build();
  }
}
