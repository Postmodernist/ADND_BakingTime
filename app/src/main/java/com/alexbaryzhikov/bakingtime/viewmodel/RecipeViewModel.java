package com.alexbaryzhikov.bakingtime.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.support.test.espresso.IdlingResource;

import com.alexbaryzhikov.bakingtime.datamodel.RecipeItem;
import com.alexbaryzhikov.bakingtime.repositiory.Repository;
import com.alexbaryzhikov.bakingtime.utils.SimpleIdlingResource;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RecipeViewModel extends ViewModel {

  private final Repository repository;
  private final SimpleIdlingResource idlingResource;

  RecipeViewModel(Repository repository, SimpleIdlingResource idlingResource) {
    this.repository = repository;
    this.idlingResource = idlingResource;
  }

  public Observable<List<RecipeItem>> getRecipes() {
    return repository.getRecipes()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }
}
