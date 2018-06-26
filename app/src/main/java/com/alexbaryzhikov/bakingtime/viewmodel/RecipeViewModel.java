package com.alexbaryzhikov.bakingtime.viewmodel;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.alexbaryzhikov.bakingtime.datamodel.response.Recipe;
import com.alexbaryzhikov.bakingtime.datamodel.response.Step;
import com.alexbaryzhikov.bakingtime.datamodel.view.BrowseItem;
import com.alexbaryzhikov.bakingtime.datamodel.view.DetailItem;
import com.alexbaryzhikov.bakingtime.datamodel.view.StepItem;
import com.alexbaryzhikov.bakingtime.repositiory.Repository;
import com.alexbaryzhikov.bakingtime.utils.NetworkResource;
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

import static com.alexbaryzhikov.bakingtime.utils.RecipeUtils.buildIngredientsSummary;
import static com.alexbaryzhikov.bakingtime.utils.RecipeUtils.buildSteps;

public class RecipeViewModel extends ViewModel {

  private static final NetworkResource<List<BrowseItem>> LOADING_NETWORK_RESOURCE =
      NetworkResource.loading(Collections.emptyList());
  private static final NetworkResource<List<BrowseItem>> ERROR_NETWORK_RESOURCE =
      NetworkResource.error(Collections.emptyList());
  private static final BrowseRequest DEFAULT_BROWSE_REQUEST = new BrowseRequest();

  private final Application application;
  private final Repository repository;
  private final SimpleIdlingResource idlingResource;
  private final BehaviorSubject<BrowseRequest> browseSubject = BehaviorSubject.create();
  private final BehaviorSubject<DetailRequest> detailSubject = BehaviorSubject.create();
  private final BehaviorSubject<StepRequest> stepSubject = BehaviorSubject.create();

  private boolean initialized = false;
  private List<Recipe> recipes;
  private DetailRequest detailRequest;
  private StepRequest stepRequest;

  RecipeViewModel(Application application, Repository repository, SimpleIdlingResource idlingResource) {
    this.application = application;
    this.repository = repository;
    this.idlingResource = idlingResource;
  }

  public void init() {
    if (initialized) {
      return;
    }
    initialized = true;
    onBrowse();
  }

  /** Stream for browse fragment */
  public Observable<NetworkResource<List<BrowseItem>>> getBrowseStream() {
    return browseSubject
        .flatMap(browseRequest -> recipes != null ?
            Observable.just(recipes)
                .map(this::toBrowseItems)
                .map(NetworkResource::success)
            :
            repository.getRecipes()
                .doOnNext(this::cacheRecipes)
                .map(this::toNetworkResource)
                .startWith(LOADING_NETWORK_RESOURCE))
        .doOnNext(listNetworkResource -> idlingResource.setIdleState(listNetworkResource.getStatus() != NetworkResource.Status.LOADING))
        .observeOn(AndroidSchedulers.mainThread());
  }

  /** Trigger for browse stream */
  public void onBrowse() {
    browseSubject.onNext(DEFAULT_BROWSE_REQUEST);
  }

  /** Stream for detail fragment */
  public Observable<DetailItem> getDetailStream() {
    return detailSubject
        .map(detailRequest -> getRecipeDetails(detailRequest.getPosition()));
  }

  /** Setup detail request */
  public void setDetail(int position) {
    detailRequest = new DetailRequest(position);
  }

  /** Trigger for detail stream */
  public void onDetail() {
    detailSubject.onNext(detailRequest);
  }

  /** Stream for step fragment */
  public Observable<StepItem> getStepStream() {
    return stepSubject
        .map(stepRequest -> getStepDetails(stepRequest.getRecipePosition(), stepRequest.getStepPosition()));
  }

  /** Set step request */
  public void setStep(int recipePosition, int stepPosition) {
    stepRequest = new StepRequest(recipePosition, stepPosition);
  }

  /** Trigger for step stream */
  public void onStep(int direction) {
    if (direction > 0) {
      stepRequest = stepRequest.next();
    } else if (direction < 0) {
      stepRequest = stepRequest.prev();
    }
    stepSubject.onNext(stepRequest);
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

  private NetworkResource<List<BrowseItem>> toNetworkResource(@NonNull Result<List<Recipe>> result) {
    // Handle IO errors and propagate others
    if (result.isError()) {
      if (IOException.class.isInstance(result.error())) {
        return ERROR_NETWORK_RESOURCE;
      }
      RxJavaPlugins.onError(result.error());
      return ERROR_NETWORK_RESOURCE;
    }

    // Get response
    final Response<List<Recipe>> response = result.response();
    if (response == null) {
      // Should never happen. Response can be null only if result.isError() is true
      RxJavaPlugins.onError(new AssertionError("Response is null"));
      return ERROR_NETWORK_RESOURCE;
    }

    // Get response body
    final List<Recipe> recipes = response.body();
    // Return error if response is not OK (legit, we don't own the server)
    // or response body is null (should never happen)
    if (!response.isSuccessful() || recipes == null) {
      return ERROR_NETWORK_RESOURCE;
    }

    // Transform to BrowseItems and wrap in NetworkResource
    List<BrowseItem> browseItems = toBrowseItems(recipes);
    return NetworkResource.success(browseItems);
  }

  private List<BrowseItem> toBrowseItems(List<Recipe> recipes) {
    List<BrowseItem> browseItems = new ArrayList<>(recipes.size());
    for (Recipe recipe : recipes) {
      String name = recipe.name;
      String ingredients = buildIngredientsSummary(application, recipe.ingredients);
      browseItems.add(new BrowseItem(name, ingredients));
    }
    return browseItems;
  }

  private DetailItem getRecipeDetails(int position) {
    if (recipes == null) {
      throw new IllegalStateException("View model is not initialized");
    }
    Recipe recipe = recipes.get(position);
    if (recipe == null) {
      throw new AssertionError("Recipe " + position + " is null");
    }
    final String ingredients = buildIngredientsSummary(application, recipe.ingredients);
    final List<String> steps = buildSteps(application, recipe.steps);
    return new DetailItem(recipe.name, position, ingredients, steps);
  }

  private StepItem getStepDetails(int recipePosition, int stepPosition) {
    if (recipes == null) {
      throw new IllegalStateException("View model is not initialized");
    }
    Recipe recipe = recipes.get(recipePosition);
    if (recipe == null) {
      throw new AssertionError("Recipe " + recipePosition + " is null");
    }
    Step step = recipe.steps.get(stepPosition);
    if (step == null) {
      throw new AssertionError("Step " + stepPosition + " is null");
    }
    final boolean first = stepPosition == 0;
    final boolean last = stepPosition == recipe.steps.size() - 1;
    return new StepItem(step.description, step.videoURL, first, last);
  }
}
