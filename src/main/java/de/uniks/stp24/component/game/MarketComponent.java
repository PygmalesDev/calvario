package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.dto.ResourceDto;
import de.uniks.stp24.dto.UpdateEmpireDto;
import de.uniks.stp24.dto.UpdateEmpireMarketDto;
import de.uniks.stp24.model.Empire;
import de.uniks.stp24.model.EmpireExtendedDto;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.rest.EmpireApiService;
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
import org.fulib.fx.annotation.param.Param;
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
    Label userCreditsLabel;
    @FXML
    Label marketFeeLabel;
    @FXML
    Label numberOfGoodsLabel;
    @FXML
    Label buyingPriceLabel;
    @FXML
    Label sellingPriceLabel;
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

    @Param("empireID")
    String empire;

    private InGameController inGameController;
    private String lastUpdate;
    private String lastSeasonUpdate;

    public String selectedItem;
    private double sellingPrice;
    private double buyingPrice;
    private double userCredits;
    private double marketFee;
    private int resourceAmount;

    private boolean noPurchase;


    private ResourceDto resourceDto;

    Provider<MarketResourceComponent> marketResourceComponentProvider = () -> new MarketResourceComponent(true, true, true, gameResourceBundle);

    // From Server
    Map<String, Integer> variables = new HashMap<>();
    Map<String, Resource> idResourceMap = new HashMap<>();

    Map<String, Integer> resourceCountMap = new HashMap<>();
    Map<String, Integer> resourcePriceMap = new HashMap<>();
    Map<String, Integer> resourceCountMapCopy = new HashMap<>();

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
    }

    private void createResourceListeners() {
        subscriber.subscribe(eventListener.listen(
                        "games" + tokenStorage.getGameId() + "empires" + tokenStorage.getEmpireId() + ".updated", EmpireExtendedDto.class),
                event -> {
                    Map<String, Integer> eventResources = event.data().resources();
                    if (!eventResources.equals(resourceCountMapCopy)) {
                        return;
                    }
                    if (noPurchase) {
                        eventResources.put(selectedItem, resourceCountMapCopy.get(selectedItem) - Integer.parseInt(numberOfGoodsLabel.getText()));
                    } else {
                        eventResources.put(selectedItem, resourceCountMapCopy.get(selectedItem) + Integer.parseInt(numberOfGoodsLabel.getText()));
                    }
                    resourceCountMap = eventResources;
                    refreshListview();
                }
        );
    }

    private void refreshListview() {
        listMarketResources();
    }


    private void getIdResourcesMap() {
        subscriber.subscribe(presetsApiService.getResources(),
                res -> {
                    resourceDto = res;
                    System.out.println();
                    System.out.println(res);
                });
    }


    private void updateResources() {
        UpdateEmpireMarketDto updateEmpireMarketDto = new UpdateEmpireMarketDto(Map.of(selectedItem, resourceAmount), null, null, null);
        this.subscriber.subscribe(empireService.updateEmpireMarket(tokenStorage.getGameId(), tokenStorage.getEmpireId(), updateEmpireMarketDto),
                error -> System.out.println("errorEmpireListener"));
        System.out.println("imran");
    }


    private void loadVariablesAndSetup() {
        subscriber.subscribe(presetsApiService.getVariables(),
                res -> {
                    this.variables = res;
                    createResourceCountMap();
                    createResourcePriceMap();
                    setMarketFee();
                }
        );
    }

    private void createResourcePriceMap() {
        resourcePriceMap.put("energy", variables.get("resources.energy.credit_value"));
        resourcePriceMap.put("minerals", variables.get("resources.minerals.credit_value"));
        resourcePriceMap.put("food", variables.get("resources.food.credit_value"));
        resourcePriceMap.put("fuel", variables.get("resources.fuel.credit_value"));
        resourcePriceMap.put("alloys", variables.get("resources.alloys.credit_value"));
        resourcePriceMap.put("consumer_goods", variables.get("resources.consumer_goods.credit_value"));
    }

    private void createResourceCountMap() {
        subscriber.subscribe(empireService.getEmpire(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
                empire -> {
                    resourceCountMap = empire.resources();
                    setCreditCount();
                    filterResourceMap();
                    listMarketResources();
                    createResourceListeners();
                    buttonLogic();
                }
                , error -> System.out.println("errorEmpireListener"));
    }

    public void filterResourceMap() {
        resourceCountMap.remove("population");
        resourceCountMap.remove("research");
        resourceCountMap.remove("credits");

    }

    private void setMarketFee() {
        if (variables.get("empire.market.fee") != null) {
            marketFeeLabel.setText(String.valueOf(variables.get("empire.market.fee")));
            this.marketFee = Double.parseDouble(marketFeeLabel.getText());
        } else {
            marketFeeLabel.setText("0");
            this.marketFee = 0;
        }
    }

    private void setCreditCount() {
        userCredits = resourceCountMap.get("credits");
        userCreditsLabel.setText(String.valueOf(userCredits));
    }

    public void buyingAndSellingPrice(String resource) {
        //TODO MarketFee correctly
        this.sellingPrice = (resourcePriceMap.get(resource) * Integer.parseInt(numberOfGoodsLabel.getText())) * (1 - 0.3);
        this.buyingPrice = (resourcePriceMap.get(resource) * Integer.parseInt(numberOfGoodsLabel.getText())) * (1 + 0.3);

        buyingPriceLabel.setText(String.valueOf(buyingPrice));
        sellingPriceLabel.setText(String.valueOf(sellingPrice));
        buttonLogic();
    }

    public void buyItem() {
        noPurchase = true;
        resourceCountMapCopy = new HashMap<>(resourceCountMap);
        resourceAmount = Integer.parseInt(numberOfGoodsLabel.getText());

        userCredits -= buyingPrice;
        userCreditsLabel.setText(String.valueOf(userCredits));
        resourceCountMap.put(selectedItem, resourceCountMap.get(selectedItem) + resourceAmount);
        updateResources();
        refreshListview();

    }

    public void sellItem() {
        noPurchase = false;
        resourceCountMapCopy = new HashMap<>(resourceCountMap);
        resourceAmount = Integer.parseInt(numberOfGoodsLabel.getText()) * -1;

        userCredits += sellingPrice;
        userCreditsLabel.setText(String.valueOf(userCredits));
        resourceCountMap.put(selectedItem, resourceCountMap.get(selectedItem) + resourceAmount);
        updateResources();
        refreshListview();
    }

    public void incrementAmount() {
        int amount = Integer.parseInt(numberOfGoodsLabel.getText());
        amount++;
        numberOfGoodsLabel.setText(String.valueOf(amount));
        buyingAndSellingPrice(selectedItem);
    }

    public void decrementAmount() {
        int amount = Integer.parseInt(numberOfGoodsLabel.getText());
        amount--;
        numberOfGoodsLabel.setText(String.valueOf(amount));
        buyingAndSellingPrice(selectedItem);
    }

    public void setInGameController(InGameController ingameController) {
        this.inGameController = ingameController;
    }

    public void closeMarketOverview() {
        this.getParent().setVisible(false);
    }
    //--------------------------------------------listViewOfResources-------------------------------------------------//

    public void listMarketResources() {
        if (this.resourceCountMap.isEmpty()) {
            System.out.println("resourceMap is empty");
        } else {
            System.out.println("resourceCountMap: " + resourceCountMap);
        }
        resourcesListView.getItems().clear();
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
            setOnMouseClicked(event -> {
                System.out.println(item.getKey());
                selectedItem = item.getKey();
                selectedIconImage.setImage(imageCache.get("/de/uniks/stp24/icons/resources/" + item.getKey() + ".png"));
                numberOfGoodsLabel.setText(String.valueOf(resourceCountMap.get(selectedItem)));
                buyingAndSellingPrice(item.getKey());
            });
        }
    }

    private void buttonLogic() {
        boolean noDebts = userCredits > 0;
        boolean userSelectedItem = selectedItem != null;
        boolean moreThanZeroGoods = Integer.parseInt(numberOfGoodsLabel.getText()) > 0;
        boolean enoughCredits = userCredits > buyingPrice;
        boolean enoughResources = resourceCountMap.getOrDefault(selectedItem, 0) >= Integer.parseInt(numberOfGoodsLabel.getText());

        boolean userCanBuy = noDebts && enoughCredits && userSelectedItem && moreThanZeroGoods;
        boolean userCanSell = enoughResources && userSelectedItem && moreThanZeroGoods;

        buyButton.setDisable(!userCanBuy);
        sellButton.setDisable(!userCanSell);
        decrementNumberOfGoods.setDisable(Integer.parseInt(numberOfGoodsLabel.getText()) <= 1);
        incrementNumberOfGoods.setDisable(false);
    }
}
