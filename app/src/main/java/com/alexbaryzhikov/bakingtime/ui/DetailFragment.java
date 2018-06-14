package com.alexbaryzhikov.bakingtime.ui;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexbaryzhikov.bakingtime.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

  private static final String KEY_RECIPE_ID = "recipe-id";
  private static final String KEY_POSITION = "position";

  public DetailFragment() {
    // Required empty public constructor
  }

  static DetailFragment forRecipe(int recipeId, int position) {
    DetailFragment fragment = new DetailFragment();
    Bundle args = new Bundle();
    args.putInt(KEY_RECIPE_ID, recipeId);
    args.putInt(KEY_POSITION, position);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_detail, container, false);
  }

}
