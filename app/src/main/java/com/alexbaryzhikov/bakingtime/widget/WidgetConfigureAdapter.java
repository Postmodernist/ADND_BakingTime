package com.alexbaryzhikov.bakingtime.widget;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexbaryzhikov.bakingtime.R;
import com.alexbaryzhikov.bakingtime.di.scopes.WidgetConfigureActivityScope;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@WidgetConfigureActivityScope
public class WidgetConfigureAdapter extends RecyclerView.Adapter<WidgetConfigureAdapter.RecipeViewHolder> {

  private RecipeClickCallback clickCallback;
  private List<WidgetConfigureItem> items;

  WidgetConfigureAdapter(RecipeClickCallback clickCallback) {
    this.clickCallback = clickCallback;
  }

  @NonNull
  @Override
  public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.widget_configure_list_item, parent, false);
    return new RecipeViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
    holder.recipeTitle.setText(items.get(position).getName());
  }

  @Override
  public int getItemCount() {
    if (items == null) {
      return 0;
    }
    return items.size();
  }

  public void setItems(List<WidgetConfigureItem> items) {
    this.items = items;
    notifyDataSetChanged();
  }

  interface RecipeClickCallback {
    void onClick(int position, WidgetConfigureItem item);
  }

  class RecipeViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_container) ViewGroup itemContainer;
    @BindView(R.id.recipe_title) TextView recipeTitle;

    RecipeViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);

      itemContainer.setOnClickListener(v -> {
        if (clickCallback != null) {
          final int position = getAdapterPosition();
          clickCallback.onClick(position, items.get(position));
        }
      });
    }
  }
}
