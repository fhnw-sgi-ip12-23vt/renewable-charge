package ch.fhnw.elektroautos.mvc.renewablecharge.view.gui;

import ch.fhnw.elektroautos.mvc.renewablecharge.controller.game.ApplicationFXController;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.MainModel;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.Car;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.gameobjects.Player;
import ch.fhnw.elektroautos.mvc.renewablecharge.model.seasons.EnergyPackage;
import ch.fhnw.elektroautos.mvc.renewablecharge.view.pui.CarSelectionPUI;
import ch.fhnw.elektroautos.mvc.util.mvcbase.ViewMixin;
import com.pi4j.context.Context;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class CarSelectionScreen extends StackPane implements ViewMixin<MainModel, ApplicationFXController> {
    private GridPane gridPane;
    private ImageView[] carImageViews;
    private ImageView[] plateImageViews;
    private Arc semiCircle;
    private Pane[] playerPanes;
    private Label countdown;

    public CarSelectionScreen(ApplicationFXController controller, Context pi4J) {
        init(controller);
        new CarSelectionPUI(controller, pi4J);
    }

    @Override
    public void initializeSelf() {
        loadFonts("/fonts/Lato/Lato-Lig.ttf", "/fonts/fontawesome-webfont.ttf");
        addStylesheetFiles("/mvc/renewablecharge/css/root.screen.css");
        addStylesheetFiles("/mvc/renewablecharge/css/car-selection.screen.css");
        getStyleClass().add("root");
    }

    @Override
    public void initializeParts() {
        setFocusTraversable(true);
        Platform.runLater(this::requestFocus);

        this.gridPane = new GridPane(); // Create a new GridPane for the existing layout
        this.carImageViews = new ImageView[4];
        this.plateImageViews = new ImageView[4];
        this.playerPanes = new Pane[4];

        for (int i = 0; i < playerPanes.length; i++) {
            playerPanes[i] = createPane(i);
        }

        this.countdown = new Label("0");
        this.countdown.getStyleClass().add("countdown-label");

        // Create semi-circle
        this.semiCircle = new Arc();
        this.semiCircle.setRadiusX(95);
        this.semiCircle.setRadiusY(95);
        this.semiCircle.setStartAngle(180);
        this.semiCircle.setLength(180);
        this.semiCircle.setType(ArcType.ROUND);
        this.semiCircle.setFill(Color.valueOf("#710000"));
    }

    @Override
    public void layoutParts() {
        this.getChildren().add(gridPane); // Add the gridPane to the StackPane

        // Configure and align semi-circle
        StackPane.setAlignment(semiCircle, Pos.TOP_CENTER);
        StackPane.setMargin(semiCircle, new Insets(30, 0, 0, 0)); // Adjust top margin to position it

        // Configure and align countdown within the semi-circle
        StackPane.setAlignment(countdown, Pos.TOP_CENTER);
        StackPane.setMargin(countdown, new Insets(15, 0, 0, 0)); // Adjust top margin to center it in the semi-circle

        setupGridPane();

        this.getChildren().add(semiCircle);
        this.getChildren().add(countdown);

        StackPane.setAlignment(semiCircle, Pos.TOP_CENTER);
        StackPane.setMargin(semiCircle, new Insets(-50, 0, 0, 0));

        StackPane.setAlignment(countdown, Pos.TOP_CENTER);
        StackPane.setMargin(countdown, new Insets(2, 0, 0, 0));
    }

    private void setupGridPane() {
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        // Adding a row for the countdown is now unnecessary in gridPane
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(50);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(50);

        gridPane.getRowConstraints().addAll(row1, row2);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(50);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(50);
        gridPane.getColumnConstraints().addAll(column1, column2);

        // Adjust the placement of player panes
        for (int i = 0; i < playerPanes.length; i++) {
            gridPane.add(playerPanes[i], i % 2, i / 2); // No need to offset rows
        }
    }

    private Pane createPane(int index) {
        StackPane pane = new StackPane();

        String imageUrl = "/mvc/renewablecharge/images/seasons/";
        switch (index) {
            case 0:
                imageUrl += "winter.jpg";
                break;
            case 1:
                imageUrl += "spring.jpg";
                break;
            case 2:
                imageUrl += "summer.jpg";
                break;
            case 3:
                imageUrl += "fall.jpg";
                break;
        }

        // Background for visual debugging
        Rectangle background = new Rectangle();
        background.setFill(new ImagePattern(new Image(imageUrl)));
        background.widthProperty().bind(pane.widthProperty());
        background.heightProperty().bind(pane.heightProperty());
        pane.getChildren().add(background);

        // Image views for the car and plate
        ImageView plateImageView = new ImageView(new Image("/mvc/renewablecharge/images/cars/car_plate.png"));
        plateImageView.fitWidthProperty().bind(pane.widthProperty().divide(2));
        plateImageView.setPreserveRatio(true);
        plateImageViews[index] = plateImageView;

        // Set alignment for the plate image view and add a bit of bottom padding
        StackPane.setAlignment(plateImageView, Pos.BOTTOM_CENTER);
        StackPane.setMargin(plateImageView, new Insets(0, 0, 20, 0));

        ImageView carImageView = new ImageView();
        carImageView.setPreserveRatio(true);
        carImageView.fitHeightProperty().bind(pane.heightProperty().multiply(0.5));
        StackPane.setAlignment(carImageView, Pos.BOTTOM_CENTER);
        StackPane.setMargin(carImageView, new Insets(0, 0, 22, 0));
        carImageViews[index] = carImageView;

        // Label for player info
        Label playerInfo = new Label("Player " + (index + 1)); // Placeholder text
        playerInfo.getStyleClass().add("player-info"); // Add a CSS class for styling

        // Set alignment based on the column
        if (index % 2 == 0) {
            // Left column
            StackPane.setAlignment(playerInfo, Pos.TOP_LEFT);
        } else {
            // Right column
            StackPane.setAlignment(playerInfo, Pos.TOP_RIGHT);
        }

        // Add all components to the pane
        pane.getChildren().addAll(plateImageView, carImageView, playerInfo);
        return pane;
    }

    @Override
    public void setupUiToActionBindings(ApplicationFXController controller) {
        System.out.println("CarSelectionScreen.setupUiToActionBindings");
        setOnKeyPressed(event -> {
            System.out.println("Key pressed: " + event.getCode().getCode());
            if (event.getCode().getCode() == 49) { // 1
                Car car = controller.getGameController().getGameConfiguration().getCars().get(0);
                Player player = controller.getGameController().getGameConfiguration().getPlayers().get(0);
                controller.getGameController().findAndAddCar(player, car.getChipIds().get(0));
            } else if (event.getCode().getCode() == 50) { // 2
                Car car = controller.getGameController().getGameConfiguration().getCars().get(1);
                Player player = controller.getGameController().getGameConfiguration().getPlayers().get(1);
                controller.getGameController().findAndAddCar(player, car.getChipIds().get(0));
            } else if (event.getCode().getCode() == 51) { // 3
                Car car = controller.getGameController().getGameConfiguration().getCars().get(2);
                Player player = controller.getGameController().getGameConfiguration().getPlayers().get(2);
                controller.getGameController().findAndAddCar(player, car.getChipIds().get(0));
            } else if (event.getCode().getCode() == 52) { // 4
                Car car = controller.getGameController().getGameConfiguration().getCars().get(3);
                Player player = controller.getGameController().getGameConfiguration().getPlayers().get(3);
                controller.getGameController().findAndAddCar(player, car.getChipIds().get(0));
            }
        });
    }

    @Override
    public void setupModelToUiBindings(MainModel model) {
        onChangeOf(model.players).execute((oldValue, newValue) -> {
            for (int i = 0; i < model.players.getValue().size() && i < playerPanes.length; i++) {
                Player player          = model.players.getValue().get(i);
                Label  playerInfoLabel = (Label) ((StackPane) playerPanes[i]).getChildren().get(3);
                String carName         = player.getSelectedCar() != null ? player.getSelectedCar().getName() : "No Car";
                playerInfoLabel.setText(player.getTranslationPropName() + " - " + carName);
            }
        });
        onChangeOf(model.updatePlayers).execute((oldValue, newValue) -> {
            for (int i = 0; i < model.players.getValue().size() && i < carImageViews.length; i++) {
                Player player = model.players.getValue().get(i);
                if (player.getSelectedCar() != null) {
                    String carName = player.getSelectedCar().getName();
                    if (carName != null && !carName.isEmpty()) {
                        String imagePath = "/mvc/renewablecharge/images/cars/" + carName + ".png";
                        Image  newImage  = new Image(imagePath, true);
                        carImageViews[i].setImage(newImage);
                        Label playerInfoLabel = (Label) ((StackPane) playerPanes[i]).getChildren().get(3);
                        playerInfoLabel.setText(player.getTranslationPropName() + " - " + carName);
                    }
                } else {
                    carImageViews[i].setImage(null);
                }
            }
        });
        onChangeOf(model.countdown)
                .execute((oldValue, newValue) -> {
                    countdown.setText(newValue.toString());
                });
    }
}