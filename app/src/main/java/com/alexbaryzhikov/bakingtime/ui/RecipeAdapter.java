package com.alexbaryzhikov.bakingtime.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexbaryzhikov.bakingtime.R;
import com.alexbaryzhikov.bakingtime.datamodel.view.RecipeItem;
import com.alexbaryzhikov.bakingtime.di.scopes.BrowseFragmentScope;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

@BrowseFragmentScope
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

  @Inject RecipeClickCallback clickCallback;
  private List<RecipeItem> recipeData;

  @Inject
  RecipeAdapter() {
  }

  @NonNull
  @Override
  public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.browse_list_item, parent, false);
    return new RecipeViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
    holder.recipeTitle.setText(recipeData.get(position).getName());
    holder.recipeIngredients.setText(recipeData.get(position).getIngredientsStr());
  }

  @Override
  public int getItemCount() {
    if (recipeData == null) {
      return 0;
    }
    return recipeData.size();
  }

  public Disposable subscribeTo(Observable<List<RecipeItem>> observable) {
    return observable.subscribe(this::setRecipeData,
        throwable -> { throw new RuntimeException(throwable); });
  }

  private void setRecipeData(List<RecipeItem> recipeData) {
    this.recipeData = recipeData;
    notifyDataSetChanged();
  }

  public interface RecipeClickCallback {
    void onClick(int position);
  }

  class RecipeViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.recipe_title) TextView recipeTitle;
    @BindView(R.id.recipe_ingredients) TextView recipeIngredients;
    @BindView(R.id.expand_button) ImageView expandButton;

    private boolean expanded = false;

    RecipeViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);

      // Open recipe details fragment
      itemView.setOnClickListener(v -> {
        if (clickCallback != null) {
          clickCallback.onClick(getAdapterPosition());
        }
      });

      // Expand recipe ingredients
      expandButton.setOnClickListener(v -> {
        if (expanded) {
          expandButton.setImageResource(R.drawable.ic_collapsed);
          recipeIngredients.setVisibility(View.GONE);
          expanded = false;
        } else {
          expandButton.setImageResource(R.drawable.ic_expanded);
          recipeIngredients.setVisibility(View.VISIBLE);
          expanded = true;
        }
      });
    }
  }
}
