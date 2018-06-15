package com.alexbaryzhikov.bakingtime.di.components;

import android.app.Application;

import com.alexbaryzhikov.bakingtime.BakingApp;
import com.alexbaryzhikov.bakingtime.di.modules.ApplicationModule;
import com.alexbaryzhikov.bakingtime.utils.SimpleIdlingResource;
import com.alexbaryzhikov.bakingtime.viewmodel.RecipeViewModelFactory;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = ApplicationModule.class)
@Singleton
public interface ApplicationComponent {

  void inject(BakingApp app);

  RecipeViewModelFactory factory();

  SimpleIdlingResource idlingResource();

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder application(Application application);

    ApplicationComponent build();
  }
}
