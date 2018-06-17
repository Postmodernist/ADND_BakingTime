package com.alexbaryzhikov.bakingtime.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.alexbaryzhikov.bakingtime.datamodel.RecipeItem;
import com.alexbaryzhikov.bakingtime.repositiory.Repository;
import com.alexbaryzhikov.bakingtime.utils.Resource;
import com.alexbaryzhikov.bakingtime.utils.SimpleIdlingResource;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class RecipeViewModel extends ViewModel {

  private final Repository repository;
  private final SimpleIdlingResource idlingResource;
  private final BehaviorSubject<Object> requestSubject = BehaviorSubject.create();
  private boolean initialized = false;

  RecipeViewModel(Repository repository, SimpleIdlingResource idlingResource) {
    this.repository = repository;
    this.idlingResource = idlingResource;
  }

  public void init() {
    if (initialized) {
      return;
    }
    initialized = true;
    loadRecipes();
  }

  public Observable<Resource<List<RecipeItem>>> getRecipes() {
    // TODO Detect errors incoming from repository
    return requestSubject
        .flatMap(ignored -> repository.getRecipes()
            .map(Resource::success)
            .startWith(Resource.loading(Collections.emptyList())))
        .doOnNext(listResource -> idlingResource.setIdleState(
            listResource.getStatus() != Resource.Status.LOADING))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  public void loadRecipes() {
    requestSubject.onNext(new Object());
  }
}
