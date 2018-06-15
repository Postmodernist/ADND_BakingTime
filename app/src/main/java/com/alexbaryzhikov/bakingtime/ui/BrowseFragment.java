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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alexbaryzhikov.bakingtime.BakingApp;
import com.alexbaryzhikov.bakingtime.R;
import com.alexbaryzhikov.bakingtime.di.components.DaggerBrowseFragmentComponent;
import com.alexbaryzhikov.bakingtime.viewmodel.RecipeViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

/** List of recipes */
public class BrowseFragment extends Fragment {

  @BindView(R.id.recipe_list) RecyclerView recipeList;
  @BindView(R.id.error_message) TextView errorMessage;
  @BindView(R.id.loading_indicator) ProgressBar loadingIndicator;

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
    setupRecipesList();
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
    disposable = adapter.subscribeTo(viewModel.getRecipes());
  }
}
