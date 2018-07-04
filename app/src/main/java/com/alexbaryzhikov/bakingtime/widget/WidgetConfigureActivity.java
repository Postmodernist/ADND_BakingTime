package com.alexbaryzhikov.bakingtime.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.alexbaryzhikov.bakingtime.R;
import com.alexbaryzhikov.bakingtime.datamodel.response.Recipe;
import com.alexbaryzhikov.bakingtime.di.components.DaggerWidgetConfigureActivityComponent;
import com.alexbaryzhikov.bakingtime.repositiory.Repository;
import com.alexbaryzhikov.bakingtime.ui.MainActivity;
import com.alexbaryzhikov.bakingtime.utils.NetworkResource;
import com.alexbaryzhikov.bakingtime.utils.RecipeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public class WidgetConfigureActivity extends AppCompatActivity {

  public static final String PREF_ID_KEY = "_id";
  public static final String PREF_NAME_KEY = "_name";
  public static final String PREF_INGREDIENTS_KEY = "_ingredients";
  private static final String PREFS_NAME = "com.alexbaryzhikov.bakingtime.widget.RecipeWidgetProvider";
  private static final String PREF_PREFIX = "widget_";

  @BindView(R.id.recipe_list) RecyclerView recipeList;
  @BindView(R.id.loading_indicator) ProgressBar loadingIndicator;
  @BindView(R.id.error_view_group) ViewGroup errorViewGroup;
  @BindView(R.id.refresh_button) Button refreshButton;

  @Inject Repository repository;

  private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
  private BehaviorSubject<Object> loadSubject = BehaviorSubject.create();
  private Disposable disposable;

  public WidgetConfigureActivity() {
    super();
  }

  public static void savePrefs(@NonNull Context context, final int appWidgetId, final int recipeId,
                               @NonNull final String name, @NonNull final String ingredients) {
    context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
        .putInt(PREF_PREFIX + appWidgetId + PREF_ID_KEY, recipeId)
        .putString(PREF_PREFIX + appWidgetId + PREF_NAME_KEY, name)
        .putString(PREF_PREFIX + appWidgetId + PREF_INGREDIENTS_KEY, ingredients)
        .apply();
  }

  public static int loadIdPref(@NonNull Context context, final int appWidgetId) {
    SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    return prefs.getInt(PREF_PREFIX + appWidgetId + PREF_ID_KEY, MainActivity.INVALID_RECIPE_ID);
  }

  @NonNull
  public static String loadNamePref(@NonNull Context context, final int appWidgetId) {
    SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    return prefs.getString(PREF_PREFIX + appWidgetId + PREF_NAME_KEY, "No recipe available");
  }

  @NonNull
  public static String loadIngredientsPref(@NonNull Context context, final int appWidgetId) {
    SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    return prefs.getString(PREF_PREFIX + appWidgetId + PREF_INGREDIENTS_KEY, "");
  }

  public static void deletePrefs(Context context, int appWidgetId) {
    context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
        .remove(PREF_PREFIX + appWidgetId + PREF_ID_KEY)
        .remove(PREF_PREFIX + appWidgetId + PREF_NAME_KEY)
        .remove(PREF_PREFIX + appWidgetId + PREF_INGREDIENTS_KEY)
        .apply();
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    setupDagger();
    super.onCreate(savedInstanceState);
    setResult(RESULT_CANCELED);
    setContentView(R.layout.activity_widget_configure);
    ButterKnife.bind(this);
    setupActivity();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (disposable != null) {
      disposable.dispose();
    }
  }

  private void setupDagger() {
    DaggerWidgetConfigureActivityComponent.builder().activity(this).build().inject(this);
  }

  private void setupActivity() {
    // Get the widget id from the intent
    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    if (extras != null) {
      appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }
    // Bail out if widget id is invalid
    if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
      finish();
    }
    // Set title
    setTitle(getString(R.string.widget_configure_activity_title));
    // Setup refresh button
    refreshButton.setOnClickListener(v -> emitRecipes());
    // Setup recipes list
    final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
    final WidgetConfigureAdapter adapter = new WidgetConfigureAdapter(this::returnResult);
    final DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
    recipeList.setLayoutManager(layoutManager);
    recipeList.setAdapter(adapter);
    recipeList.addItemDecoration(divider);
    // Subscribe to recipes
    disposable = loadSubject.flatMap(o ->
        repository.getRecipes()
            .map(listResult -> RecipeUtils.toNetworkResource(listResult, this::toWidgetConfigureItems))
            .startWith(NetworkResource.loading(Collections.emptyList())))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(this::renderNetworkStatus)
        .filter(networkResource -> networkResource.getStatus() == NetworkResource.Status.SUCCESS)
        .map(RecipeUtils::stripNetworkStatus)
        .subscribe(adapter::setItems, throwable -> { throw new RuntimeException(throwable); });
    emitRecipes();
  }

  private List<WidgetConfigureItem> toWidgetConfigureItems(List<Recipe> recipes) {
    List<WidgetConfigureItem> items = new ArrayList<>(recipes.size());
    for (Recipe recipe : recipes) {
      final String ingredients = RecipeUtils.buildIngredientsSummary(this, recipe.ingredients);
      items.add(new WidgetConfigureItem(recipe.name, ingredients));
    }
    return items;
  }

  private void emitRecipes() {
    loadSubject.onNext(new Object());
  }

  private void renderNetworkStatus(NetworkResource networkResource) {
    NetworkResource.Status status = networkResource.getStatus();
    switch (status) {
      case LOADING:
        loadingIndicator.setVisibility(View.VISIBLE);
        errorViewGroup.setVisibility(View.INVISIBLE);
        break;
      case SUCCESS:
        loadingIndicator.setVisibility(View.INVISIBLE);
        errorViewGroup.setVisibility(View.INVISIBLE);
        break;
      case ERROR:
        loadingIndicator.setVisibility(View.INVISIBLE);
        errorViewGroup.setVisibility(View.VISIBLE);
        break;
      default:
        throw new IllegalArgumentException("Unknown status: " + status.name());
    }
  }

  private void returnResult(int position, WidgetConfigureItem item) {
    // Save the widget data to prefs
    savePrefs(this, appWidgetId, position, item.getName(), item.getIngredients());

    // Push widget update to surface with newly set name
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
    RecipeWidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetId, item.getName());

    // Pass back the OK result with the original appWidgetId
    Intent result = new Intent();
    result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
    setResult(RESULT_OK, result);
    finish();
  }
}
