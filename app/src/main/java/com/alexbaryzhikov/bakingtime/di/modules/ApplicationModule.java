package com.alexbaryzhikov.bakingtime.di.modules;

import com.alexbaryzhikov.bakingtime.api.RecipeApi;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ApplicationModule {

  @Provides
  @Singleton
  RecipeApi provideMoviesApi(Retrofit retrofit) {
    return retrofit.create(RecipeApi.class);
  }

  @Provides
  @Singleton
  Retrofit provideRetrofit(@Named("IoScheduler") Scheduler scheduler) {
    return new Retrofit.Builder()
        .baseUrl("http://go.udacity.com/")
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
