package com.alexbaryzhikov.bakingtime.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexbaryzhikov.bakingtime.R;
import com.alexbaryzhikov.bakingtime.datamodel.RecipeItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

  private List<RecipeItem> recipeData;

  @NonNull
  @Override
  public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.browse_list_item, parent, false);
    return new RecipeViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
    holder.recipeTitle.setText(recipeData.get(position).getTitle());
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

  class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.recipe_title) TextView recipeTitle;

    RecipeViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      // TODO Card onClick callback
    }
  }
}
