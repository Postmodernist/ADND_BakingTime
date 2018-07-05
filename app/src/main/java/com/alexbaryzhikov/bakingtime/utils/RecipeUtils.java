package com.alexbaryzhikov.bakingtime.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;

import com.alexbaryzhikov.bakingtime.R;
import com.alexbaryzhikov.bakingtime.datamodel.response.Ingredient;
import com.alexbaryzhikov.bakingtime.datamodel.response.Step;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.reactivex.plugins.RxJavaPlugins;
import retrofit2.Response;
import retrofit2.adapter.rxjava2.Result;

import static android.media.MediaMetadataRetriever.OPTION_CLOSEST;

public final class RecipeUtils {

  private RecipeUtils() {  // Utility class
  }

  @NonNull
  public static Drawable buildThumbnail(@NonNull Context context, @NonNull Step step) {
    // Get thumbnail URL either from thumbnailURL field, or videoURL field
    String thumbnailUrl = step.thumbnailURL.isEmpty() ? step.videoURL : step.thumbnailURL;
    if (thumbnailUrl.isEmpty()) {
      return context.getResources().getDrawable(R.drawable.thumb_video);
    } else {
      Bitmap thumbBmp = getFrameFromVideoUrl(thumbnailUrl);
      if (thumbBmp == null) {
        return context.getResources().getDrawable(R.drawable.thumb_video);
      }
      return new BitmapDrawable(context.getResources(), thumbBmp);
    }
  }

  private static Bitmap getFrameFromVideoUrl(@NonNull String videoUrl) {
    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
    mediaMetadataRetriever.setDataSource(videoUrl, new HashMap<>());
    Bitmap frame = mediaMetadataRetriever.getFrameAtTime(1, OPTION_CLOSEST);
    mediaMetadataRetriever.release();
    return frame;
  }

  public static List<String> buildDescriptions(@NonNull Context context, @NonNull List<Step> steps) {
    List<String> descriptions = new ArrayList<>(steps.size());
    descriptions.add(steps.get(0).shortDescription);  // do not append index to introduction step
    for (int i = 1; i < steps.size(); i++) {
      String step = context.getString(R.string.step_fmt,
          i, steps.get(i).shortDescription);
      descriptions.add(step);
    }
    return Collections.unmodifiableList(descriptions);
  }

  public static String buildIngredientsSummary(@NonNull Context context, @NonNull List<Ingredient> ingredients) {
    StringBuilder sb = new StringBuilder();
    for (Ingredient ingredient : ingredients) {
      sb.append(context.getString(R.string.ingredient_fmt,
          readableValueOf(ingredient.quantity, ingredient.measure), ingredient.ingredient));
    }
    return sb.substring(0, sb.length() - 1);
  }

  private static String readableValueOf(Double d, String measure) {
    String quantity = d % 1 == 0 ? String.valueOf(d.intValue()) : String.valueOf(d);
    return quantity + humanReadableMeasure(measure, d.compareTo(1.0) == 0);
  }

  private static String humanReadableMeasure(String measure, boolean single) {
    switch (measure) {
      case "CUP":
        return single ? " cup of" : " cups of";
      case "TBLSP":
        return " tbs. of";
      case "TSP":
        return " tsp. of";
      case "G":
        return " g of";
      case "K":
        return " kg of";
      case "OZ":
        return " oz of";
      case "UNIT":
        return "";
      default:
        return " " + measure + " of";
    }
  }

  /** Convert retrofit Result to NewworkResource */
  public static <T, U> NetworkResource<List<U>> toNetworkResource(
      @NonNull Result<List<T>> result, @NonNull Transformer<List<T>, List<U>> transformer) {

    final NetworkResource<List<U>> ERROR_NETWORK_RESOURCE = NetworkResource.error(Collections.emptyList());

    // Handle IO errors and propagate others
    if (result.isError()) {
      if (!IOException.class.isInstance(result.error())) {
        RxJavaPlugins.onError(result.error());
      }
      return ERROR_NETWORK_RESOURCE;
    }

    // Get response
    final Response<List<T>> response = result.response();
    if (response == null) {
      // Should never happen. Response can be null only if result.isError() is true
      RxJavaPlugins.onError(new AssertionError("Response is null"));
      return ERROR_NETWORK_RESOURCE;
    }

    // Get response body
    final List<T> body = response.body();
    // Return error if response is not OK (legit, we don't own the server)
    // or response body is null (should never happen)
    if (!response.isSuccessful() || body == null) {
      return ERROR_NETWORK_RESOURCE;
    }
    final List<T> bodyCopy = new ArrayList<>(body);
    // Convert and wrap in NetworkResource
    List<U> transformedBody = transformer.apply(bodyCopy);
    return NetworkResource.success(transformedBody);
  }

  /** Strip NetworkResource wrapper */
  public static <T> List<T> stripNetworkStatus(NetworkResource<List<T>> networkResource) {
    List<T> recipeItems = networkResource.getData();
    if (recipeItems == null) {
      throw new AssertionError("NetworkResource with null data");
    }
    return recipeItems;
  }

  @FunctionalInterface
  public interface Transformer<T, U> {
    U apply(T t);
  }
}
