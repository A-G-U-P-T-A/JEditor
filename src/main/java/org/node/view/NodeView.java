package org.node.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import org.node.model.Node;
import org.node.model.Pin;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class NodeView extends Region {
    private Node node;
    private VBox content;
    private Map<Pin, PinView> pinViews;
    private static final double NODE_WIDTH = 200;
    private static final double HEADER_HEIGHT = 30;
    private static final int MAX_VISIBLE_PINS = 5;
    private boolean pinsExpanded = false;
    private VBox inputPinsBox;
    private VBox outputPinsBox;
    private boolean selected = false;
    private static final String STYLE_NORMAL = "-fx-background-color: #2D2D2D; -fx-background-radius: 5; -fx-border-color: #3D3D3D; -fx-border-radius: 5; -fx-border-width: 1;";
    private static final String STYLE_SELECTED = "-fx-background-color: #4D4D4D; -fx-background-radius: 5; -fx-border-color: #00A5E5; -fx-border-width: 2; -fx-border-radius: 5;";

    public NodeView(Node node) {
        this.node = node;
        this.pinViews = new HashMap<>();
        setupNodeView();
    }

    private void setupNodeView() {
        content = new VBox(5);
        content.setPadding(new Insets(5));
        content.setStyle(STYLE_NORMAL);

        // Header
        Label titleLabel = new Label(node.getTitle());
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        titleLabel.setPrefHeight(HEADER_HEIGHT);
        content.getChildren().add(titleLabel);

        // Input pins
        inputPinsBox = new VBox(5);
        setupPinSection(node.getInputPins(), inputPinsBox, true);
        content.getChildren().add(inputPinsBox);

        // Output pins
        outputPinsBox = new VBox(5);
        setupPinSection(node.getOutputPins(), outputPinsBox, false);
        content.getChildren().add(outputPinsBox);

        getChildren().add(content);
        setLayoutX(node.getPosition().getX());
        setLayoutY(node.getPosition().getY());
    }

    private void setupPinSection(List<Pin> pins, VBox pinBox, boolean isInput) {
        if (pins.isEmpty()) return;

        List<Pin> visiblePins = pins.subList(0, Math.min(MAX_VISIBLE_PINS, pins.size()));
        for (Pin pin : visiblePins) {
            addPinToBox(pin, pinBox, isInput);
        }

        if (pins.size() > MAX_VISIBLE_PINS) {
            Button expandButton = new Button((pins.size() - MAX_VISIBLE_PINS) + " more...");
            expandButton.setStyle("-fx-background-color: #3D3D3D; -fx-text-fill: white; -fx-cursor: hand;");
            
            expandButton.setOnAction(e -> {
                pinBox.getChildren().remove(expandButton);
                for (Pin pin : pins.subList(MAX_VISIBLE_PINS, pins.size())) {
                    addPinToBox(pin, pinBox, isInput);
                }
                
                // Add collapse button
                Button collapseButton = new Button("Show less");
                collapseButton.setStyle("-fx-background-color: #3D3D3D; -fx-text-fill: white; -fx-cursor: hand;");
                collapseButton.setOnAction(e2 -> {
                    pinBox.getChildren().clear();
                    for (Pin pin : visiblePins) {
                        addPinToBox(pin, pinBox, isInput);
                    }
                    pinBox.getChildren().add(expandButton);
                });
                pinBox.getChildren().add(collapseButton);
            });
            
            pinBox.getChildren().add(expandButton);
        }
    }

    private void addPinToBox(Pin pin, VBox pinBox, boolean isInput) {
        PinView pinView = new PinView(pin);
        pinViews.put(pin, pinView);
        HBox pinContainer = new HBox(5);
        Label label = new Label(pin.getLabel());
        label.setStyle("-fx-text-fill: white;"); // Make label text white

        if (isInput) {
            pinContainer.getChildren().addAll(pinView, label);
        } else {
            Region spacer = new Region();
            spacer.setPrefWidth(NODE_WIDTH - 50);
            pinContainer.getChildren().addAll(spacer, label, pinView);
        }
        pinBox.getChildren().add(pinContainer);
    }

    public PinView getPinView(Pin pin) {
        return pinViews.get(pin);
    }

    public Node getNode() {
        return node;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        content.setStyle(selected ? STYLE_SELECTED : STYLE_NORMAL);
    }

    public boolean isSelected() {
        return selected;
    }
}
