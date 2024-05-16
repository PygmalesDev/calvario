package de.uniks.stp24.service;

import de.uniks.stp24.App;
import org.fulib.fx.annotation.controller.Resource;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Locale;
import java.util.ResourceBundle;


public class LanguageService {

    public PrefService prefService;
    @Inject
    @Resource
    ResourceBundle resources;
    @Inject
    Provider<ResourceBundle> newResources;
    public App app;

    @Inject
    public LanguageService(App app, PrefService prefService) {
        this.app = app;
        this.prefService = prefService;
    }

    public void setLocale(Locale locale) {
        prefService.setLocale(locale);
        resources = newResources.get();
        app.refresh();
    }
}
