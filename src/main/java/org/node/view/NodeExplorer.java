package org.node.view;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.collections.FXCollections;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
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
        setPadding(new Insets(10));
        setSpacing(10);

        // Create title
        Label title = new Label("Node Explorer");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        // Add search field
        TextField searchField = new TextField();
        searchField.setPromptText("Search nodes...");
        searchField.setStyle("-fx-background-color: #3D3D3D; -fx-text-fill: white; -fx-prompt-text-fill: #808080;");

        // Create node list
        nodeList = new ListView<>();
        nodeList.setStyle("""
            -fx-background-color: #2D2D2D;
            -fx-control-inner-background: #2D2D2D;
            -fx-border-color: #3D3D3D;
            -fx-border-width: 1;
            -fx-padding: 5;
        """);
        VBox.setVgrow(nodeList, Priority.ALWAYS);

        // Setup cell factory for custom rendering
        nodeList.setCellFactory(lv -> new ListCell<NodeView>() {
            {
                // Set cell background to be transparent
                setStyle("-fx-background-color: transparent;");
            }

            @Override
            protected void updateItem(NodeView nodeView, boolean empty) {
                super.updateItem(nodeView, empty);
                
                if (empty || nodeView == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    // Create container for the cell content
                    HBox container = new HBox(10);
                    container.setPadding(new Insets(5));
                    container.setStyle("-fx-background-color: #3D3D3D; -fx-background-radius: 3;");

                    // Create label for node name
                    Label nameLabel = new Label(nodeView.getNode().getTitle());
                    nameLabel.setStyle("-fx-text-fill: white;");
                    HBox.setHgrow(nameLabel, Priority.ALWAYS);

                    // Create delete button
                    Button deleteButton = new Button("×");
                    deleteButton.setStyle("""
                        -fx-background-color: #FF4444;
                        -fx-text-fill: white;
                        -fx-font-size: 14px;
                        -fx-padding: 2 8;
                        -fx-background-radius: 3;
                    """);
                    
                    deleteButton.setOnAction(e -> {
                        if (onNodeDeleted != null) {
                            onNodeDeleted.accept(nodeView);
                        }
                    });

                    // Add components to container
                    container.getChildren().addAll(nameLabel, deleteButton);
                    
                    // Set the container as the cell's graphic
                    setGraphic(container);

                    // Handle selection
                    container.setOnMouseClicked(e -> {
                        if (onNodeSelected != null) {
                            onNodeSelected.accept(nodeView);
                            System.out.println("Node selected: " + nodeView.getNode().getTitle());
                        }
                    });
                }
            }
        });

        getChildren().addAll(title, searchField, nodeList);
    }

    public void updateNodeList(List<NodeView> nodes) {
        System.out.println("NodeExplorer: Updating node list with " + nodes.size() + " nodes");
        for (NodeView node : nodes) {
            System.out.println("NodeExplorer: - " + node.getNode().getTitle());
        }
        nodeList.setItems(FXCollections.observableArrayList(nodes));
        nodeList.refresh();
    }
}
