package org.node.view;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import org.node.model.ClassScanner;
import org.node.model.Node;

import java.util.List;
import java.util.function.Consumer;

public class NodePalette extends VBox {
    private final VBox nodeList;
    private final TextField searchField;
    private final Consumer<Node> onNodeSelected;
    private List<Class<?>> availableClasses;

    public NodePalette(Consumer<Node> onNodeSelected) {
        this.onNodeSelected = onNodeSelected;
        setPrefWidth(250);
        setStyle("-fx-background-color: #2A2A2A; -fx-padding: 10;");

        // Search field
        searchField = new TextField();
        searchField.setPromptText("Search nodes...");
        searchField.setStyle("-fx-background-color: #3D3D3D; -fx-text-fill: white;");
        searchField.textProperty().addListener((obs, old, newValue) -> filterNodes(newValue));

        // Node list
        nodeList = new VBox(5);
        ScrollPane scrollPane = new ScrollPane(nodeList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #2A2A2A;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(searchField, scrollPane);
        loadNodes();
    }

    private void loadNodes() {
        availableClasses = ClassScanner.scanJavaUtilClasses();
        refreshNodeList();
    }

    private void filterNodes(String filter) {
        nodeList.getChildren().clear();
        String filterLower = filter.toLowerCase();
        
        availableClasses.stream()
            .filter(cls -> cls.getSimpleName().toLowerCase().contains(filterLower))
            .forEach(this::addNodeButton);
    }

    private void refreshNodeList() {
        nodeList.getChildren().clear();
        availableClasses.forEach(this::addNodeButton);
    }

    private void addNodeButton(Class<?> cls) {
        Label nodeLabel = new Label(cls.getSimpleName());
        nodeLabel.setStyle("-fx-background-color: #3D3D3D; -fx-text-fill: white; " +
                          "-fx-padding: 5 10; -fx-background-radius: 3;");
        nodeLabel.setPrefWidth(230);
        
        nodeLabel.setOnMouseEntered(e -> 
            nodeLabel.setStyle("-fx-background-color: #4D4D4D; -fx-text-fill: white; " +
                             "-fx-padding: 5 10; -fx-background-radius: 3;"));
        
        nodeLabel.setOnMouseExited(e -> 
            nodeLabel.setStyle("-fx-background-color: #3D3D3D; -fx-text-fill: white; " +
                             "-fx-padding: 5 10; -fx-background-radius: 3;"));

        nodeLabel.setOnMouseClicked(e -> {
            Node node = ClassScanner.createNodeFromClass(cls);
            onNodeSelected.accept(node);
        });

        nodeList.getChildren().add(nodeLabel);
    }
}
