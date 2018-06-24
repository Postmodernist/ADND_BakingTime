package com.alexbaryzhikov.bakingtime.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class NetworkResource<T> {

  @NonNull private final Status status;
  @Nullable private final T data;

  private NetworkResource(@NonNull Status status, @Nullable T data) {
    this.status = status;
    this.data = data;
  }

  public static <T> NetworkResource<T> loading(@Nullable T data) {
    return new NetworkResource<>(Status.LOADING, data);
  }

  public static <T> NetworkResource<T> success(@NonNull T data) {
    return new NetworkResource<>(Status.SUCCESS, data);
  }

  public static <T> NetworkResource<T> error(@Nullable T data) {
    return new NetworkResource<>(Status.ERROR, data);
  }

  @NonNull
  public Status getStatus() {
    return status;
  }

  @Nullable
  public T getData() {
    return data;
  }

  public enum Status {LOADING, SUCCESS, ERROR}
}
