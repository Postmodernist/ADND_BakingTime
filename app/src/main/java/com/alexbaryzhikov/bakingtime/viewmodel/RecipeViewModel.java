package com.alexbaryzhikov.bakingtime.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.graphics.drawable.Drawable;

import com.alexbaryzhikov.bakingtime.datamodel.response.Recipe;
import com.alexbaryzhikov.bakingtime.datamodel.response.Step;
import com.alexbaryzhikov.bakingtime.datamodel.view.BrowseItem;
import com.alexbaryzhikov.bakingtime.datamodel.view.DetailItem;
import com.alexbaryzhikov.bakingtime.datamodel.view.PlayerState;
import com.alexbaryzhikov.bakingtime.datamodel.view.StepItem;
import com.alexbaryzhikov.bakingtime.datamodel.view.StepThumbnail;
import com.alexbaryzhikov.bakingtime.repositiory.Repository;
import com.alexbaryzhikov.bakingtime.utils.NetworkResource;
import com.alexbaryzhikov.bakingtime.utils.RecipeUtils;
import com.alexbaryzhikov.bakingtime.utils.SimpleIdlingResource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import retrofit2.Response;
import retrofit2.adapter.rxjava2.Result;

import static com.alexbaryzhikov.bakingtime.utils.RecipeUtils.buildDescriptions;
import static com.alexbaryzhikov.bakingtime.utils.RecipeUtils.buildIngredientsSummary;

public class RecipeViewModel extends ViewModel {

  private static final NetworkResource<List<BrowseItem>> LOADING_NETWORK_RESOURCE =
      NetworkResource.loading(Collections.emptyList());
  private static final BrowseRequest DEFAULT_BROWSE_REQUEST = new BrowseRequest();

  private final Application application;
  private final Repository repository;
  private final SimpleIdlingResource idlingResource;
  private final BehaviorSubject<BrowseRequest> browseSubject = BehaviorSubject.create();
  private final BehaviorSubject<DetailRequest> detailSubject = BehaviorSubject.create();
  private final BehaviorSubject<StepRequest> stepSubject = BehaviorSubject.create();

  private boolean initialized = false;
  private boolean phone;
  private List<Recipe> recipesCache;
  private Map<Integer, Observable<StepThumbnail>> thumbnailsCache;
  private StepItem stepItemCache;
  private CompositeDisposable disposables;
  private DetailRequest detailRequest;
  private StepRequest stepRequest;
  private PlayerState playerState;

  RecipeViewModel(Application application, Repository repository, SimpleIdlingResource idlingResource) {
    this.application = application;
    this.repository = repository;
    this.idlingResource = idlingResource;
  }

  @SuppressLint("UseSparseArrays")
  public void init() {
    if (initialized) {
      return;
    }
    initialized = true;
    phone = application.getResources().getConfiguration().smallestScreenWidthDp < 600;
    thumbnailsCache = new HashMap<>();
    disposables = new CompositeDisposable();
    emitBrowse();
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    if (disposables != null) {
      disposables.dispose();
    }
  }

  /** Stream for browse fragment */
  public Observable<NetworkResource<List<BrowseItem>>> getBrowseStream() {
    return browseSubject
        .flatMap(browseRequest -> recipesCache != null ?
            Observable.just(recipesCache)
                .map(this::toBrowseItems)
                .map(NetworkResource::success)
            :
            repository.getRecipes()
                .doOnNext(this::cacheRecipes)
                .map(listResult -> RecipeUtils.toNetworkResource(listResult, this::toBrowseItems))
                .startWith(LOADING_NETWORK_RESOURCE))
        .doOnNext(networkResource ->
            idlingResource.setIdleState(networkResource.getStatus() != NetworkResource.Status.LOADING))
        .observeOn(AndroidSchedulers.mainThread());
  }

  /** Trigger browse request */
  public void emitBrowse() {
    browseSubject.onNext(DEFAULT_BROWSE_REQUEST);
  }

