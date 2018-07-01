package com.alexbaryzhikov.bakingtime.di.components;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Component
@Singleton
public interface ApplicationComponent {

  Application application();

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder application(Application application);

    ApplicationComponent build();
  }
}
