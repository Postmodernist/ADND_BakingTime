package com.alexbaryzhikov.bakingtime.di.components;

import com.alexbaryzhikov.bakingtime.di.modules.MainActivityModule;
import com.alexbaryzhikov.bakingtime.di.scopes.MainActivityScope;
import com.alexbaryzhikov.bakingtime.repositiory.Repository;
import com.alexbaryzhikov.bakingtime.ui.MainActivity;
import com.alexbaryzhikov.bakingtime.utils.SimpleIdlingResource;
import com.alexbaryzhikov.bakingtime.viewmodel.RecipeViewModelFactory;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = MainActivityModule.class, dependencies = ApplicationComponent.class)
@MainActivityScope
public interface MainActivityComponent {

  Repository repository();

  RecipeViewModelFactory recipeViewModelFactory();

  SimpleIdlingResource simpleIdlingResource();

  MainActivity mainActivity();

  @Component.Builder
  interface Builder {

    Builder appComponent(ApplicationComponent applicationComponent);

    @BindsInstance
    Builder activity(MainActivity mainActivity);

    MainActivityComponent build();
  }

}
