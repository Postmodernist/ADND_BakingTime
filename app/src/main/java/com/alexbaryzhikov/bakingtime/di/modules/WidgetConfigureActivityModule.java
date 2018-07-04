package com.alexbaryzhikov.bakingtime.di.modules;

import com.alexbaryzhikov.bakingtime.api.RecipeApi;
import com.alexbaryzhikov.bakingtime.di.scopes.WidgetConfigureActivityScope;
import com.alexbaryzhikov.bakingtime.repositiory.Repository;

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
public class WidgetConfigureActivityModule {

  @Provides
  @WidgetConfigureActivityScope
  Repository provideRepository(RecipeApi recipeApi) {
    return new Repository(recipeApi);
  }

  @Provides
  @WidgetConfigureActivityScope
  RecipeApi provideMoviesApi(Retrofit retrofit) {
    return retrofit.create(RecipeApi.class);
  }

  @Provides
  @WidgetConfigureActivityScope
  Retrofit provideRetrofit(@Named("IoScheduler") Scheduler scheduler) {
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
