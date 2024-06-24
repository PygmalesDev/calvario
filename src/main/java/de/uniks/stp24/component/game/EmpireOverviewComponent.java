package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.IslandType;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.ws.EventListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;
import org.controlsfx.control.GridCell;
import org.fulib.fx.annotation.controller.Component;
import javafx.scene.image.ImageView;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;

import org.controlsfx.control.GridView;

import java.util.*;

@Component(view = "EmpireOverview.fxml")
public class EmpireOverviewComponent extends StackPane {
    @FXML
    Button closeEmpireOverviewButton;
    @FXML
    ImageView potraitContainer;
    @FXML
    ImageView flagContainer;
    @FXML
    TextFlow empireNameContainer;
    @FXML
    TextFlow empireDescriptionContainer;
    @FXML
    AnchorPane colourContainer;
    @FXML
    GridView<IslandComponent> islandGridView;

    @Inject
    App app;
    @Inject
    Subscriber subscriber;
    @Inject
    EventListener eventListener;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    EmpireService empireService;
    @Inject
    IslandsService islandsService;

    @Inject
    @org.fulib.fx.annotation.controller.Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    private InGameController inGameController;

    ObservableList<Island> islandObservableList;

    List<Island> islandList = new ArrayList<>();
    List<IslandComponent> islandComponentList = new ArrayList<>();
    List<IslandComponent> empireIslands = new ArrayList<>();
    List<Image> gridViewImages = new ArrayList<>();

    Map<IslandType, Image> imageMap = new HashMap<>();

    private String lastUpdate;
    private String lastSeasonUpdate;
    private String gameID;
    private String empireID;

    int potrait;
    int flag;
    String colour;
    String empireName;
    String empireDescription;
    @Inject
    ImageCache imageCache;

    String resourcesPaths = "/de/uniks/stp24/assets/";
    String flagsFolderPath = "flags/flag_";
    String portraitsFolderPath = "portraits/captain_";
    String islandButtonsFolderPath = "buttons/IslandButton/";

    @Inject
    public EmpireOverviewComponent() {
        lastUpdate = "";
        lastSeasonUpdate = "";
    }


    @OnInit
    public void initEmpireList() {
        gameID = tokenStorage.getGameId();
        empireID = tokenStorage.getEmpireId();
        this.subscriber.subscribe(this.empireService.getEmpire(gameID, empireID), this::empireTraits);
    }

    public void closeEmpireOverview() {
        this.getParent().setVisible(false);
    }

    private void empireTraits(EmpireDto empireDto) {
        this.potrait = empireDto.portrait();
        this.flag = empireDto.flag();
        this.colour = empireDto.color();
        this.empireName = empireDto.name();
        this.empireDescription = empireDto.description();
        addTraits();
        filterIslands();
    }

    private void addTraits() {
        flagContainer.setImage(imageCache.get(resourcesPaths + flagsFolderPath + this.flag + ".png"));

        empireNameContainer.getChildren().clear();
        Text empireNameText = new Text(this.empireName);
        empireNameText.getStyleClass().add("empireOverviewTextWhite");
        empireNameContainer.getChildren().add(empireNameText);

        empireDescriptionContainer.getChildren().clear();
        Text empireDescriptionText = new Text(this.empireDescription);
        empireDescriptionText.getStyleClass().add("empireOverviewTextBlack");
        empireDescriptionContainer.getChildren().add(empireDescriptionText);

        potraitContainer.setImage(imageCache.get(resourcesPaths + portraitsFolderPath + this.potrait + ".png"));
        colourContainer.setStyle("-fx-background-color: " + this.colour);
    }

    public void IslandImageMapper() {
        imageMap.put(IslandType.regular, imageCache.get(resourcesPaths + islandButtonsFolderPath + "button_regular.png"));
        imageMap.put(IslandType.energy, imageCache.get(resourcesPaths + islandButtonsFolderPath + "button_energy.png"));
        imageMap.put(IslandType.agriculture, imageCache.get(resourcesPaths + islandButtonsFolderPath + "button_agriculture.png"));
        imageMap.put(IslandType.mining, imageCache.get(resourcesPaths + islandButtonsFolderPath + "button_mining.png"));
        imageMap.put(IslandType.uninhabitable_0, imageCache.get(resourcesPaths + islandButtonsFolderPath + "button_uninhabitable_0.png"));
        imageMap.put(IslandType.uninhabitable_1, imageCache.get(resourcesPaths + islandButtonsFolderPath + "button_uninhabitable_1.png"));
        imageMap.put(IslandType.uninhabitable_2, imageCache.get(resourcesPaths + islandButtonsFolderPath + "button_uninhabitable_2.png"));
        imageMap.put(IslandType.uninhabitable_3, imageCache.get(resourcesPaths + islandButtonsFolderPath + "button_uninhabitable_3.png"));
        imageMap.put(IslandType.ancient_military, imageCache.get(resourcesPaths + islandButtonsFolderPath + "button_ancient_military.png"));
        imageMap.put(IslandType.ancient_industry, imageCache.get(resourcesPaths + islandButtonsFolderPath + "button_ancient_industry.png"));
        imageMap.put(IslandType.ancient_technology, imageCache.get(resourcesPaths + islandButtonsFolderPath + "button_ancient_technology.png"));
    }

    public void filterIslands() {
        if (!islandsService.getListOfIslands().isEmpty()) {
            System.out.println(islandsService.getListOfIslands().size());
            islandList = islandsService.getListOfIslands();
            System.out.println(islandList.size());
        }

        for (Island island : islandList) {
            IslandComponent islandComponent = new IslandComponent();
            islandComponent.applyInfo(island);
            islandComponent.setInGameController(inGameController);
//            islandComponent.setIsOnMap(false);
            if (Objects.nonNull(island.owner()) && island.owner().equals(empireID)) {
                empireIslands.add(islandComponent);
            }
            islandComponentList.add(islandComponent);
        }
        System.out.println("empireIslands size: " + empireIslands.size());

        IslandImageMapper();


        for (Island island : islandList) {
            gridViewImages.add(imageMap.get(island.type()));
        }

        fillGrid();
    }

    public void fillGrid() {
        islandGridView.getItems().addAll(empireIslands);

        islandGridView.setCellFactory(grid -> new IslandCell());
    }

    public class IslandCell extends GridCell<IslandComponent> {
        ImageCache imageCache = new ImageCache();


        @Override
        protected void updateItem(IslandComponent item, boolean empty) {
            String resourcesPaths = "/de/uniks/stp24/assets/";
            String rudderPath = "buttons/other/selection_wheel_icon.png";

            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                ImageView imageView = new ImageView(imageMap.get(item.island.type()));
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);
                imageView.setPreserveRatio(true);
                setGraphic(imageView);

                islandGridView.setOnMouseClicked(event -> {
                    islandGridView.getId();
                    IslandComponent clicked = getItem();
                    clicked.showIslandOverview();

                    ImageView rudder = new ImageView(imageCache.get(resourcesPaths + rudderPath));
                    rudder.setFitWidth(40);
                    rudder.setFitHeight(40);
                    rudder.setPreserveRatio(true);
                    setGraphic(rudder);
                });
            }
        }

    }

    public void setInGameController(InGameController ingameController) {
        this.inGameController = ingameController;
        if (Objects.nonNull(ingameController)) {
            System.out.println("inGameController ist nicht null");
        } else {
            System.out.println("inGameController null");
        }
    }
}