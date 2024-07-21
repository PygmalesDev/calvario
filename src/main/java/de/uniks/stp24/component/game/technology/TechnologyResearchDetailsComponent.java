package de.uniks.stp24.component.game.technology;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ResourceBundle;

@Component(view = "TechnologyResearchDetails.fxml")
public class TechnologyResearchDetailsComponent extends VBox {

    @FXML
    public Label base;
    @FXML
    public Label multiplier;
    @FXML
    public VBox costVBox;
    @FXML
    public Label total;

    @Inject
    @Named("technologiesResourceBundle")
    public ResourceBundle technologiesResourceBundle;


    @Inject
    public TechnologyResearchDetailsComponent() {

    }

    @OnRender
    public void render() {

    }

    @OnInit
    public void init() {

    }

    @OnDestroy
    public void destroy() {

    }

//    public void setTechnologyInfos(TechnologyExtended technology) {
//        // TODO: ADD FOR EVERY EFFECT THAT AFFECTS THE CURRENT TECHNOLOGY A NEW LABEL AN ADD IT TO COSTVBOX
//        //  SEARCH THE VARIABLE WHICH GIVES YOU THE DISCOUNT (COULD BE TRAIT OR TECHNOLOGIES...)
//        for (Effect effect : technology.effects()) {
//
//            base.setText("Base: " + technology.cost());
//
//            if (effect.multiplier() > 0) {
//                // TODO: Replace the String after the % with the name of the trait or technology that gives your
//                //  the disocunt
//                multiplier.setText((((int) (effect.multiplier() * 100) - 100) + " % " + technologiesResourceBundle.getString(technology.id())));
//            }
//
//            total.setText("Total: " + ((int) (technology.cost() * effect.multiplier())));
//        }
//}

}
