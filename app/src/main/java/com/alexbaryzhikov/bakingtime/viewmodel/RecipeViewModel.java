package com.alexbaryzhikov.bakingtime.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.alexbaryzhikov.bakingtime.datamodel.Recipe;
import com.alexbaryzhikov.bakingtime.datamodel.RecipeItem;
import com.alexbaryzhikov.bakingtime.repositiory.Repository;
import com.alexbaryzhikov.bakingtime.utils.Resource;
import com.alexbaryzhikov.bakingtime.utils.SimpleIdlingResource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import retrofit2.Response;

public class RecipeViewModel extends ViewModel {

  private static final Resource<List<RecipeItem>> errorResource =
      Resource.error(Collections.emptyList());

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
    // TODO Instead of onError find a better way to detect network and garbage response errors
    return requestSubject
        .flatMap(ignored -> repository.getRecipes()
            .map(this::transformResponse)
            .onErrorReturnItem(errorResource)
            .startWith(Resource.loading(Collections.emptyList())))
        .doOnNext(listResource -> idlingResource.setIdleState(
            listResource.getStatus() != Resource.Status.LOADING))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  public void loadRecipes() {
    requestSubject.onNext(new Object());
  }

  private Resource<List<RecipeItem>> transformResponse(@NonNull Response<List<Recipe>> response) {
    List<Recipe> recipeList = response.body();
    // Return error if response is not OK (legit, we don't own the server)
    // or response body is null (should never happen)
    if (!response.isSuccessful() || recipeList == null) {
      return errorResource;
    }
    // Transform response items into RecipeItem list and return it
    List<RecipeItem> recipeItemList = new ArrayList<>(recipeList.size());
    for (Recipe recipe : recipeList) {
      recipeItemList.add(new RecipeItem(recipe.name));
    }
    return Resource.success(recipeItemList);
  }
}
