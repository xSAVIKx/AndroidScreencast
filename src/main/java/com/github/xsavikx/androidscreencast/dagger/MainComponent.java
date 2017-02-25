package com.github.xsavikx.androidscreencast.dagger;

import com.github.xsavikx.androidscreencast.api.command.executor.CommandExecutor;
import com.github.xsavikx.androidscreencast.app.Application;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {ApiModule.class, AppModule.class, UiModule.class})
public interface MainComponent {
    Application application();

    CommandExecutor commandExecutor();
}
