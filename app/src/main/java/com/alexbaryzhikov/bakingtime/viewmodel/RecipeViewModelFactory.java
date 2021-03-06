package com.alexbaryzhikov.bakingtime.viewmodel;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.alexbaryzhikov.bakingtime.di.scopes.MainActivityScope;
import com.alexbaryzhikov.bakingtime.repositiory.Repository;
import com.alexbaryzhikov.bakingtime.utils.SimpleIdlingResource;

import javax.inject.Inject;
import javax.inject.Singleton;

@MainActivityScope
public class RecipeViewModelFactory extends ViewModelProvider.NewInstanceFactory {

  @Inject Application application;
  @Inject Repository repository;
  @Inject SimpleIdlingResource idlingResource;

  @Inject
  RecipeViewModelFactory() {
  }

  @NonNull
  @Override
  @SuppressWarnings("unchecked")
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    return (T) new RecipeViewModel(application, repository, idlingResource);
  }
}
