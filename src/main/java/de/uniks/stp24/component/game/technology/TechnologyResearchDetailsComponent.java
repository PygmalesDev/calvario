package de.uniks.stp24.component.game.technology;

import de.uniks.stp24.dto.EffectDto;
import de.uniks.stp24.model.Effect;
import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.model.Trait;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.rest.PresetsApiService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.TechnologyService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ResourceBundle;

@Component(view = "TechnologyResearchDetails.fxml")
public class TechnologyResearchDetailsComponent extends VBox {

    @FXML
    public Label base;
    @FXML
    public VBox costVBox;
    @FXML
    public Label total;

    @Inject
    Subscriber subscriber;

    @Inject
    PresetsApiService presetsApiService;

    @Inject
    TechnologyService technologyService;

    @Inject
    EmpireApiService empireApiService;

    @Inject
    TokenStorage tokenStorage;

    @Inject
    @Named("technologiesResourceBundle")
    public ResourceBundle technologiesResourceBundle;

    @Inject
    @Named("variablesResourceBundle")
    public ResourceBundle variablesResourceBundle;

    ObservableList<Trait> myTraits = FXCollections.observableArrayList();

    @Inject
    public TechnologyResearchDetailsComponent() {

    }

    @OnRender
    public void render() {

    }

    @OnInit
    public void init() {
        subscriber.subscribe(empireApiService.getEmpire(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
                empireDto -> {
                    if (empireDto.traits() != null) {
                        for (String traitName : empireDto.traits()) {
                            subscriber.subscribe(presetsApiService.getTrait(traitName),
                                    trait -> myTraits.add(trait),
                                    error -> System.out.println("Error on getting traits: " + error.getMessage())
                            );
                        }
                    }
                }
        );
    }

    @OnDestroy
    public void destroy() {

    }

    public void setTechnologyInfos(TechnologyExtended technology) {

        base.setText("Base: " + technology.cost());

        total.setText("Total: " + technology.cost());

        for (Trait trait : myTraits) {
            for (EffectDto effect : trait.effects()) {
                if (effect.variable().contains(technology.id())) {
                    Label l = new Label(((int) (effect.multiplier() * 100) - 100) + " % "
                            + variablesResourceBundle.getString(trait.id()));
                    costVBox.getChildren().add(l);
                }
            }
        }

        for (TechnologyExtended tech : technologyService.getUnlockedTechnologies()) {
            for (Effect effect : tech.effects()) {
                if (effect.variable().contains(technology.id())) {
                    Label l = new Label((1 - (int) (effect.multiplier() * 100)) + " % "
                            + technologiesResourceBundle.getString(tech.id()));
                    costVBox.getChildren().add(l);
                }
            }
        }
    }
}