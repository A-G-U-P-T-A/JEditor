package org.node.view;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import java.util.List;
import java.util.function.Consumer;
import org.node.model.Node;

public class NodeExplorer extends VBox {
    private VBox nodeContainer;
    private Consumer<NodeView> onNodeSelected;
    private Label counterLabel;

    public NodeExplorer(Consumer<NodeView> onNodeSelected) {
        this.onNodeSelected = onNodeSelected;
        setupUI();
    }

    private void setupUI() {
        // Set fixed size
        setPrefWidth(250);
        setMinWidth(250);
        setMaxWidth(250);
        
        // Debug styling
        setStyle("""
            -fx-background-color: #2D2D2D;
            -fx-border-color: red;
            -fx-border-width: 2;
            -fx-padding: 10;
            -fx-spacing: 10;
        """);

        // Header
        Label header = new Label("NODE EXPLORER");
        header.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");
        
        // Counter
        counterLabel = new Label("NO NODES");
        counterLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: yellow;");

        // Simple container for nodes
        nodeContainer = new VBox(5);
        nodeContainer.setStyle("""
            -fx-background-color: #3D3D3D;
            -fx-padding: 5;
            -fx-spacing: 5;
            -fx-border-color: lime;
            -fx-border-width: 1;
        """);
        VBox.setVgrow(nodeContainer, Priority.ALWAYS);

        getChildren().addAll(header, counterLabel, nodeContainer);
    }

    public void updateNodeList(List<NodeView> nodes) {
        System.out.println("\n=== NodeExplorer Debug ===");
        System.out.println("Updating node list with " + nodes.size() + " nodes");
        
        nodeContainer.getChildren().clear();
        
        // Add each node as a button instead of a label
        for (NodeView nodeView : nodes) {
            Button nodeButton = new Button(nodeView.getNode().getTitle());
            nodeButton.setStyle("""
                -fx-background-color: #4D4D4D;
                -fx-text-fill: white;
                -fx-font-size: 12px;
                -fx-padding: 5 10;
                -fx-background-radius: 3;
                -fx-border-color: #666666;
                -fx-border-radius: 3;
                -fx-border-width: 1;
                -fx-cursor: hand;
            """);
            nodeButton.setMaxWidth(Double.MAX_VALUE);
            
            // Add hover effect
            nodeButton.setOnMouseEntered(e -> 
                nodeButton.setStyle(nodeButton.getStyle() + "-fx-background-color: #5D5D5D;"));
            nodeButton.setOnMouseExited(e -> 
                nodeButton.setStyle(nodeButton.getStyle() + "-fx-background-color: #4D4D4D;"));
            
            // Add click handler
            nodeButton.setOnAction(e -> {
                if (onNodeSelected != null) {
                    onNodeSelected.accept(nodeView);
                }
            });
            
            nodeContainer.getChildren().add(nodeButton);
            System.out.println("Added button for node: " + nodeView.getNode().getTitle());
        }
        
        counterLabel.setText(nodes.size() + " NODES");
        System.out.println("=== Update Complete ===\n");
    }
}
