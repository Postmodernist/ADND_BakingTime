package com.alexbaryzhikov.bakingtime.di.modules;

import com.alexbaryzhikov.bakingtime.api.RecipeApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
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
  Retrofit provideRetrofit() {
    return new Retrofit.Builder()
        .baseUrl("http://go.udacity.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
  }

}
