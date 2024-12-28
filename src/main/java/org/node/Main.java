package org.node;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.input.MouseButton;
import org.node.model.*;
import org.node.view.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private Pane canvas;
    private List<NodeView> nodeViews;
    private List<ConnectionView> connectionViews;
    private PinView dragSourcePin;
    private ConnectionView previewConnection;
    private Point2D lastMousePosition;
    private NodeExplorer nodeExplorer;

    @Override
    public void start(Stage primaryStage) {
        initializeCanvas();
        setupStage(primaryStage);
    }

    private void initializeCanvas() {
        canvas = new Pane();
        canvas.setStyle("-fx-background-color: #1E1E1E;");
        nodeViews = new ArrayList<>();
        connectionViews = new ArrayList<>();

        // Setup canvas navigation
        setupCanvasNavigation();
    }

    private void setupCanvasNavigation() {
        canvas.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                lastMousePosition = new Point2D(e.getSceneX(), e.getSceneY());
                canvas.setCursor(javafx.scene.Cursor.CLOSED_HAND);
            }
        });

        canvas.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY && lastMousePosition != null) {
                double deltaX = e.getSceneX() - lastMousePosition.getX();
                double deltaY = e.getSceneY() - lastMousePosition.getY();

                // Move all nodes
                nodeViews.forEach(nodeView -> {
                    nodeView.setLayoutX(nodeView.getLayoutX() + deltaX);
                    nodeView.setLayoutY(nodeView.getLayoutY() + deltaY);
                });

                lastMousePosition = new Point2D(e.getSceneX(), e.getSceneY());
            }
        });

        canvas.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                lastMousePosition = null;
                canvas.setCursor(javafx.scene.Cursor.DEFAULT);
            }
        });
    }

    private void addNodeToCanvas(Node node) {
        // Set initial position in the center of the visible canvas
        if (node.getPosition() == null) {
            double x = canvas.getWidth() / 2;
            double y = canvas.getHeight() / 2;
            node.setPosition(new Point2D(x, y));
        }

        NodeView nodeView = new NodeView(node);
        nodeViews.add(nodeView);
        canvas.getChildren().add(nodeView);
        
        // Make node draggable
        makeDraggable(nodeView);

        // Update node explorer
        nodeExplorer.updateNodeList(nodeViews);
    }

    private void focusOnNode(NodeView nodeView) {
        // Calculate the center of the scroll viewport
        ScrollPane scrollPane = (ScrollPane) canvas.getParent();
        double viewportCenterX = scrollPane.getViewportBounds().getWidth() / 2;
        double viewportCenterY = scrollPane.getViewportBounds().getHeight() / 2;

        // Calculate the position to center the node
        double nodeX = nodeView.getLayoutX();
        double nodeY = nodeView.getLayoutY();

        // Adjust all nodes to center the selected node
        double deltaX = viewportCenterX - nodeX;
        double deltaY = viewportCenterY - nodeY;

        nodeViews.forEach(nv -> {
            nv.setLayoutX(nv.getLayoutX() + deltaX);
            nv.setLayoutY(nv.getLayoutY() + deltaY);
        });

        // Highlight the selected node temporarily
        String originalStyle = nodeView.getStyle();
        nodeView.setStyle(originalStyle + "-fx-effect: dropshadow(gaussian, #00ff00, 10, 0.5, 0, 0);");
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
        pause.setOnFinished(e -> nodeView.setStyle(originalStyle));
        pause.play();
    }

    private void setupInteractions() {
        // Pin drag interaction
        canvas.setOnMouseMoved(e -> {
            if (previewConnection != null) {
                previewConnection.setEndX(e.getX());
                previewConnection.setEndY(e.getY());
                previewConnection.setControlX2(e.getX() - 100);
                previewConnection.setControlY2(e.getY());
            }
        });

        canvas.setOnMouseReleased(e -> {
            if (previewConnection != null) {
                canvas.getChildren().remove(previewConnection);
                previewConnection = null;
            }
        });
    }

    private void makeDraggable(NodeView nodeView) {
        final Delta dragDelta = new Delta();

        nodeView.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                dragDelta.x = nodeView.getLayoutX() - e.getSceneX();
                dragDelta.y = nodeView.getLayoutY() - e.getSceneY();
                e.consume();
            }
        });

        nodeView.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                nodeView.setLayoutX(e.getSceneX() + dragDelta.x);
                nodeView.setLayoutY(e.getSceneY() + dragDelta.y);
                e.consume();
            }
        });
    }

    private void setupStage(Stage primaryStage) {
        ScrollPane canvasScroll = new ScrollPane(canvas);
        canvasScroll.setPannable(true);
        canvasScroll.setFitToWidth(true);
        canvasScroll.setFitToHeight(true);

        // Create node palette
        NodePalette palette = new NodePalette(this::addNodeToCanvas);

        // Create node explorer
        nodeExplorer = new NodeExplorer(this::focusOnNode);

        // Create main layout
        HBox root = new HBox();
        root.getChildren().addAll(palette, canvasScroll, nodeExplorer);
        HBox.setHgrow(canvasScroll, javafx.scene.layout.Priority.ALWAYS);

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("Blueprint Node Editor");
        primaryStage.setScene(scene);
        primaryStage.show();

        setupInteractions();
    }

    // Helper class for drag functionality
    private static class Delta {
        double x, y;
    }

    public static void main(String[] args) {
        launch(args);
    }
}