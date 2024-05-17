package de.uniks.stp24.service;

import de.uniks.stp24.App;
import org.fulib.fx.annotation.controller.Resource;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Locale;
import java.util.ResourceBundle;


public class LanguageService {
    @Inject
    public PrefService prefService;
    @Inject
    public Provider<ResourceBundle> newResources;

    @Inject
    public LanguageService() {
    }

    public ResourceBundle setLocale(Locale locale) {
        prefService.setLocale(locale);
        return newResources.get();
    }

    public void refreshApp(App app){
        app.refresh();
    }
}
