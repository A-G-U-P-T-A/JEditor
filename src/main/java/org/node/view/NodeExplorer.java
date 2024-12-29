package org.node.view;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
        setPrefWidth(250);
        setStyle("-fx-background-color: #2D2D2D;");

        // Create title
        Label title = new Label("Node Explorer");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");

        // Create node list
        nodeList = new ListView<>();
        nodeList.setStyle("-fx-background-color: #2D2D2D;");
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
                        nodeView.getParent().getChildrenUnmodifiable().remove(nodeView);
                        updateNodeList(getListView().getItems().filtered(nv -> nv != nodeView));
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

        getChildren().addAll(title, nodeList);
    }

    public void updateNodeList(List<NodeView> nodes) {
        nodeList.getItems().setAll(nodes);
    }
}
