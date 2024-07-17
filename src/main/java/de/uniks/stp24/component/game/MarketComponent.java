package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.EmpireExtendedDto;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.SeasonComponent;
import de.uniks.stp24.rest.PresetsApiService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.MarketService;
import de.uniks.stp24.service.game.ResourcesService;
import de.uniks.stp24.ws.EventListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.annotation.param.Param;
import org.fulib.fx.constructs.listview.ComponentListCell;
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
    ToggleButton everySeasonButton;
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
    Button createSeasonalTrades;
    @FXML
    public ListView<Map.Entry<String, Integer>> resourcesListView;
    @FXML
    ListView<SeasonComponent> seasonalTradesListView;


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
    @Inject
    MarketService marketService;

    @Param("empireID")
    String empire;

    private InGameController inGameController;
    private String lastUpdate;
    private String lastSeasonUpdate;

    public String selectedItem;
    private int sellingPrice;
    private int buyingPrice;
    private int userCredits;
    private double marketFee;
    private int resourceAmount;

    private boolean noPurchase;

    private ResourceDto resourceDto;

    //a1 Provider has now lambda expression. In it marketSeasonCompontne is initialized and its marketService too
    @Inject
    public Provider<MarketSeasonComponent> marketSeasonComponentProvider = () ->  {
        var marketSeasonComponent = new MarketSeasonComponent();
        marketSeasonComponent.marketService =  this.marketService;
        return marketSeasonComponent;
    };

    private final ObservableList<SeasonComponent> seasonComponents = FXCollections.observableArrayList();

    // From Server
    Map<String, Double> variables = new HashMap<>();
    Map<String, Integer> resourceCountMap = new HashMap<>();
    Map<String, Double> resourcePriceMap = new HashMap<>();
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
        if (!tokenStorage.isSpectator()) {
            loadVariablesAndSetup();
            getIdResourcesMap();
            createResourceListeners();
            createSeasonListener();
        }
    }

    private void loadVariablesAndSetup() {
        subscriber.subscribe(marketService.getVariables(),
                res -> {
                    this.variables = res;
                    createResourcePriceMap();
                    setMarketFee();
                    createResourceCountMap();
                }
        );
    }

    private void createResourceCountMap() {
        subscriber.subscribe(marketService.getEmpire(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
                empire -> {
                    resourceCountMap = empire.resources();

                    setCreditCount();
                    filterResourceMap();
                    listMarketResources();
                    buttonLogic();
                }
                , error -> System.out.println("errorCreateResouceCountMap: " + error));
    }

    private void setCreditCount() {
        userCredits = resourceCountMap.get("credits");
        userCreditsLabel.setText(String.valueOf(userCredits));
    }

    public void filterResourceMap() {
        resourceCountMap.remove("population");
        resourceCountMap.remove("research");
        resourceCountMap.remove("credits");

    }

    private void createResourceListeners() {
        subscriber.subscribe(eventListener.listen(
                        "games" + tokenStorage.getGameId() + "empires" + tokenStorage.getEmpireId() + ".updated", EmpireExtendedDto.class),
                event -> {
                    if (!lastUpdate.equals(event.data().updatedAt())) {
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
                        this.lastUpdate = event.data().updatedAt();
                    }
                }
        );
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

    private void setMarketFee() {
        marketFeeLabel.setText(String.valueOf(this.variables.get("empire.market.fee")));
        this.marketFee = Double.parseDouble(marketFeeLabel.getText());
    }

    private void refreshListview() {
        listMarketResources();
    }

    private void getIdResourcesMap() {
        subscriber.subscribe(marketService.getResources(),
                res -> {
                    resourceDto = res;
                });
    }

    private void updateResources() {
        UpdateEmpireMarketDto updateEmpireMarketDto = new UpdateEmpireMarketDto(Map.of(selectedItem, resourceAmount), null, null, null);
        this.subscriber.subscribe(marketService.updateEmpireMarket(tokenStorage.getGameId(), tokenStorage.getEmpireId(), updateEmpireMarketDto),
                error -> System.out.println("errorUpdateResources" + error));
    }

    private void createResourcePriceMap() {
        resourcePriceMap.put("energy", variables.get("resources.energy.credit_value"));
        resourcePriceMap.put("minerals", variables.get("resources.minerals.credit_value"));
        resourcePriceMap.put("food", variables.get("resources.food.credit_value"));
        resourcePriceMap.put("fuel", variables.get("resources.fuel.credit_value"));
        resourcePriceMap.put("alloys", variables.get("resources.alloys.credit_value"));
        resourcePriceMap.put("consumer_goods", variables.get("resources.consumer_goods.credit_value"));
    }

    public void buyingAndSellingPrice(String resource) {
        //TODO MarketFee correctly
        this.sellingPrice = (int) Math.round((resourcePriceMap.get(resource) * Integer.parseInt(numberOfGoodsLabel.getText())) * (1 - this.marketFee));
        this.buyingPrice = (int) Math.round((resourcePriceMap.get(resource) * Integer.parseInt(numberOfGoodsLabel.getText())) * (1 + this.marketFee));

        buyingPriceLabel.setText(String.valueOf(buyingPrice));
        sellingPriceLabel.setText(String.valueOf(sellingPrice));
        buttonLogic();
    }


    public void buyItem() {
        resourceAmount = Integer.parseInt(numberOfGoodsLabel.getText());
        if (everySeasonButton.isSelected()) {
            addSeasonalTransaction("buy", buyingPrice);
        } else {
            noPurchase = true;
            resourceCountMapCopy = new HashMap<>(resourceCountMap);

            userCredits -= buyingPrice;
            userCreditsLabel.setText(String.valueOf(userCredits));
            resourceCountMap.put(selectedItem, resourceCountMap.get(selectedItem) + resourceAmount);
            updateResources();
            refreshListview();
        }
    }

    public void sellItem() {
        resourceAmount = Integer.parseInt(numberOfGoodsLabel.getText()) * -1;
        if (everySeasonButton.isSelected()) {
            addSeasonalTransaction("sell", sellingPrice);
        } else {
            noPurchase = false;
            resourceCountMapCopy = new HashMap<>(resourceCountMap);

            userCredits += sellingPrice;
            userCreditsLabel.setText(String.valueOf(userCredits));
            resourceCountMap.put(selectedItem, resourceCountMap.get(selectedItem) + resourceAmount);
            updateResources();
            refreshListview();
        }
    }

    public void incrementAmount() {
        if (Objects.nonNull(selectedItem)) {
            int amount = Integer.parseInt(numberOfGoodsLabel.getText());
            amount++;
            numberOfGoodsLabel.setText(String.valueOf(amount));
            buyingAndSellingPrice(selectedItem);
        }
    }

    public void decrementAmount() {
        if (Objects.nonNull(selectedItem)) {
            int amount = Integer.parseInt(numberOfGoodsLabel.getText());
            amount--;
            numberOfGoodsLabel.setText(String.valueOf(amount));
            buyingAndSellingPrice(selectedItem);
        }
    }

    public void closeMarketOverview() {
        this.getParent().setVisible(false);
    }

    //--------------------------------------------listViewOfResources-------------------------------------------------//

    public void listMarketResources() {
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
            text.getStyleClass().add("javaneseText");
            vBox.getChildren().addAll(imageView, text);
            VBox.setMargin(vBox, new Insets(10, 10, 10, 10));
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
                selectedItem = item.getKey();
                selectedIconImage.setImage(imageCache.get("/de/uniks/stp24/icons/resources/" + item.getKey() + ".png"));
                numberOfGoodsLabel.setText("1");
                buyingAndSellingPrice(item.getKey());
            });
        }

    }
    /*---------------------------------------SeasonalTrades------------------------------------------------------------*/
    //TODO Seasonal Trades -> synchronize with storageOverviewComponent by also adding the added resource in storage to market and vice versa.
    //                     -> normal buy and sell works but seasonal is really buggy
    //                     -> buy, sell, seasonalBuy, seasonalSell
    //TODO Seasonal Trades -> save seasonal trades after leaving
    //TODO Seasonal Trades -> cancel running seasonal Trades

    @OnRender
    public void render() {
        loadSeasonalTrades();
    }

    public void createSeasonalTrades() {
        everySeasonButton.setSelected(everySeasonButton.isSelected());
    }

    private void addSeasonalTransaction(String transactionType, int price) {
        SeasonComponent seasonComponent = new SeasonComponent(transactionType, this.selectedItem, resourceAmount, price, true);
        seasonComponents.add(seasonComponent);
        saveSeasonalTrades();
    }


    public void createSeasonListener() {
        this.subscriber.subscribe(this.eventListener
                        .listen("games." + tokenStorage.getGameId() + ".ticked", Game.class),
                event -> {
                    if (!lastSeasonUpdate.equals(event.data().updatedAt())) {
                        performSeasonalTrades();
                        updateResources();
                        refreshListview();
                        this.lastSeasonUpdate = event.data().updatedAt();
                    }
                },
                error -> System.out.println("errorCreatSeasonListener:" + error));
    }


    private void performSeasonalTrades() {
        System.out.println("performSeasonalTrades");
        for (SeasonComponent seasonalTrade : seasonComponents) {
            if (seasonalTrade.isPlaying()) {
                if ("buy".equals(seasonalTrade.getTransActionTypeText())) {
                    performSeasonalBuy(seasonalTrade.getResourceType(), seasonalTrade.getResourceAmount());
                } else if ("sell".equals(seasonalTrade.getTransActionTypeText())) {
                    performSeasonalSell(seasonalTrade.getResourceType(), seasonalTrade.getResourceAmount());
                }
            }
        }
        userCreditsLabel.setText(String.valueOf(userCredits));
        refreshListview();
    }

    private void performSeasonalBuy(String resourceType, int resourceAmount) {
        int buyCost = resourceAmount * (int) Math.round(resourcePriceMap.get(resourceType) * (1 + marketFee));
        if (userCredits >= buyCost) {
            userCredits -= buyCost;
            resourceCountMap.put(resourceType, resourceCountMap.getOrDefault(resourceType, 0) + resourceAmount);
            updateResources();
            refreshListview();
        }
    }

    private void performSeasonalSell(String resourceType, int resourceAmount) {
        int sellCost = resourceAmount * (int) Math.round(resourcePriceMap.get(resourceType) * (1 - marketFee));
        if (resourceCountMap.getOrDefault(resourceType, 0) >= resourceAmount) {
            userCredits += sellCost;
            resourceCountMap.put(resourceType, resourceCountMap.get(resourceType) + resourceAmount);
            updateResources();
            refreshListview();
        }
    }


    public void setInGameController(InGameController ingameController) {
        this.inGameController = ingameController;
    }

    @OnDestroy
    public void destroy() {
        this.marketService.dispose();
        this.subscriber.dispose();
    }

    public void saveSeasonalTrades(){
        Map<String, List<SeasonComponent>> _private = new HashMap<>();
        _private.put("allSeasonalTrades", seasonComponents);
        SeasonalTradeDto seasonalTradeDto = new SeasonalTradeDto(_private);
        subscriber.subscribe(marketService.saveSeasonalComponents(tokenStorage.getGameId(), tokenStorage.getEmpireId(), seasonalTradeDto),
                error -> System.out.println("errorSaveSeasonalTrades:" + error));
    }

    public void loadSeasonalTrades(){
        subscriber.subscribe(marketService.getSeasonalTrades(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
        seasonalTradeDto -> {
            this.seasonComponents.addAll(seasonalTradeDto._private().get("allSeasonalTrades"));

            this.seasonalTradesListView.setItems(this.seasonComponents);
            this.seasonalTradesListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.marketSeasonComponentProvider));
            this.marketService.setSeasonComponents(this.seasonComponents);
        }
        , error -> System.out.println("errorLoadSeasonalTrades:" + error));
    }
}
