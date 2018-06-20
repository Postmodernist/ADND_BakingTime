package com.alexbaryzhikov.bakingtime.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexbaryzhikov.bakingtime.BakingApp;
import com.alexbaryzhikov.bakingtime.R;
import com.alexbaryzhikov.bakingtime.datamodel.response.Ingredient;
import com.alexbaryzhikov.bakingtime.datamodel.response.Recipe;
import com.alexbaryzhikov.bakingtime.di.components.DaggerDetailFragmentComponent;
import com.alexbaryzhikov.bakingtime.viewmodel.RecipeViewModel;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alexbaryzhikov.bakingtime.utils.RecipeUtils.smartValueOf;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

  private static final String KEY_POSITION = "position";

  @BindView(R.id.recipe_ingredients) TextView recipeIngredients;

  @Inject RecipeViewModel viewModel;
  @Inject MainActivity mainActivity;

  public DetailFragment() {
    // Required empty public constructor
  }

  static DetailFragment forRecipe(int position) {
    DetailFragment fragment = new DetailFragment();
    Bundle args = new Bundle();
    args.putInt(KEY_POSITION, position);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onAttach(Context context) {
    setupDagger();
    super.onAttach(context);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_detail, container, false);
    ButterKnife.bind(this, view);
    setupFragment();
    return view;
  }

  private void setupDagger() {
    assert getContext() != null;
    DaggerDetailFragmentComponent.builder()
        .appComponent(BakingApp.getAppComponent(getContext()))
        .fragment(this)
        .build()
        .inject(this);
  }

  private void setupFragment() {
    // Show action bar back button
    mainActivity.showBackInActionBar();
    // Get fragment arguments
    Bundle args = getArguments();
    if (args != null && args.containsKey(KEY_POSITION)) {
      final int position = args.getInt(KEY_POSITION);
      Recipe recipe = viewModel.getRecipe(position);
      // Set activity title
      mainActivity.setTitle(recipe.name);
      // Set ingredients list
      recipeIngredients.setText(buildIngredientsString(recipe.ingredients));
    }
  }

  private String buildIngredientsString(List<Ingredient> ingredients) {
    StringBuilder sb = new StringBuilder();
    for (Ingredient ingredient : ingredients) {
      sb.append(getString(R.string.ingredient,
          smartValueOf(ingredient.quantity, ingredient.measure), ingredient.ingredient));
    }
    return sb.substring(0, sb.length() - 1);
  }
}
