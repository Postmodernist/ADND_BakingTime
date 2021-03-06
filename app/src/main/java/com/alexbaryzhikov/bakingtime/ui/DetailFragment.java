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

import com.alexbaryzhikov.bakingtime.R;
import com.alexbaryzhikov.bakingtime.di.components.DaggerDetailFragmentComponent;
import com.alexbaryzhikov.bakingtime.di.components.DetailFragmentComponent;
import com.alexbaryzhikov.bakingtime.viewmodel.RecipeViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/** Cooking steps */
public class DetailFragment extends Fragment {

  @BindView(R.id.cooking_steps) RecyclerView cookingSteps;

  @Inject RecipeViewModel viewModel;
  @Inject MainActivity mainActivity;
  @Inject DetailAdapter adapter;
  @Inject DividerItemDecoration divider;
  @Inject CompositeDisposable disposable;

  private DetailFragmentComponent detailFragmentComponent;
  private Disposable thumbnailsDisposable;

  public DetailFragment() {
    // Required empty public constructor
  }

  @Override
  public void onAttach(Context context) {
    setupDagger();
    super.onAttach(context);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    mainActivity.showBackInActionBar();
    View view = inflater.inflate(R.layout.fragment_detail, container, false);
    ButterKnife.bind(this, view);
    setupFragment();
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    showIntroByDefault();
    // Update thumbnails
    adapter.clearThumbnails();
    thumbnailsDisposable = viewModel.getStepThumbnailStream()
        .subscribe(adapter::updateThumbnail, throwable -> {
          throw new RuntimeException(throwable);
        });
  }

  @Override
  public void onPause() {
    super.onPause();
    if (thumbnailsDisposable != null) {
      thumbnailsDisposable.dispose();
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (!disposable.isDisposed()) {
      disposable.dispose();
    }
  }

  private void setupDagger() {
    if (getActivity() == null) {
      throw new AssertionError();
    }
    detailFragmentComponent = DaggerDetailFragmentComponent.builder()
        .mainActivityComponent(((MainActivity) getActivity()).getMainActivityComponent())
        .fragment(this)
        .build();
    detailFragmentComponent.inject(this);
  }

  private void setupFragment() {
    // Setup cooking steps list
    cookingSteps.setLayoutManager(detailFragmentComponent.layoutManager());
    cookingSteps.setAdapter(adapter);
    cookingSteps.addItemDecoration(divider);
    // Subscribe
    disposable.add(viewModel.getDetailStream()
        // Set activity title
        .doOnNext(recipeDetails -> mainActivity.setTitle(recipeDetails.getName()))
        .subscribe(adapter::setDetailItem, throwable -> {
          throw new RuntimeException(throwable);
        }));
    disposable.add(viewModel.getDetailSelectionStream()
        .subscribe(adapter::updateSelection, throwable -> {
          throw new RuntimeException(throwable);
        }));
  }

  /** Display intro after recipe is opened in two pane mode. */
  private void showIntroByDefault() {
    final boolean twoPaneMode = mainActivity.findViewById(R.id.step_fragment_container) != null;
    if (twoPaneMode) {
      Fragment fragment = mainActivity.getSupportFragmentManager().findFragmentById(R.id.step_fragment_container);
      if (fragment == null && adapter.stepClickCallback != null) {
        adapter.stepClickCallback.onClick(viewModel.getRecipePosition(), 0);
      }
    }
  }
}
