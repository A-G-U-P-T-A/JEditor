package org.node.view;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.collections.FXCollections;
import java.util.List;
import java.util.function.Consumer;
import org.node.model.Node;

public class NodeExplorer extends VBox {
    private ListView<NodeView> nodeList;
    private Consumer<NodeView> onNodeSelected;
    private Consumer<NodeView> onNodeDeleted;

    public NodeExplorer(Consumer<NodeView> onNodeSelected) {
        this.onNodeSelected = onNodeSelected;
        setupUI();
    }

    public void setOnNodeDeleted(Consumer<NodeView> onNodeDeleted) {
        this.onNodeDeleted = onNodeDeleted;
    }

    private void setupUI() {
        setPrefWidth(250);
        setStyle("-fx-background-color: #2D2D2D;");

        // Create title
        Label title = new Label("Node Explorer");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");

        // Create node list
        nodeList = new ListView<>();
        nodeList.setPrefHeight(2000); // Make it tall enough
        nodeList.setStyle("-fx-background-color: #2D2D2D; -fx-control-inner-background: #2D2D2D;");
        VBox.setVgrow(nodeList, Priority.ALWAYS);

        // Setup cell factory for custom rendering
        nodeList.setCellFactory(lv -> new ListCell<NodeView>() {
            private Button deleteButton;
            private HBox content;

            {
                deleteButton = new Button("X");
                deleteButton.setStyle("-fx-background-color: #FF4444; -fx-text-fill: white;");
                content = new HBox(5);
                content.setStyle("-fx-padding: 5;");
            }

            @Override
            protected void updateItem(NodeView nodeView, boolean empty) {
                super.updateItem(nodeView, empty);
                if (empty || nodeView == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Create label for node name
                    Label nameLabel = new Label(nodeView.getNode().getTitle());
                    nameLabel.setStyle("-fx-text-fill: white;");
                    HBox.setHgrow(nameLabel, Priority.ALWAYS);

                    // Setup delete button action
                    deleteButton.setOnAction(e -> {
                        if (onNodeDeleted != null) {
                            onNodeDeleted.accept(nodeView);
                        }
                    });

                    // Add label and delete button to content
                    content.getChildren().setAll(nameLabel, deleteButton);
                    setGraphic(content);
                    setStyle("-fx-background-color: transparent;");

                    // Setup click handler
                    setOnMouseClicked(e -> onNodeSelected.accept(nodeView));
                }
            }
        });

        // Add search field
        TextField searchField = new TextField();
        searchField.setPromptText("Search nodes...");
        searchField.setStyle("-fx-background-color: #3D3D3D; -fx-text-fill: white;");

        getChildren().addAll(title, searchField, nodeList);
    }

    public void updateNodeList(List<NodeView> nodes) {
        System.out.println("Updating node list with " + nodes.size() + " nodes");  
        for (NodeView node : nodes) {
            System.out.println(" - " + node.getNode().getTitle());  
        }
        nodeList.setItems(FXCollections.observableArrayList(nodes));
    }
}
