package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.service.*;
import de.uniks.stp24.utils.ResponseConstants;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Map;
import java.util.ResourceBundle;

/*
doesn't need @Controller annotation!
contains important @Inject annotations
provides some useful methods
*/
public class BasicController {
    @Inject
    App app;
    @Inject
    PrefService prefService;
    @Inject
    LanguageService languageService;
    @Inject
    @Resource
    public ResourceBundle resources;
    @Inject
    ErrorService errorService;
    @Inject
    ResponseConstants responseConstants;
    @Inject
    Subscriber subscriber;
    @Inject
    ImageCache imageCache;
    @Inject
    TokenStorage tokenStorage;

    public Map<Integer,String> controlResponses;

    @Inject
    public BasicController() {}

    // check if one or more string aren't empty or blank
    public boolean checkIt(String @NotNull ... texts) {
        if (texts.length == 0) return false;
        boolean cond = true;
        for (String str : texts) {
            cond &= checkIfInputNotBlankOrEmpty(str);
        }
        return cond;
    }

    private boolean checkIfInputNotBlankOrEmpty(@NotNull String text) {
        return (!text.isBlank() && !text.isEmpty());
    }

    // look for a text (info or error) in a respective dictionary
    public String getErrorInfoText(Map<Integer,String> map, int code) {
        if (map == null || map.isEmpty())
            return resources.getString(responseConstants.resStdText.getOrDefault(code, "no.dict"));
        return resources.getString(
          map.getOrDefault(code, "no.entry.dict")
        );
    }

    public String getErrorInfoText(int code) {
        return getErrorInfoText(this.controlResponses,code);
    }

    /*
    use this methode when making a request like with
     subscriber.subscribe(method,
                          result -> {...},
                          error -> {
                          ...getErrorInfoText(error);
                          ...});
     */
    public String getErrorInfoText(Throwable error) {
        int code = errorService.getStatus(error);
        return getErrorInfoText(this.controlResponses,code);
    }

}
