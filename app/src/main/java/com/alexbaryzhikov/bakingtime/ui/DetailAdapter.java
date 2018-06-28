package com.alexbaryzhikov.bakingtime.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexbaryzhikov.bakingtime.R;
import com.alexbaryzhikov.bakingtime.datamodel.view.DetailItem;
import com.alexbaryzhikov.bakingtime.di.scopes.DetailFragmentScope;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

@DetailFragmentScope
public class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private static final int TYPE_INGREDIENTS = 0;
  private static final int TYPE_STEP = 1;

  @Inject StepClickCallback stepClickCallback;
  private DetailItem detailItem;
  private int selectedPosition = 1;

  @Inject
  DetailAdapter() {
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
      ((IngredientsViewHolder) holder).recipeIngredients.setText(detailItem.getIngredients());
    } else if (holder instanceof StepViewHolder) {
      ((StepViewHolder) holder).stepDescription.setText(detailItem.getSteps().get(position - 1));
      ((StepViewHolder) holder).selectionMark.setVisibility(position == selectedPosition ? View.VISIBLE : View.INVISIBLE);
    }
  }

  @Override
  public int getItemCount() {
    if (detailItem == null) {
      return 0;
    }
    return detailItem.getSteps().size() + 1;
  }

  @Override
  public int getItemViewType(int position) {
    return position == 0 ? TYPE_INGREDIENTS : TYPE_STEP;
  }

  public Disposable subscribeTo(Observable<DetailItem> observable) {
    return observable.subscribe(this::setDetailItem,
        throwable -> { throw new RuntimeException(throwable); });
  }

  public Disposable subscribeSelection(Observable<Integer> observable) {
    return observable.subscribe(this::updateSelection,
        throwable -> { throw new RuntimeException(throwable); });
  }

  private void setDetailItem(DetailItem detailItem) {
    this.detailItem = detailItem;
    notifyDataSetChanged();
  }

  private void updateSelection(int position) {
    notifyItemChanged(selectedPosition);
    selectedPosition = position + 1;
    notifyItemChanged(selectedPosition);
  }

  public interface StepClickCallback {
    void onClick(int recipePosition, int stepPosition);
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
    @BindView(R.id.selection_mark) View selectionMark;

    StepViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      // Open step details
      stepDescription.setOnClickListener(v -> {
        if (stepClickCallback != null) {
          stepClickCallback.onClick(detailItem.getPosition(), getAdapterPosition() - 1);
        }
      });
    }
  }
}
