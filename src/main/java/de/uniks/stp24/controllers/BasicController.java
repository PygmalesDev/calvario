package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.service.ErrorService;
import de.uniks.stp24.service.LanguageService;
import de.uniks.stp24.service.PrefService;
import de.uniks.stp24.utils.ErrorTextWriter;
import de.uniks.stp24.utils.ResponseConstants;
import org.fulib.fx.annotation.controller.Resource;

import javax.inject.Inject;
import java.util.Map;
import java.util.ResourceBundle;

// doesn't need @Controller annotation!
// contains important @Inject annotations
// provides some useful methods
public class BasicController {
    @Inject
    App app;
    @Inject
    PrefService prefService;
    @Inject
    LanguageService languageService;
    @Inject
    @Resource
    ResourceBundle resources;
    @Inject
    ErrorService errorService;
    @Inject
    ResponseConstants responseConstants;


    @Inject
    public BasicController() {}

    public boolean checkIt(String text) {
        return checkIfInputNotBlankOrEmpty(text);
    }

    private boolean checkIfInputNotBlankOrEmpty(String text) {
        return (!text.isBlank() && !text.isEmpty());
    }

    public String getErrorInfoText(Map<Integer,String> map, int code) {
        return resources.getString(
                new ErrorTextWriter(map,code).getErrorText()
        );
    }

}
