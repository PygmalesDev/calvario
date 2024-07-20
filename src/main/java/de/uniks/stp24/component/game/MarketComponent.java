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
import de.uniks.stp24.service.game.ExplanationService;
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
    public Label numberOfGoodsLabel;
    @FXML
    Label buyingPriceLabel;
    @FXML
    Label sellingPriceLabel;
    @FXML
    public ToggleButton everySeasonButton;
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
    @FXML
    ListView<SeasonComponent> seasonalTradesListView;

    @Inject
    App app;
    @Inject
    public Subscriber subscriber;
    @Inject
    ResourcesService resourcesService;
    @Inject
    EmpireService empireService;
    @Inject
    EventListener eventListener;
    @Inject
    public TokenStorage tokenStorage;
    @Inject
    public ImageCache imageCache;
    @Inject
    public PresetsApiService presetsApiService;
    @Inject
    public MarketService marketService;
    @Inject
    public ExplanationService explanationService;
    @Inject
    @org.fulib.fx.annotation.controller.Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    @Param("empireID")
    String empire;

    private InGameController inGameController;
    private String lastUpdate;
    private String lastSeasonUpdate;

    public String selectedItem;
    public int sellingPrice;
    public int buyingPrice;
    public int userCredits;
    public double marketFee;
    public int resourceAmount;
    public boolean noPurchase;

    public Map<String, Double> variables = new HashMap<>();
    public Map<String, Integer> resourceCountMap = new HashMap<>();
    public Map<String, Double> resourcePriceMap = new HashMap<>();
    public Map<String, Integer> resourceCountMapCopy = new HashMap<>();

    @Inject
    public Provider<MarketSeasonComponent> marketSeasonComponentProvider = () -> {
        var marketSeasonComponent = new MarketSeasonComponent();
        marketSeasonComponent.marketService = this.marketService;
        return marketSeasonComponent;
    };
    public final ObservableList<SeasonComponent> seasonComponents = FXCollections.observableArrayList();

    @Inject
    public MarketComponent() {
        lastUpdate = " ";
        lastSeasonUpdate = " ";
    }

    @OnInit
    public void init() {
        if (!tokenStorage.isSpectator()) {
            loadVariablesAndSetup();
            createResourceListeners();
            createSeasonListener();
        }
    }

    /**
     * Initializes amount of resources, their prices and the market fee.
     */
    private void loadVariablesAndSetup() {
        subscriber.subscribe(marketService.getVariables(),
                res -> {
                    this.variables = res;
                    createResourcePriceMap();
                    setMarketFee();
                    createResourceCountMap();
                }, error -> System.out.println("errorLoadVariableAndSetup:" + error)
        );
    }

    /**
     * Creates ResourceCountMap by fetching data form empire's resources.
     */
    private void createResourceCountMap() {
        subscriber.subscribe(marketService.getEmpire(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
                empire -> {
                    resourceCountMap.putAll(empire.resources());
                    setCreditCount();
                    filterResourceMap();
                    listMarketResources();
                    buttonLogic();
                }
                , error -> System.out.println("errorCreateResourceCountMap: " + error));
    }

    /**
     * Sets the user's credit count.
     */
    private void setCreditCount() {
        userCredits = resourceCountMap.get("credits");
        userCreditsLabel.setText(String.valueOf(userCredits));
    }

    /**
     * Non-tradable resources are filtered out in resourceCountMap.
     */
    private void filterResourceMap() {
        resourceCountMap.remove("population");
        resourceCountMap.remove("research");
        resourceCountMap.remove("credits");
    }

    /**
     * Creates listeners for resource updates.
     */
    private void createResourceListeners() {
        subscriber.subscribe(eventListener.listen(
                        "games." + tokenStorage.getGameId() + ".empires." + tokenStorage.getEmpireId() + ".updated", EmpireDto.class),
                event -> {
                    if (!lastUpdate.equals(event.data().updatedAt())) {
                        Map<String, Integer> eventResources = event.data().resources();
                        if (!eventResources.equals(resourceCountMapCopy)) {
                            return;
                        }
                        int numberOfGoods = getNumberOfGoods();
                        if (noPurchase) {
                            eventResources.put(selectedItem, resourceCountMapCopy.get(selectedItem) - numberOfGoods);
                        } else {
                            eventResources.put(selectedItem, resourceCountMapCopy.get(selectedItem) + numberOfGoods);
                        }
                        resourceCountMap = eventResources;
                        refreshListview();
                        this.lastUpdate = event.data().updatedAt();
                    }
                }, error -> System.out.println("errorCreateResourceListener: " + error));
    }

    /**
     * This method provides logic for enabling and disabling buttons based on user actions and available resources.
     */
    private void buttonLogic() {
        int numberOfGoods = getNumberOfGoods();
        boolean noDebts = userCredits > 0;
        boolean userSelectedItem = selectedItem != null;
        boolean moreThanZeroGoods = numberOfGoods > 0;
        boolean enoughCredits = userCredits > buyingPrice;
        boolean enoughResources = resourceCountMap.getOrDefault(selectedItem, 0) >= numberOfGoods;

        boolean userCanBuy = noDebts && enoughCredits && userSelectedItem && moreThanZeroGoods;
        boolean userCanSell = enoughResources && userSelectedItem && moreThanZeroGoods;

        buyButton.setDisable(!userCanBuy);
        sellButton.setDisable(!userCanSell);
        decrementNumberOfGoods.setDisable(numberOfGoods <= 1);
        incrementNumberOfGoods.setDisable(false);
    }

    /**
     * Sets the market fee.
     */
    private void setMarketFee() {
        marketFeeLabel.setText(String.valueOf(this.variables.get("empire.market.fee")));
        this.marketFee = Double.parseDouble(marketFeeLabel.getText());
    }

    /**
     * Refreshes the list view with updated resource data and also the buttonLogic.
     */
    private void refreshListview() {
        buttonLogic();
        listMarketResources();
    }

    /**
     * Updates the resources on the server by providing a map of the updated amount of resources and resourcesType after transactions.
     */
    private void updateResources() {
        UpdateEmpireMarketDto updateEmpireMarketDto = new UpdateEmpireMarketDto(Map.of(selectedItem, resourceAmount), null, null, null);
        this.subscriber.subscribe(marketService.updateEmpireMarket(tokenStorage.getGameId(), tokenStorage.getEmpireId(), updateEmpireMarketDto),
                result -> {},
                error -> System.out.println("errorUpdateResources:\n" + error.getMessage()));
    }

    /**
     * Creates a map of resource prices.
     */
    private void createResourcePriceMap() {
        resourcePriceMap.put("energy", variables.get("resources.energy.credit_value"));
        resourcePriceMap.put("minerals", variables.get("resources.minerals.credit_value"));
        resourcePriceMap.put("food", variables.get("resources.food.credit_value"));
        resourcePriceMap.put("fuel", variables.get("resources.fuel.credit_value"));
        resourcePriceMap.put("alloys", variables.get("resources.alloys.credit_value"));
        resourcePriceMap.put("consumer_goods", variables.get("resources.consumer_goods.credit_value"));
    }

    /**
     * Dynamically updating the buying and selling price of the resources after incrementing or decrementing numberOfGoods.
     */
    public void buyingAndSellingPrice(String resource) {
        int numberOfGoods = getNumberOfGoods();
        this.sellingPrice = (int) Math.round((resourcePriceMap.get(resource) * numberOfGoods) * (1 - this.marketFee));
        this.buyingPrice = (int) Math.round((resourcePriceMap.get(resource) * numberOfGoods) * (1 + this.marketFee));

        buyingPriceLabel.setText(String.valueOf(buyingPrice));
        sellingPriceLabel.setText(String.valueOf(sellingPrice));
        buttonLogic();
    }

    /**
     * Handles buying an item. Distinction between seasonal and non-seasonal buys.
     */
    public void buyItem() {
        int numberOfGoods = getNumberOfGoods();
        resourceAmount = numberOfGoods;
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

    /**
     * Handles selling an item. Distinction between seasonal and non-seasonal sells.
     */
    public void sellItem() {
        int numberOfGoods = getNumberOfGoods();
        resourceAmount = numberOfGoods * -1;
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

    /**
     * Checks if the numberOfGoodsLabel is empty to prevent numberFormatException
     */
    private int getNumberOfGoods() {
        return numberOfGoodsLabel.getText().isEmpty() ? 0 : Integer.parseInt(numberOfGoodsLabel.getText());
    }

    /**
     * Increments the amount of goods.
     */
    public void incrementAmount() {
        if (Objects.nonNull(selectedItem)) {
            int amount = getNumberOfGoods();
            amount++;
            numberOfGoodsLabel.setText(String.valueOf(amount));
            buyingAndSellingPrice(selectedItem);
        }
    }

    /**
     * Decrements the amount of goods.
     */
    public void decrementAmount() {
        if (Objects.nonNull(selectedItem)) {
            int amount = getNumberOfGoods();
            amount--;
            numberOfGoodsLabel.setText(String.valueOf(amount));
            buyingAndSellingPrice(selectedItem);
        }
    }

    /**
     * Closes the market overview.
     */
    public void closeMarketOverview() {
        this.setVisible(false);
    }

    // --------------------------------------------listViewOfResources-------------------------------------------------//

    /**
     * Lists the market resources in the ListView.
     */
    public void listMarketResources() {
        resourcesListView.getItems().clear();
        resourcesListView.getItems().addAll(this.resourceCountMap.entrySet());
        resourcesListView.setCellFactory(list -> new ResourceCell());
    }

    /**
     * Custom ListCell for displaying resources in the ListView.
     */
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
                setId(item.getKey() + "_marketButton");
                imageView.setImage(imageCache.get("/de/uniks/stp24/assets/market/buttons/" + item.getKey() + ".png"));
                imageView.setFitWidth(35);
                imageView.setFitHeight(35);
                text.setText(String.valueOf(item.getValue()));
                text.setId(item.getKey() + "_marketGoods");
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

    // ---------------------------------------SeasonalTrades------------------------------------------------------------//

    @OnRender
    public void render() {
        loadSeasonalTrades();
    }

    /**
     * Toggles the creation of seasonal trades.
     */
    public void createSeasonalTrades() {
        everySeasonButton.setSelected(everySeasonButton.isSelected());
    }

    /**
     * Adds a seasonal transaction.
     */
    private void addSeasonalTransaction(String transactionType, int price) {
        SeasonComponent seasonComponent = new SeasonComponent(transactionType, this.selectedItem, resourceAmount, price, true);
        seasonComponents.add(seasonComponent);
        saveSeasonalTrades();
    }

    /**
     * Creates a listener for seasonal trades.
     */
    public void createSeasonListener() {
        this.subscriber.subscribe(this.eventListener
                        .listen("games." + tokenStorage.getGameId() + ".ticked", Game.class),
                event -> {
                    if (!lastSeasonUpdate.equals(event.data().updatedAt())) {
                        updateAfterSeasonalTrade();
                        this.lastSeasonUpdate = event.data().updatedAt();
                    }
                },
                error -> System.out.println("errorCreateSeasonListener:" + error));
    }

    /**
     * Performs seasonal trades.
     */
    private void performSeasonalTrades() {
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
    }

    /**
     * Updates resources after a seasonal trade.
     */
    private void updateAfterSeasonalTrade() {
        subscriber.subscribe(marketService.getEmpire(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
                empireDto -> {
                    resourceCountMap = empireDto.resources();
                    filterResourceMap();
                    refreshListview();
                    performSeasonalTrades();
                }, error -> System.out.println("errorUpdateAfterSeasonalTrade: " + error));
    }

    /**
     * Performs a seasonal buy transaction.
     */
    private void performSeasonalBuy(String resourceType, int resourceAmount) {
        this.resourceAmount = resourceAmount;
        int buyCost = (int) Math.round((resourcePriceMap.get(resourceType) * this.resourceAmount) * (1 + this.marketFee));
        if (userCredits >= buyCost) {
            resourceCountMapCopy = resourceCountMap;
            userCredits -= buyCost;
            resourceCountMap.put(resourceType, resourceCountMap.get(resourceType) + this.resourceAmount);
            updateResources();
            refreshListview();
        }
    }

    /**
     * Performs a seasonal sell transaction.
     */
    private void performSeasonalSell(String resourceType, int resourceAmount) {
        this.resourceAmount = resourceAmount;
        int sellCost = (int) Math.round((resourcePriceMap.get(resourceType) * resourceAmount) * (1 - this.marketFee));
        if (resourceCountMap.get(resourceType) >= resourceAmount) {
            resourceCountMapCopy = new HashMap<>(resourceCountMap);
            userCredits += sellCost;
            resourceCountMap.put(selectedItem, resourceCountMap.get(resourceType) + resourceAmount);
            updateResources();
            refreshListview();
        }
    }

    /**
     * Sets the in-game controller.
     */
    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

    @OnDestroy
    public void destroy() {
        this.marketService.dispose();
        this.subscriber.dispose();
    }

    /**
     * Saves seasonal trades to the server.
     */
    public void saveSeasonalTrades() {
        marketService.saveSeasonalTrades();
    }

    /**
     * Loads seasonal trades from the server.
     */
    public void loadSeasonalTrades() {
        subscriber.subscribe(marketService.getSeasonalTrades(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
                seasonalTradeDto -> {
                    if (Objects.nonNull(seasonalTradeDto._private()))
                        this.seasonComponents.addAll(seasonalTradeDto._private().get("allSeasonalTrades"));

                    this.seasonalTradesListView.setItems(this.seasonComponents);
                    this.seasonalTradesListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.marketSeasonComponentProvider));
                    this.marketService.setSeasonComponents(this.seasonComponents);
                }
                , error -> System.out.println("errorLoadSeasonalTrades:" + error));
    }
}
