package com.alexbaryzhikov.bakingtime;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class RecyclerViewMatcher {

  private final int recyclerViewId;

  private RecyclerViewMatcher(int recyclerViewId) {
    this.recyclerViewId = recyclerViewId;
  }

  public static RecyclerViewMatcher byId(final int recyclerViewId) {
    return new RecyclerViewMatcher(recyclerViewId);
  }

  public Matcher<View> atPosition(final int position) {
    return atPositionOnView(position, -1);
  }

  public Matcher<View> atPositionOnView(final int position, final int targetViewId) {
    return new TypeSafeMatcher<View>() {

      Resources resources = null;
      View childView;

      public void describeTo(Description description) {
        String idDescription = Integer.toString(recyclerViewId);

        if (this.resources != null) {
          try {
            idDescription = this.resources.getResourceName(recyclerViewId);
          } catch (Resources.NotFoundException e) {
            idDescription = String.format("%s (resource name not found)", recyclerViewId);
          }
        }

        description.appendText("RecyclerView with id: " + idDescription + " at position: " + position);
      }

      public boolean matchesSafely(View view) {
        this.resources = view.getResources();

        if (childView == null) {
          RecyclerView recyclerView = view.getRootView().findViewById(recyclerViewId);
          if (recyclerView != null && recyclerView.getId() == recyclerViewId) {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
            if (viewHolder != null) {
              childView = viewHolder.itemView;
            }
          } else {
            return false;
          }
        }

        if (targetViewId == -1) {
          return view == childView;
        } else {
          if (childView == null) {
            throw new IllegalStateException("Could not acquire child view");
          }
          View targetView = childView.findViewById(targetViewId);
          return view == targetView;
        }
      }
    };
  }
}
