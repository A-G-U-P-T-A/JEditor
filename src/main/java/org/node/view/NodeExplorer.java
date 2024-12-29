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
    private ScrollPane scrollPane;

    public NodeExplorer(Consumer<NodeView> onNodeSelected) {
        this.onNodeSelected = onNodeSelected;
        setupUI();
    }

    private void setupUI() {
        // Force size and make it very visible
        setPrefWidth(300);
        setMinWidth(300);
        setMaxWidth(300);
        setPrefHeight(800);
        setMinHeight(800);
        
        setStyle("-fx-background-color: #2D2D2D; -fx-border-color: red; -fx-border-width: 2;");
        setPadding(new Insets(10));
        setSpacing(10);

        // Debug header
        Label header = new Label("NODE EXPLORER");
        header.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");
        
        // Counter
        counterLabel = new Label("NO NODES");
        counterLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: yellow;");

        // Container for nodes
        nodeContainer = new VBox(5);
        nodeContainer.setStyle("""
            -fx-background-color: #3D3D3D;
            -fx-padding: 10;
            -fx-border-color: lime;
            -fx-border-width: 2;
        """);
        nodeContainer.setMinHeight(600);
        nodeContainer.setPrefHeight(600);
        
        // Wrap in ScrollPane with visible viewport
        scrollPane = new ScrollPane(nodeContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("""
            -fx-background: #3D3D3D;
            -fx-background-color: #3D3D3D;
            -fx-border-color: yellow;
            -fx-border-width: 2;
        """);
        scrollPane.setMinHeight(600);
        scrollPane.setPrefHeight(600);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Make viewport visible
        scrollPane.setViewportBounds(new javafx.geometry.BoundingBox(0, 0, 280, 600));
        
        getChildren().addAll(header, counterLabel, scrollPane);
        
        // Add a test label to verify visibility
        Label testLabel = new Label("Container Test Label");
        testLabel.setStyle("-fx-text-fill: lime; -fx-font-size: 14px;");
        nodeContainer.getChildren().add(testLabel);
    }

    public void updateNodeList(List<NodeView> nodes) {
        System.out.println("\n=== NodeExplorer Debug ===");
        System.out.println("Received nodes list size: " + nodes.size());
        System.out.println("Current container children: " + nodeContainer.getChildren().size());
        
        nodeContainer.getChildren().clear();
        System.out.println("Cleared container, new size: " + nodeContainer.getChildren().size());
        
        // Add each node as a simple white label
        for (NodeView nodeView : nodes) {
            String title = nodeView.getNode().getTitle();
            System.out.println("Creating label for node: " + title);
            
            Label nodeLabel = new Label(title);
            nodeLabel.setStyle("""
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-background-color: #4D4D4D;
                -fx-padding: 8;
                -fx-background-radius: 4;
                -fx-border-color: #666666;
                -fx-border-radius: 4;
                -fx-border-width: 1;
                -fx-min-height: 30;
                -fx-pref-height: 30;
            """);
            nodeLabel.setMaxWidth(Double.MAX_VALUE);
            
            // Add hover effect
            nodeLabel.setOnMouseEntered(e -> 
                nodeLabel.setStyle(nodeLabel.getStyle() + "-fx-background-color: #5D5D5D;"));
            nodeLabel.setOnMouseExited(e -> 
                nodeLabel.setStyle(nodeLabel.getStyle() + "-fx-background-color: #4D4D4D;"));
                
            // Add click handler
            nodeLabel.setOnMouseClicked(e -> {
                if (onNodeSelected != null) {
                    onNodeSelected.accept(nodeView);
                }
            });
            
            nodeContainer.getChildren().add(nodeLabel);
            System.out.println("Added label to container, new size: " + nodeContainer.getChildren().size());
        }
        
        counterLabel.setText(nodes.size() + " NODES");
        System.out.println("Final container size: " + nodeContainer.getChildren().size());
        System.out.println("=== End Debug ===\n");
        
        // Force layout update
        nodeContainer.requestLayout();
        scrollPane.requestLayout();
        requestLayout();
    }
}
