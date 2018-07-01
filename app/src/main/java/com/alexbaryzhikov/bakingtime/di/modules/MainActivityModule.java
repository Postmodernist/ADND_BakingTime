package com.alexbaryzhikov.bakingtime.di.modules;

import android.util.Log;

import com.alexbaryzhikov.bakingtime.api.RecipeApi;
import com.alexbaryzhikov.bakingtime.di.scopes.MainActivityScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.alexbaryzhikov.bakingtime.api.RecipeApiConstants.BASE_URL;

@Module
public class MainActivityModule {

  @Provides
  @MainActivityScope
  RecipeApi provideMoviesApi(Retrofit retrofit) {
    return retrofit.create(RecipeApi.class);
  }

  @Provides
  @MainActivityScope
  Retrofit provideRetrofit(@Named("IoScheduler") Scheduler scheduler) {
    Log.d("TAG", "provideRetrofit: BASE_URL = " + BASE_URL);
    return new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(scheduler))
        .build();
  }

  @Provides
  @Named("IoScheduler")
  Scheduler provideScheduler() {
    return Schedulers.io();
  }
}
