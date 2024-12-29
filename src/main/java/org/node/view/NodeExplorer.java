package org.node.view;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import java.util.List;
import java.util.function.Consumer;
import org.node.model.Node;

public class NodeExplorer extends VBox {
    private ListView<NodeView> nodeList;
    private Consumer<NodeView> onNodeSelected;

    public NodeExplorer(Consumer<NodeView> onNodeSelected) {
        this.onNodeSelected = onNodeSelected;
        setupUI();
    }

    private void setupUI() {
        // Make it VERY visible for debugging
        setPrefWidth(250);
        setMinWidth(250);
        setStyle("-fx-background-color: #2D2D2D; -fx-border-color: red; -fx-border-width: 2;");
        setPadding(new Insets(10));
        setSpacing(10);

        // Debug label
        Label title = new Label("Node Explorer (Debug)");
        title.setStyle("-fx-text-fill: #FF0000; -fx-font-size: 16px; -fx-font-weight: bold;");

        // Debug counter label
        Label counter = new Label("No nodes yet");
        counter.setStyle("-fx-text-fill: yellow;");

        // Create list with OBVIOUS styling
        nodeList = new ListView<>();
        nodeList.setMinHeight(200);
        nodeList.setPrefHeight(2000);
        nodeList.setStyle("""
            -fx-background-color: #3D3D3D;
            -fx-border-color: yellow;
            -fx-border-width: 2;
            -fx-control-inner-background: #3D3D3D;
        """);
        VBox.setVgrow(nodeList, Priority.ALWAYS);

        nodeList.setCellFactory(lv -> new ListCell<NodeView>() {
            @Override
            protected void updateItem(NodeView nodeView, boolean empty) {
                super.updateItem(nodeView, empty);
                if (empty || nodeView == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(nodeView.getNode().getTitle());
                    setStyle("-fx-text-fill: lime; -fx-font-size: 14px;");
                }
            }
        });

        nodeList.setOnMouseClicked(e -> {
            NodeView selected = nodeList.getSelectionModel().getSelectedItem();
            if (selected != null && onNodeSelected != null) {
                onNodeSelected.accept(selected);
                System.out.println("Node clicked in list: " + selected.getNode().getTitle());
            }
        });

        getChildren().addAll(title, counter, nodeList);
    }

    public void updateNodeList(List<NodeView> nodes) {
        System.out.println("DEBUG: Updating node list with " + nodes.size() + " nodes");
        nodeList.setItems(FXCollections.observableArrayList(nodes));
        
        // Update counter label
        ((Label)getChildren().get(1)).setText(nodes.size() + " nodes in list");
        
        // Force layout update
        nodeList.requestLayout();
        requestLayout();
    }
}
