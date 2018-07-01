package com.alexbaryzhikov.bakingtime.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.alexbaryzhikov.bakingtime.BakingApp;
import com.alexbaryzhikov.bakingtime.R;
import com.alexbaryzhikov.bakingtime.datamodel.view.BrowseItem;
import com.alexbaryzhikov.bakingtime.di.components.BrowseFragmentComponent;
import com.alexbaryzhikov.bakingtime.di.components.DaggerBrowseFragmentComponent;
import com.alexbaryzhikov.bakingtime.di.components.MainActivityComponent;
import com.alexbaryzhikov.bakingtime.utils.NetworkResource;
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

  @Inject BrowseAdapter adapter;
  @Inject RecipeViewModel viewModel;
  @Inject MainActivity mainActivity;

  private BrowseFragmentComponent browseFragmentComponent;
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
    mainActivity.showBackInActionBar();
    mainActivity.clearStepFragment();
    View view = inflater.inflate(R.layout.fragment_browse, container, false);
    ButterKnife.bind(this, view);
    viewModel.init();
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

  private void setupDagger() {
    if (getActivity() == null) {
      throw new AssertionError();
    }
    browseFragmentComponent = DaggerBrowseFragmentComponent.builder()
        .mainActivityComponent(((MainActivity) getActivity()).getMainActivityComponent())
        .fragment(this)
        .build();
    browseFragmentComponent.inject(this);
  }

  private void setupFragment() {
    // Set activity title
    mainActivity.setTitle(getString(R.string.app_name));
    // Setup refresh button
    refreshButton.setOnClickListener(v -> viewModel.emitBrowse());
    // Setup recipes list
    final RecyclerView.LayoutManager layoutManager = browseFragmentComponent.layoutManager();
    recipeList.setLayoutManager(layoutManager);
    recipeList.setAdapter(adapter);
    // Subscribe to recipes stream
    if (adapter.getItemCount() == 0) {
      disposable = adapter.subscribeTo(viewModel.getBrowseStream()
          .doOnNext(this::renderNetworkStatus)
          .map(this::stripNetworkStatus));
    }
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

  private List<BrowseItem> stripNetworkStatus(NetworkResource<List<BrowseItem>> networkResource) {
    List<BrowseItem> recipeItems = networkResource.getData();
    if (recipeItems == null) {
      throw new AssertionError("NetworkResource with null data");
    }
    return recipeItems;
  }
}
