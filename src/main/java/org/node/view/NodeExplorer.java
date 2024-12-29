package org.node.view;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import java.util.*;
import java.util.function.Consumer;
import org.node.model.Node;

public class NodeExplorer extends VBox {
    private VBox nodeContainer;
    private Consumer<NodeView> onNodeSelected;
    private Label counterLabel;
    private Map<Node, Button> nodeButtons;
    private Set<NodeView> selectedNodes;

    public NodeExplorer(Consumer<NodeView> onNodeSelected) {
        this.onNodeSelected = onNodeSelected;
        this.nodeButtons = new HashMap<>();
        this.selectedNodes = new HashSet<>();
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

        // Container for nodes
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
        nodeContainer.getChildren().clear();
        nodeButtons.clear();
        
        for (NodeView nodeView : nodes) {
            Button nodeButton = createNodeButton(nodeView);
            nodeButtons.put(nodeView.getNode(), nodeButton);
            nodeContainer.getChildren().add(nodeButton);
            
            // Update button state if node is selected
            updateButtonState(nodeView);
        }
        
        int selectedCount = (int) nodes.stream().filter(NodeView::isSelected).count();
        String countText = nodes.size() + " NODES";
        if (selectedCount > 0) {
            countText += " (" + selectedCount + " selected)";
        }
        counterLabel.setText(countText);
    }
    
    private Button createNodeButton(NodeView nodeView) {
        Button button = new Button(nodeView.getNode().getTitle());
        button.setMaxWidth(Double.MAX_VALUE);
        
        button.setOnAction(e -> {
            if (onNodeSelected != null) {
                onNodeSelected.accept(nodeView);
                updateButtonState(nodeView);
            }
        });
        
        return button;
    }
    
    private void updateButtonState(NodeView nodeView) {
        Button button = nodeButtons.get(nodeView.getNode());
        if (button == null) return;
        
        if (nodeView.isSelected()) {
            button.setStyle("""
                -fx-background-color: #6a1b9a;
                -fx-text-fill: white;
                -fx-font-size: 12px;
                -fx-padding: 5 10;
                -fx-background-radius: 3;
                -fx-border-color: #9c27b0;
                -fx-border-radius: 3;
                -fx-border-width: 2;
                -fx-cursor: hand;
            """);
        } else {
            button.setStyle("""
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
        }
        
        // Add hover effects
        button.setOnMouseEntered(e -> {
            String currentStyle = button.getStyle();
            button.setStyle(currentStyle.replace(
                nodeView.isSelected() ? "#6a1b9a" : "#4D4D4D",
                nodeView.isSelected() ? "#7b1fa2" : "#5D5D5D"
            ));
        });
        
        button.setOnMouseExited(e -> {
            updateButtonState(nodeView);
        });
    }
}
