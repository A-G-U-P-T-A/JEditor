package org.node.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import org.node.model.Node;
import org.node.model.Pin;
import java.util.HashMap;
import java.util.Map;

public class NodeView extends Region {
    private Node node;
    private VBox content;
    private Map<Pin, PinView> pinViews;
    private static final double NODE_WIDTH = 200;
    private static final double HEADER_HEIGHT = 30;

    public NodeView(Node node) {
        this.node = node;
        this.pinViews = new HashMap<>();
        setupNodeView();
    }

    private void setupNodeView() {
        content = new VBox(5);
        content.setPadding(new Insets(5));
        content.setStyle("-fx-background-color: #2D2D2D; " +
                        "-fx-background-radius: 5; " +
                        "-fx-border-color: #3D3D3D; " +
                        "-fx-border-radius: 5; " +
                        "-fx-border-width: 1;");

        // Header
        Label titleLabel = new Label(node.getTitle());
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        titleLabel.setPrefHeight(HEADER_HEIGHT);
        content.getChildren().add(titleLabel);

        // Pins
        for (Pin pin : node.getInputPins()) {
            PinView pinView = new PinView(pin);
            pinViews.put(pin, pinView);
            HBox pinContainer = new HBox(5);
            pinContainer.getChildren().addAll(pinView, new Label(pin.getLabel()));
            content.getChildren().add(pinContainer);
        }

        for (Pin pin : node.getOutputPins()) {
            PinView pinView = new PinView(pin);
            pinViews.put(pin, pinView);
            HBox pinContainer = new HBox(5);
            Label label = new Label(pin.getLabel());
            Region spacer = new Region();
            spacer.setPrefWidth(NODE_WIDTH - 50);
            pinContainer.getChildren().addAll(spacer, label, pinView);
            content.getChildren().add(pinContainer);
        }

        getChildren().add(content);
        setLayoutX(node.getPosition().getX());
        setLayoutY(node.getPosition().getY());
    }

    public PinView getPinView(Pin pin) {
        return pinViews.get(pin);
    }

    public Node getNode() {
        return node;
    }
}
