package com.alexbaryzhikov.bakingtime.di.components;

import com.alexbaryzhikov.bakingtime.di.modules.WidgetConfigureActivityModule;
import com.alexbaryzhikov.bakingtime.di.scopes.WidgetConfigureActivityScope;
import com.alexbaryzhikov.bakingtime.widget.WidgetConfigureActivity;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = WidgetConfigureActivityModule.class)
@WidgetConfigureActivityScope
public interface WidgetConfigureActivityComponent {

  void inject(WidgetConfigureActivity activity);

  @Component.Builder
  interface Builder {

    @BindsInstance
    WidgetConfigureActivityComponent.Builder activity(WidgetConfigureActivity activity);

    WidgetConfigureActivityComponent build();
  }
}
