package com.alexbaryzhikov.bakingtime.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexbaryzhikov.bakingtime.R;
import com.alexbaryzhikov.bakingtime.datamodel.response.Step;
import com.alexbaryzhikov.bakingtime.di.scopes.DetailFragmentScope;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

@DetailFragmentScope
public class StepsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private static final int TYPE_INGREDIENTS = 0;
  private static final int TYPE_STEP = 1;

  private String ingredients;
  private List<Step> steps;

  @Inject
  StepsAdapter() {
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    if (viewType == TYPE_INGREDIENTS) {
      View view = inflater.inflate(R.layout.detail_ingredients_item, parent, false);
      return new IngredientsViewHolder(view);
    } else if (viewType == TYPE_STEP) {
      View view = inflater.inflate(R.layout.detail_step_item, parent, false);
      return new StepViewHolder(view);
    }
    throw new IllegalArgumentException("Unknown view type: " + viewType);
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof IngredientsViewHolder) {
      ((IngredientsViewHolder) holder).recipeIngredients.setText(ingredients);
    } else if (holder instanceof StepViewHolder) {
      ((StepViewHolder) holder).stepDescription.setText(steps.get(position - 1).description);
    }
  }

  @Override
  public int getItemCount() {
    if (steps == null) {
      return 0;
    }
    return steps.size() + 1;
  }

  @Override
  public int getItemViewType(int position) {
    return position == 0 ? TYPE_INGREDIENTS : TYPE_STEP;
  }

  public void setIngredients(String ingredients) {
    this.ingredients = ingredients;
  }

  public void setSteps(List<Step> steps) {
    this.steps = steps;
  }

  class IngredientsViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.recipe_ingredients) TextView recipeIngredients;

    IngredientsViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  class StepViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.step_description) TextView stepDescription;

    StepViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
