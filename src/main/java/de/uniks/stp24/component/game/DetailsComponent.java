package de.uniks.stp24.component.game;

import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.ResourcesService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.uniks.stp24.service.Constants.resourceImagePath;

@Component(view = "Details.fxml")
public class DetailsComponent extends AnchorPane {

    @FXML
    public Button showConsumption;
    @FXML
    public Button showProduction;
    @FXML
    public Pane res1Pic;
    @FXML
    public Text amount1;
    @FXML
    public Pane res2Pic;
    @FXML
    public Text amount2;
    @FXML
    public Pane res3Pic;
    @FXML
    public Text amount3;
    @FXML
    public Pane res4Pic;
    @FXML
    public Text amount4;
    @FXML
    public Pane res5Pic;
    @FXML
    public Text amount5;
    @FXML
    public Pane res6Pic;
    @FXML
    public Text amount6;
    @FXML
    public Pane res7Pic;
    @FXML
    public Text amount7;
    @FXML
    public Pane res8Pic;
    @FXML
    public Text amount8;
    @FXML
    public Pane res9Pic;
    @FXML
    public Text amount9;
    @FXML
    public Text title;
    @Inject
    InGameService inGameService;
    @Inject
    ResourcesService resourcesService;
    @Inject
    public IslandAttributeStorage islandAttributes;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    Subscriber subscriber;

    List<Pane> resImages = new ArrayList<>();
    List<Text> resInf = new ArrayList<>();

    @Inject
    public DetailsComponent(){

    }

    public void setSumProduction(Map<String, Integer> totalProduction){
        int i = 0;
        for (Map.Entry<String, Integer> entry : totalProduction.entrySet()) {
            String imageStyle = resourceImagePath.get(entry.getKey());
            resImages.get(i).setStyle(imageStyle + "-fx-background-size: cover;");
            resInf.get(i).setText("×" + entry.getValue());
            i ++;
        }
        title.setText("     Total Production");
    }

    public void setSumConsumption(Map<String, Integer> totalConsumption){
        int i = 0;
        for (Map.Entry<String, Integer> entry : totalConsumption.entrySet()) {
            String imageStyle = resourceImagePath.get(entry.getKey());
            resImages.get(i).setStyle(imageStyle + "-fx-background-size: cover;");
            resInf.get(i).setText("×" + entry.getValue());
            i++;
        }
        title.setText("  Total Consumption");
    }

    public void setResLists(){
        resImages.add(res1Pic);
        resImages.add(res2Pic);
        resImages.add(res3Pic);
        resImages.add(res4Pic);
        resImages.add(res5Pic);
        resImages.add(res6Pic);
        resImages.add(res7Pic);
        resImages.add(res8Pic);
        resImages.add(res9Pic);

        resInf.add(amount1);
        resInf.add(amount2);
        resInf.add(amount3);
        resInf.add(amount4);
        resInf.add(amount5);
        resInf.add(amount6);
        resInf.add(amount7);
        resInf.add(amount8);
        resInf.add(amount9);

        for(int i = 0; i < 9; i++){
            resInf.get(i).setStyle("-fx-font-family: 'Tempus Sans ITC';" +
                    "-fx-background-color: 'TRANSPARENT';" +
                    "-fx-font-weight: none;" +
                    "-fx-font-size: 12;");
        }
        title.setStyle("-fx-font-family: 'Blackadder ITC';" +
                "-fx-background-color: 'TRANSPARENT';" +
                "-fx-font-weight: none;" +
                "-fx-font-size: 22;");

        showProduction.setVisible(false);
        showConsumption.setVisible(true);
    }

    public void showConsumption(){
        showProduction.setVisible(true);
        showConsumption.setVisible(false);
        setSumConsumption(islandAttributes.mergeConsumption());
    }

    public void showProduction(){
        showProduction.setVisible(false);
        showConsumption.setVisible(true);
        setSumProduction(islandAttributes.mergeProduction());

    }
}
