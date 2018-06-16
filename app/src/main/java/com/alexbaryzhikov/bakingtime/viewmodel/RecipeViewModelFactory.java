package com.alexbaryzhikov.bakingtime.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.alexbaryzhikov.bakingtime.repositiory.Repository;
import com.alexbaryzhikov.bakingtime.utils.SimpleIdlingResource;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RecipeViewModelFactory extends ViewModelProvider.NewInstanceFactory {

  private final Repository repository;
  private final SimpleIdlingResource idlingResource;

  @Inject
  RecipeViewModelFactory(Repository repository, SimpleIdlingResource idlingResource) {
    this.repository = repository;
    this.idlingResource = idlingResource;
  }

  @NonNull
  @Override
  @SuppressWarnings("unchecked")
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    return (T) new RecipeViewModel(repository, idlingResource);
  }
}
