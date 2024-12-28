package org.node.view;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import org.node.model.Node;
import java.util.List;
import java.util.function.Consumer;

public class NodeExplorer extends VBox {
    private final VBox nodeList;
    private final TextField searchField;
    private List<NodeView> nodeViews;
    private final Consumer<NodeView> onNodeSelected;

    public NodeExplorer(Consumer<NodeView> onNodeSelected) {
        this.onNodeSelected = onNodeSelected;
        setPrefWidth(250);
        setStyle("-fx-background-color: #2A2A2A; -fx-padding: 10;");

        // Header
        Label header = new Label("Node Explorer");
        header.setStyle("-fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-padding: 0 0 10 0;");

        // Search field
        searchField = new TextField();
        searchField.setPromptText("Search nodes...");
        searchField.setStyle("-fx-background-color: #3D3D3D; -fx-text-fill: white;");
        searchField.textProperty().addListener((obs, old, newValue) -> filterNodes(newValue));

        // Node list
        nodeList = new VBox(5);
        ScrollPane scrollPane = new ScrollPane(nodeList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #2A2A2A; -fx-background-color: #2A2A2A;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(header, searchField, scrollPane);
    }

    public void updateNodeList(List<NodeView> nodeViews) {
        this.nodeViews = nodeViews;
        refreshNodeList();
    }

    private void filterNodes(String filter) {
        nodeList.getChildren().clear();
        String filterLower = filter.toLowerCase();
        
        nodeViews.stream()
            .filter(nodeView -> nodeView.getNode().getTitle().toLowerCase().contains(filterLower))
            .forEach(this::addNodeEntry);
    }

    private void refreshNodeList() {
        nodeList.getChildren().clear();
        nodeViews.forEach(this::addNodeEntry);
    }

    private void addNodeEntry(NodeView nodeView) {
        HBox entry = new HBox(10);
        entry.setStyle("-fx-background-color: #3D3D3D; -fx-padding: 5 10; -fx-background-radius: 3;");

        // Type indicator circle
        Circle typeIndicator = new Circle(5);
        typeIndicator.setFill(getColorForNodeType(nodeView.getNode().getType()));

        // Node title
        Label titleLabel = new Label(nodeView.getNode().getTitle());
        titleLabel.setStyle("-fx-text-fill: white;");
        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        entry.getChildren().addAll(typeIndicator, titleLabel);

        // Hover effect
        entry.setOnMouseEntered(e -> 
            entry.setStyle("-fx-background-color: #4D4D4D; -fx-padding: 5 10; -fx-background-radius: 3;"));
        
        entry.setOnMouseExited(e -> 
            entry.setStyle("-fx-background-color: #3D3D3D; -fx-padding: 5 10; -fx-background-radius: 3;"));

        // Click handler
        entry.setOnMouseClicked(e -> onNodeSelected.accept(nodeView));

        nodeList.getChildren().add(entry);
    }

    private javafx.scene.paint.Color getColorForNodeType(Node.NodeType type) {
        switch (type) {
            case FUNCTION: return javafx.scene.paint.Color.DODGERBLUE;
            case VARIABLE: return javafx.scene.paint.Color.LIGHTGREEN;
            case EVENT: return javafx.scene.paint.Color.RED;
            case CUSTOM_EVENT: return javafx.scene.paint.Color.ORANGE;
            case BRANCH: return javafx.scene.paint.Color.PURPLE;
            case LOOP: return javafx.scene.paint.Color.YELLOW;
            default: return javafx.scene.paint.Color.GRAY;
        }
    }
}
