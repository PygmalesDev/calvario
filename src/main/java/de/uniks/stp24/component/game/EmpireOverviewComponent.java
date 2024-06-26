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
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
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
    Text empireNameContainer;
    @FXML
    Text empireDescriptionContainer;
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
    ImageCache imageCache;

    @Inject
    @org.fulib.fx.annotation.controller.Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    private InGameController inGameController;
    private String gameID, empireID;
    int potrait, flag;
    String colour, empireName, empireDescription;

    List<Island> islandList = new ArrayList<>();
    List<IslandComponent> islandComponentList = new ArrayList<>();
    List<IslandComponent> empireIslands = new ArrayList<>();
    List<Image> gridViewImages = new ArrayList<>();

    Map<IslandType, Image> imageMap = new HashMap<>();

    String resourcesPaths = "/de/uniks/stp24/assets/";
    String flagsFolderPath = "flags/flag_";
    String portraitsFolderPath = "portraits/captain_";
    String islandButtonsFolderPath = "buttons/IslandButton/";

    @Inject
    public EmpireOverviewComponent() {
    }

    public void setInGameController(InGameController ingameController) {
        this.inGameController = ingameController;
    }

    @OnInit
    public void initEmpireList() {
        gameID = tokenStorage.getGameId();
        empireID = tokenStorage.getEmpireId();
        this.subscriber.subscribe(this.empireService.getEmpire(gameID, empireID),
                this::empireTraits,
                error -> System.out.println(error));
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
        filterIslandsAndFillGrid();
    }

    private void addTraits() {
        flagContainer.setImage(imageCache.get(resourcesPaths + flagsFolderPath + this.flag + ".png"));

        empireNameContainer.setText(this.empireName);
        this.empireNameContainer.getStyleClass().add("empireOverviewTextWhite");

        empireDescriptionContainer.setText(this.empireDescription);
        empireDescriptionContainer.getStyleClass().add("empireOverviewTextBlack");

        potraitContainer.setImage(imageCache.get(resourcesPaths + portraitsFolderPath + this.potrait + ".png"));
        colourContainer.setStyle("-fx-background-color: " + this.colour);
    }

    public void islandImageMapper() {
        Arrays.stream(IslandType.values()).forEach(type ->
                imageMap.put(type, imageCache.get(resourcesPaths + islandButtonsFolderPath + "button_" + type.name().toLowerCase() + ".png")));
    }

    public void filterIslandsAndFillGrid() {
        if (!islandsService.getListOfIslands().isEmpty()) {
            System.out.println(islandsService.getListOfIslands().size());
            islandList = islandsService.getListOfIslands();
            System.out.println(islandList.size());
        }

        for (Island island : islandList) {
            IslandComponent islandComponent = new IslandComponent();
            islandComponent.applyInfo(island);
            islandComponent.setInGameController(inGameController);
            if (Objects.nonNull(island.owner()) && island.owner().equals(empireID)) {
                empireIslands.add(islandComponent);
            }
            islandComponentList.add(islandComponent);
        }
        System.out.println("empireIslands size: " + empireIslands.size());

        islandImageMapper();


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
        int clickCounter = 0;

        @Override
        protected void updateItem(IslandComponent item, boolean empty) {
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
                    inGameController.selectedIsland = clicked;
                    inGameController.islandAttributes.setIsland(clicked.getIsland());
                    if (clickCounter%2 == 0) {
                        clicked.inGameController.showOverview();
                    }else {
                        inGameController.overviewSitesComponent.closeOverview();
                    }
                    clickCounter++;
                });
            }
        }

    }
}