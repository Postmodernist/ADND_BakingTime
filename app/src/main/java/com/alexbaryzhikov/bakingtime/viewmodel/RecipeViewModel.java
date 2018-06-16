package com.alexbaryzhikov.bakingtime.viewmodel;

import android.arch.lifecycle.ViewModel;

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
    return Observable.just(new Object())
        .doOnNext(o -> idlingResource.setIdleState(false))
        .flatMap(o -> repository.getRecipes())
        .doAfterNext(recipeItems -> idlingResource.setIdleState(true))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }
}
