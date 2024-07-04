package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.rest.PresetsApiService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.ResourcesService;
import de.uniks.stp24.ws.EventListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@Component(view = "MarketComponent.fxml")
public class MarketComponent extends StackPane {
    @FXML
    Button closeMarketOverviewButton;
    @FXML
    Label userCredits;
    @FXML
    Label marketFee;
    @FXML
    Label numberOfGoods;
    @FXML
    Label priceTag;
    @FXML
    Button everySeasonButton;
    @FXML
    Button sellButton;
    @FXML
    Button buyButton;
    @FXML
    Button incrementNumberOfGoods;
    @FXML
    Button decrementNumberOfGoods;
    @FXML
    ImageView creditIconImageView;
    @FXML
    ImageView selectedIconImage;
    @FXML
    public ListView<Map.Entry<String, Integer>> resourcesListView;

    @Inject
    App app;
    @Inject
    Subscriber subscriber;
    @Inject
    ResourcesService resourcesService;
    @Inject
    EmpireService empireService;
    @Inject
    EventListener eventListener;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    ImageCache imageCache;
    @Inject
    @org.fulib.fx.annotation.controller.Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;
    @Inject
    PresetsApiService presetsApiService;

    private InGameController inGameController;
    private String lastUpdate;
    private String lastSeasonUpdate;
    Provider<MarketResourceComponent> marketResourceComponentProvider = () -> new MarketResourceComponent(true, true, true, gameResourceBundle);

    Map<String, Integer> variables = new HashMap<>();

    Map<String, Integer> resourceMap = new HashMap<>();
    Map<String, Integer> creditsMap = new HashMap<>();

    @Inject
    public MarketComponent() {
        lastUpdate = "";
        lastSeasonUpdate = "";
    }

    @OnInit
    public void init() {
        subscriber.subscribe(presetsApiService.getVariables(),
                res -> {
                    variables = res;
                    System.out.println("imran");
                    System.out.println(variables);
                    System.out.println(variables.get("empire.market.fee"));
                    marketFee.setText(String.valueOf(res.get("empire.market.fee")));
                }
        );
    }

    // Optional: A method to retrieve data from the map


    @OnRender
    public void render() {
        System.out.println(variables);
        setRessourceAmount();

    }

    private void setRessourceAmount() {

    }


    public void setInGameController(InGameController ingameController) {
        this.inGameController = ingameController;
    }

    public void closeMarketOverview() {
        this.getParent().setVisible(false);
    }

    public void incrementAmount() {
        int amount = Integer.parseInt(numberOfGoods.getText());
        amount++;
        numberOfGoods.setText(String.valueOf(amount));
    }

    public void decrementAmount() {
        int amount = Integer.parseInt(numberOfGoods.getText());
        amount--;
        numberOfGoods.setText(String.valueOf(amount));
    }


    public void filterResourceMap() {
        resourceMap.remove("population");
        resourceMap.remove("research");
    }

    public void separateCredits() {
        if (resourceMap.containsKey("credits")) {
            creditsMap.put("credits", resourceMap.get("credits"));
            resourceMap.remove("credits");
        }
        creditIconImageView.setImage(imageCache.get("/de/uniks/stp24/icons/resources/credits.png"));
        userCredits.setText(String.valueOf(creditsMap.get("credits")));
    }

    public void listMarketResources(Map<String, Integer> resourceMap) {
        this.resourceMap = resourceMap;
        filterResourceMap();
        separateCredits();
        if (this.resourceMap.isEmpty()) {
            System.out.println("resourceMap is empty");
        }
        resourcesListView.getItems().addAll(this.resourceMap.entrySet());
        resourcesListView.setCellFactory(list -> new ResourceCell());
    }

    public class ResourceCell extends ListCell<Map.Entry<String, Integer>> {
        private VBox vBox = new VBox();
        private ImageView imageView = new ImageView();
        private Text text = new Text();
        ImageCache imageCache = new ImageCache();


        public ResourceCell() {
            super();
            vBox.getChildren().addAll(imageView, text);
        }

        @Override
        protected void updateItem(Map.Entry<String, Integer> item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                imageView.setImage(imageCache.get("/de/uniks/stp24/assets/market/buttons/" + item.getKey() + ".png"));
                imageView.setFitWidth(35);
                imageView.setFitHeight(35);

                text.setText(String.valueOf(item.getValue()));
                setGraphic(vBox);
            }

            resourcesListView.setOnMouseClicked(event -> {
                selectedIconImage.setImage(imageCache.get("/de/uniks/stp24/icons/resources/" + item.getKey() + ".png"));
                numberOfGoods.setText(String.valueOf(item.getValue()));
            });
        }
    }
}