  /** Stream for detail fragment */
  public Observable<DetailItem> getDetailStream() {
    return detailSubject
        .doOnNext(request -> detailRequest = request)
        .map(detailRequest -> getRecipeDetail(detailRequest.getPosition()))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  /** Trigger detail request */
  public void emitDetail(int position) {
    detailSubject.onNext(new DetailRequest(position));
  }

  /** Stream of recipe thumbnails */
  public Observable<StepThumbnail> getStepThumbnailStream() {
    return detailSubject.map(DetailRequest::getPosition)
        .flatMap(position -> {
          if (!thumbnailsCache.containsKey(position)) {
            Observable<StepThumbnail> thumbnailFetcher = Observable.fromIterable(getRecipeSteps(position))
                .map(step -> {
                  Drawable thumbnail = RecipeUtils.buildThumbnail(application, step);
                  return new StepThumbnail(thumbnail, step.id);
                })
                .subscribeOn(Schedulers.io())
                .replay()
                .autoConnect();
            thumbnailsCache.put(position, thumbnailFetcher);
            disposables.add(thumbnailFetcher.subscribe()); // Prefetch
          }
          return thumbnailsCache.get(position);
        })
        .observeOn(AndroidSchedulers.mainThread());
  }

  /** Stream for detail item selection */
  public Observable<Integer> getDetailSelectionStream() {
    return stepSubject
        .skip(phone ? 0 : 1)
        .map(StepRequest::getStepPosition);
  }

  /** Stream for step fragment */
  public Observable<StepItem> getStepStream() {
    return stepSubject
        .doOnNext(request -> stepRequest = request)
        .map(stepRequest -> getStepDetail(stepRequest.getRecipePosition(), stepRequest.getStepPosition()))
        .doOnNext(stepItem -> stepItemCache = stepItem);
  }

  /** Trigger step request */
  public void emitStep(int recipePosition, int stepPosition) {
    stepItemCache = null;
    playerState = null;
    stepSubject.onNext(new StepRequest(recipePosition, stepPosition));
  }

  /** Trigger step request */
  public void emitNextStep() {
    stepItemCache = null;
    playerState = null;
    stepSubject.onNext(stepRequest.next());
  }

  /** Trigger step request */
  public void emitPrevStep() {
    stepItemCache = null;
    playerState = null;
    stepSubject.onNext(stepRequest.prev());
  }

  public int getRecipePosition() {
    if (detailRequest == null) {
      return 0;
    }
    return detailRequest.getPosition();
  }

  public PlayerState getPlayerState() {
    return playerState;
  }

  public void setPlayerState(PlayerState playerState) {
    this.playerState = playerState;
  }

  public StepItem getStepItemCache() {
    return stepItemCache;
  }

  private void cacheRecipes(Result<List<Recipe>> listResult) {
    if (listResult.isError()) {
      return;
    }
    Response<List<Recipe>> response = listResult.response();
    if (response != null) {
      List<Recipe> body = response.body();
      if (body != null) {
        recipesCache = new ArrayList<>(body);
      }
    }
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

  private DetailItem getRecipeDetail(int position) {
    if (recipesCache == null) {
      throw new IllegalStateException("Cache is not initialized");
    }
    Recipe recipe = recipesCache.get(position);
    if (recipe == null) {
      throw new AssertionError("Cache item " + position + " is null");
    }
    final String ingredients = buildIngredientsSummary(application, recipe.ingredients);
    final List<String> descriptions = buildDescriptions(application, recipe.steps);
    return new DetailItem(recipe.name, position, ingredients, descriptions);
  }

  private List<Step> getRecipeSteps(int position) {
    if (recipesCache == null) {
      throw new IllegalStateException("Cache is not initialized");
    }
    Recipe recipe = recipesCache.get(position);
    if (recipe == null) {
      throw new AssertionError("Cache item " + position + " is null");
    }
    return recipe.steps;
  }

  private StepItem getStepDetail(int recipePosition, int stepPosition) {
    if (recipesCache == null) {
      throw new IllegalStateException("View model is not initialized");
    }
    Recipe recipe = recipesCache.get(recipePosition);
    if (recipe == null) {
      throw new AssertionError("Recipe " + recipePosition + " is null");
    }
    Step step = recipe.steps.get(stepPosition);
    if (step == null) {
      throw new AssertionError("Step " + stepPosition + " is null");
    }
    final boolean first = stepPosition == 0;
    final boolean last = stepPosition == recipe.steps.size() - 1;
    return new StepItem(recipe.id, step.id, recipe.name, step.description, step.videoURL, first, last);
  }
}
