package de.uniks.stp24.component.game;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;

@Component(view = "sites.fxml")
public class SitesComponent extends VBox {

    @Inject
    public SitesComponent(){

    }

}
