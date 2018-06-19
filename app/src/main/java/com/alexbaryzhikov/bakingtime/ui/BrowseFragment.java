package com.alexbaryzhikov.bakingtime.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.alexbaryzhikov.bakingtime.BakingApp;
import com.alexbaryzhikov.bakingtime.R;
import com.alexbaryzhikov.bakingtime.datamodel.response.Ingredient;
import com.alexbaryzhikov.bakingtime.datamodel.view.RecipeItem;
import com.alexbaryzhikov.bakingtime.di.components.DaggerBrowseFragmentComponent;
import com.alexbaryzhikov.bakingtime.utils.Resource;
import com.alexbaryzhikov.bakingtime.viewmodel.RecipeViewModel;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

/** List of recipes */
public class BrowseFragment extends Fragment {

  @BindView(R.id.recipe_list) RecyclerView recipeList;
  @BindView(R.id.loading_indicator) ProgressBar loadingIndicator;
  @BindView(R.id.error_viewgroup) ViewGroup errorViewGroup;
  @BindView(R.id.refresh_button) Button refreshButton;

  @Inject LayoutManager layoutManager;
  @Inject RecipeAdapter adapter;
  @Inject RecipeViewModel viewModel;

  private Disposable disposable;

  public BrowseFragment() {
    // Required empty public constructor
  }

  @Override
  public void onAttach(Context context) {
    setupDagger();
    super.onAttach(context);
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_browse, container, false);
    ButterKnife.bind(this, view);
    viewModel.init();
    setupRecipesList();
    refreshButton.setOnClickListener(v -> viewModel.loadRecipes());
    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    disposable.dispose();
  }

  private void setupDagger() {
    assert getContext() != null;
    DaggerBrowseFragmentComponent.builder()
        .appComponent(BakingApp.getAppComponent(getContext()))
        .fragment(this)
        .build()
        .inject(this);
  }

  private void setupRecipesList() {
    recipeList.setLayoutManager(layoutManager);
    recipeList.setAdapter(adapter);
    disposable = adapter.subscribeTo(viewModel.getRecipes()
        .doOnNext(listResource -> renderStatus(listResource.getStatus()))
        .map(this::buildIngredientsString));
  }

  private void renderStatus(Resource.Status status) {
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

  private List<RecipeItem> buildIngredientsString(Resource<List<RecipeItem>> listResource) {
    List<RecipeItem> recipes = listResource.getData();
    assert recipes != null;
    for (RecipeItem recipe : recipes) {
      StringBuilder sb = new StringBuilder();
      for (Ingredient ingredient : recipe.getIngredients()) {
        sb.append(getString(R.string.ingredient, smartValueOf(ingredient.quantity, ingredient.measure), ingredient.ingredient));
      }
      recipe.setIngredientsStr(sb.substring(0, sb.length() - 1));
    }
    return recipes;
  }

  private String smartValueOf(Double d, String measure) {
    String quantity = d % 1 == 0 ? String.valueOf(d.intValue()) : String.valueOf(d);
    return quantity + humanReadableMeasure(measure, d.compareTo(1.0) == 0);
  }

  private String humanReadableMeasure(String measure, boolean single) {
    switch (measure) {
      case "CUP": return single ? " cup of" : " cups of";
      case "TBLSP": return " tbs. of";
      case "TSP": return " tsp. of";
      case "G": return " g of";
      case "K": return " kg of";
      case "OZ": return " oz of";
      case "UNIT": return "";
      default: return " " + measure + " of";
    }
  }
}
