package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.model.Resource;
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
import java.util.*;

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
    Label buyingPrice;
    @FXML
    Label sellingPrice;
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
    public String selectedItem;

    Provider<MarketResourceComponent> marketResourceComponentProvider = () -> new MarketResourceComponent(true, true, true, gameResourceBundle);

    // From Server
    Map<String,Integer> variables = new HashMap<>();
    Map<String,Resource> idResourceMap = new HashMap<>();

    Map<String, Integer> resourceCountMap = new HashMap<>();
    Map<String,Integer> resourcePriceMap = new HashMap<>();

    //From Storage
    Map<String, Integer> resourceMap = new HashMap<>();
    Map<String, Integer> creditsMap = new HashMap<>();

    @Inject
    public MarketComponent() {
        lastUpdate = "";
        lastSeasonUpdate = "";
    }

    @OnInit
    public void init() {
        loadVariablesAndSetup();
        getIdResourcesMap();
//        updateResources();
    }

//    private void updateResources() {
//        this.subscriber.subscribe(eventListener
//                        .listen("games." + tokenStorage.getGameId() + ".empires." + tokenStorage.getEmpireId() + ".updated", EmpireDto.class),
//                event -> {
//                    if (!lastUpdate.equals(event.data().updatedAt())) {
//                        for (Resource resource : event.data().getResources()) {
//                            updateResource(resource);
//                        }
//                        this.lastUpdate = event.data().updatedAt();
//                    }
//                }, error -> System.out.println("errorEmpireListener"));
//    }
//
//    private void updateResource(Resource resource) {
//        resourceMap.put(resource.getName(), resource.getAmount());
//
//        if ("credits".equals(resource.getName())) {
//            creditsMap.put("credits", resource.getAmount());
//            userCredits.setText(String.valueOf(resource.getAmount()));
//        }
//
//        refreshResourceListView();
//    }
//
//    private void refreshResourceListView() {
//        resourcesListView.getItems().clear();
//        resourcesListView.getItems().addAll(resourceMap.entrySet());
//        resourcesListView.refresh();
//    }

    private void loadVariablesAndSetup() {
        subscriber.subscribe(presetsApiService.getVariables(),
                res -> {
                    this.variables = res;
                    createResourceCountMap();
                    createResourcePriceMap();
                    setMarketFee();
                    listMarketResources();
                }
        );
    }

    private void createResourceCountMap() {
        resourcePriceMap.put("energy",variables.get("resources.energy.credit_value"));
        resourcePriceMap.put("minerals",variables.get("resources.minerals.credit_value"));
        resourcePriceMap.put("food",variables.get("resources.food.credit_value"));
        resourcePriceMap.put("fuel",variables.get("resources.fuel.credit_value"));
        resourcePriceMap.put("alloys",variables.get("resources.alloys.credit_value"));
        resourcePriceMap.put("consumer_goods",variables.get("resources.consumer_goods.credit_value"));
    }

    private void createResourcePriceMap() {
        resourceCountMap.put("energy",variables.get("resources.energy.starting"));
        resourceCountMap.put("minerals",variables.get("resources.minerals.starting"));
        resourceCountMap.put("food",variables.get("resources.food.starting"));
        resourceCountMap.put("fuel",variables.get("resources.fuel.starting"));
        resourceCountMap.put("alloys",variables.get("resources.alloys.starting"));
        resourceCountMap.put("consumer_goods",variables.get("resources.consumer_goods.starting"));
        System.out.println("here");
        System.out.println(variables);
        System.out.println(variables.get(("resources.energy.starting")));
    }

    private void setMarketFee() {
        marketFee.setText(String.valueOf(variables.get("empire.market.fee")));
    }

    public void buyingAndSellingPrice(String resource) {
        double sell = (resourcePriceMap.get(resource) * Integer.parseInt(numberOfGoods.getText())) *(1-0.3);
        double buy = (resourcePriceMap.get(resource) * Integer.parseInt(numberOfGoods.getText())) *(1+0.3);

        buyingPrice.setText(String.valueOf(buy));
        sellingPrice.setText(String.valueOf(sell));
    }

    private void getIdResourcesMap() {
        subscriber.subscribe(presetsApiService.getResources(),
                resources -> {
                    System.out.println("Wan");
                    System.out.println(resources);
                });
    }

    public void buyItem() {
        int amount = Integer.parseInt(numberOfGoods.getText());
        int buy = Integer.parseInt(buyingPrice.getText());
    }

    public void sellItem() {

    }

    public void incrementAmount() {
        int amount = Integer.parseInt(numberOfGoods.getText());
        amount++;
        numberOfGoods.setText(String.valueOf(amount));
        buyingAndSellingPrice(selectedItem);
    }

    public void decrementAmount() {
        int amount = Integer.parseInt(numberOfGoods.getText());
        amount--;
        numberOfGoods.setText(String.valueOf(amount));
        buyingAndSellingPrice(selectedItem);
    }

    public void setInGameController(InGameController ingameController) {
        this.inGameController = ingameController;
    }

    public void closeMarketOverview() { this.getParent().setVisible(false); }
    //--------------------------------------------listViewOfResources-------------------------------------------------//

    public void listMarketResources() {
        if (this.resourceCountMap.isEmpty()) {
            System.out.println("resourceMap is empty");
        } else {
            System.out.println("resourceCountMap: "+ resourceCountMap);
        }
        resourcesListView.getItems().addAll(this.resourceCountMap.entrySet());
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
                System.out.println(item.getKey());
                selectedItem = item.getKey();
                selectedIconImage.setImage(imageCache.get("/de/uniks/stp24/icons/resources/" + item.getKey() + ".png"));
                numberOfGoods.setText(String.valueOf(item.getValue()));
                buyingAndSellingPrice(item.getKey());
            });
        }
    }
}