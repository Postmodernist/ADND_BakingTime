package com.alexbaryzhikov.bakingtime.ui;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexbaryzhikov.bakingtime.R;
import com.alexbaryzhikov.bakingtime.datamodel.view.DetailItem;
import com.alexbaryzhikov.bakingtime.datamodel.view.StepThumbnail;
import com.alexbaryzhikov.bakingtime.di.scopes.DetailFragmentScope;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

@DetailFragmentScope
public class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private static final int TYPE_INGREDIENTS = 0;
  private static final int TYPE_STEP = 1;

  @Inject StepClickCallback stepClickCallback;
  private DetailItem detailItem;
  private List<Drawable> thumbnails;
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
      StepViewHolder h = (StepViewHolder) holder;
      if (thumbnails != null && thumbnails.size() > position - 1) {
        h.thumbnail.setImageDrawable(thumbnails.get(position - 1));
      }
      h.stepDescription.setText(detailItem.getDescriptions().get(position - 1));
      h.selectionMark.setVisibility(position == selectedPosition ? View.VISIBLE : View.INVISIBLE);
    }
  }

  @Override
  public int getItemCount() {
    if (detailItem == null) {
      return 0;
    }
    return detailItem.getDescriptions().size() + 1;
  }

  @Override
  public int getItemViewType(int position) {
    return position == 0 ? TYPE_INGREDIENTS : TYPE_STEP;
  }

  public void setDetailItem(DetailItem detailItem) {
    this.detailItem = detailItem;
    notifyDataSetChanged();
  }

  public void clearThumbnails() {
    thumbnails = null;
  }

  public void updateThumbnail(@NonNull StepThumbnail stepThumbnail) {
    if (thumbnails == null) {
      thumbnails = new ArrayList<>();
    }
    thumbnails.add(stepThumbnail.getDrawable());
    final int position = stepThumbnail.getStepId();
    notifyItemChanged(position + 1);
  }

  public void updateSelection(int position) {
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

    @BindView(R.id.item_container) ViewGroup itemContainer;
    @BindView(R.id.thumbnail) ImageView thumbnail;
    @BindView(R.id.step_description) TextView stepDescription;
    @BindView(R.id.selection_mark) View selectionMark;

    StepViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      // Open step details
      itemContainer.setOnClickListener(v -> {
        if (stepClickCallback != null) {
          stepClickCallback.onClick(detailItem.getPosition(), getAdapterPosition() - 1);
        }
      });
    }
  }
}
