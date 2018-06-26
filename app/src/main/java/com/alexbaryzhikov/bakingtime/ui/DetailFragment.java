package com.alexbaryzhikov.bakingtime.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexbaryzhikov.bakingtime.BakingApp;
import com.alexbaryzhikov.bakingtime.R;
import com.alexbaryzhikov.bakingtime.di.components.DaggerDetailFragmentComponent;
import com.alexbaryzhikov.bakingtime.di.components.DetailFragmentComponent;
import com.alexbaryzhikov.bakingtime.viewmodel.RecipeViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

/** Cooking steps */
public class DetailFragment extends Fragment {

  @BindView(R.id.cooking_steps) RecyclerView cookingSteps;

  @Inject RecipeViewModel viewModel;
  @Inject MainActivity mainActivity;
  @Inject DetailAdapter adapter;

  private DetailFragmentComponent detailFragmentComponent;
  private Disposable disposable;

  public DetailFragment() {
    // Required empty public constructor
  }

  @Override
  public void onAttach(Context context) {
    setupDagger(context);
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

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (disposable != null) {
      disposable.dispose();
    }
  }

  private void setupDagger(Context context) {
    detailFragmentComponent = DaggerDetailFragmentComponent.builder()
        .appComponent(BakingApp.getAppComponent(context))
        .fragment(this)
        .build();
    detailFragmentComponent.inject(this);
  }

  private void setupFragment() {
    // Show action bar back button
    mainActivity.showBackInActionBar();
    // Setup cooking steps list
    DividerItemDecoration divider =
        new DividerItemDecoration(cookingSteps.getContext(), DividerItemDecoration.VERTICAL);
    cookingSteps.setLayoutManager(detailFragmentComponent.layoutManager());
    cookingSteps.addItemDecoration(divider);
    cookingSteps.setAdapter(adapter);
    disposable = adapter.subscribeTo(viewModel.getDetailStream()
        // Set activity title
        .doOnNext(recipeDetails -> mainActivity.setTitle(recipeDetails.getName())));
    viewModel.onDetail();
  }
}
