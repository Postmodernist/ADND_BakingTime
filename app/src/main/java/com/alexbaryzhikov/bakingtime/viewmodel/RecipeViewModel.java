package com.alexbaryzhikov.bakingtime.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.alexbaryzhikov.bakingtime.datamodel.response.Recipe;
import com.alexbaryzhikov.bakingtime.datamodel.view.RecipeItem;
import com.alexbaryzhikov.bakingtime.repositiory.Repository;
import com.alexbaryzhikov.bakingtime.utils.Resource;
import com.alexbaryzhikov.bakingtime.utils.SimpleIdlingResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.BehaviorSubject;
import retrofit2.Response;
import retrofit2.adapter.rxjava2.Result;

public class RecipeViewModel extends ViewModel {

  private static final Resource<List<RecipeItem>> errorResource =
      Resource.error(Collections.emptyList());

  private final Repository repository;
  private final SimpleIdlingResource idlingResource;
  private final BehaviorSubject<Object> requestSubject = BehaviorSubject.create();
  private boolean initialized = false;
  private List<Recipe> recipes;

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

  /** Get recipes observable */
  public Observable<Resource<List<RecipeItem>>> getRecipes() {
    return requestSubject
        .flatMap(ignored -> repository.getRecipes()
            .doOnNext(this::cacheRecipes)
            .map(this::toResource)
            .startWith(Resource.loading(Collections.emptyList())))
        .doOnNext(listResource -> idlingResource.setIdleState(listResource.getStatus() != Resource.Status.LOADING))
        .observeOn(AndroidSchedulers.mainThread());
  }

  /** Trigger recipes observable to load recipes */
  public void loadRecipes() {
    requestSubject.onNext(new Object());
  }

  public Recipe getRecipe(int position) {
    return recipes.get(position);
  }

  private void cacheRecipes(Result<List<Recipe>> listResult) {
    if (listResult.isError()) {
      return;
    }
    Response<List<Recipe>> response = listResult.response();
    if (response != null) {
      recipes = response.body();
    }
  }

  private Resource<List<RecipeItem>> toResource(@NonNull Result<List<Recipe>> result) {
    // Handle IO errors and propagate others
    if (result.isError()) {
      if (IOException.class.isInstance(result.error())) {
        return errorResource;
      }
      RxJavaPlugins.onError(result.error());
      return errorResource;
    }

    // Get response
    final Response<List<Recipe>> response = result.response();
    if (response == null) {
      // Should never happen. Response can be null only if result.isError() is true
      RxJavaPlugins.onError(new AssertionError("result.isError() is false, but result.response() is null"));
      return errorResource;
    }

    // Get response body
    final List<Recipe> recipeList = response.body();
    // Return error if response is not OK (legit, we don't own the server)
    // or response body is null (should never happen)
    if (!response.isSuccessful() || recipeList == null) {
      return errorResource;
    }

    // Transform response items into RecipeItem list and return it
    List<RecipeItem> recipeItemList = new ArrayList<>(recipeList.size());
    for (Recipe recipe : recipeList) {
      recipeItemList.add(new RecipeItem(recipe.name, recipe.ingredients));
    }
    return Resource.success(recipeItemList);
  }
}
