package org.node;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.input.MouseButton;
import org.node.model.*;
import org.node.view.*;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

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
        setupCanvasHandlers();
    }

    private void setupCanvasNavigation() {
        canvas.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.MIDDLE) {
                lastMousePosition = new Point2D(e.getSceneX(), e.getSceneY());
                canvas.setCursor(javafx.scene.Cursor.CLOSED_HAND);
            }
        });

        canvas.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.MIDDLE && lastMousePosition != null) {
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
            if (e.getButton() == MouseButton.MIDDLE) {
                lastMousePosition = null;
                canvas.setCursor(javafx.scene.Cursor.DEFAULT);
            }
        });
    }

    private void setupCanvasHandlers() {
        canvas.setOnDragOver(event -> {
            if (event.getGestureSource() != canvas && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        canvas.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                String[] parts = db.getString().split("\\.");
                String className = parts[0];
                String methodName = parts[1];
                
                try {
                    Class<?> cls = Class.forName("java.util." + className);
                    if (methodName.equals("Create")) {
                        // Create constructor node
                        Constructor<?> constructor = cls.getConstructors()[0];
                        Node node = ClassScanner.createConstructorNode(constructor, new Point2D(event.getX(), event.getY()));
                        NodeView nodeView = new NodeView(node);
                        addNodeToCanvas(nodeView);
                    } else {
                        // Create method node
                        Method method = Arrays.stream(cls.getMethods())
                            .filter(m -> m.getName().equals(methodName))
                            .findFirst()
                            .orElse(null);
                        
                        if (method != null) {
                            Node node = ClassScanner.createMethodNode(method, new Point2D(event.getX(), event.getY()));
                            NodeView nodeView = new NodeView(node);
                            addNodeToCanvas(nodeView);
                        }
                    }
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void setupNodeDragging(NodeView nodeView) {
        final Point2D[] dragDelta = new Point2D[1];

        nodeView.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                dragDelta[0] = new Point2D(nodeView.getLayoutX() - e.getSceneX(), nodeView.getLayoutY() - e.getSceneY());
                e.consume();
            }
        });

        nodeView.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY && dragDelta[0] != null) {
                double newX = e.getSceneX() + dragDelta[0].getX();
                double newY = e.getSceneY() + dragDelta[0].getY();
                
                nodeView.setLayoutX(newX);
                nodeView.setLayoutY(newY);
                e.consume();
            }
        });

        nodeView.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                dragDelta[0] = null;
                e.consume();
            }
        });
    }

    private void addNodeToCanvas(NodeView nodeView) {
        setupNodeDragging(nodeView);
        nodeViews.add(nodeView);
        canvas.getChildren().add(nodeView);
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
        // Setup node selection
        nodeExplorer = new NodeExplorer(nodeView -> {
            // Deselect all nodes
            nodeViews.forEach(nv -> nv.setSelected(false));
            // Select the clicked node
            nodeView.setSelected(true);
        });

        // Setup canvas navigation
        setupCanvasNavigation();

        // Setup canvas drop handling
        setupCanvasHandlers();

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

    private void setupStage(Stage primaryStage) {
        ScrollPane canvasScroll = new ScrollPane(canvas);
        canvasScroll.setPannable(true);
        canvasScroll.setFitToWidth(true);
        canvasScroll.setFitToHeight(true);

        // Create node palette
        NodePalette palette = new NodePalette();
        palette.setOnNodeCreated((className, methodName, x, y) -> {
            try {
                Class<?> cls = Class.forName(className);
                if ("Create".equals(methodName)) {
                    // Create constructor node
                    Constructor<?> constructor = cls.getConstructors()[0];
                    Node node = ClassScanner.createConstructorNode(constructor, new Point2D(x, y));
                    NodeView nodeView = new NodeView(node);
                    addNodeToCanvas(nodeView);
                } else {
                    // Create method node
                    Method method = Arrays.stream(cls.getMethods())
                            .filter(m -> m.getName().equals(methodName))
                            .findFirst()
                            .orElse(null);
                    if (method != null) {
                        Node node = ClassScanner.createMethodNode(method, new Point2D(x, y));
                        NodeView nodeView = new NodeView(node);
                        addNodeToCanvas(nodeView);
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        // Create node explorer
        nodeExplorer = new NodeExplorer(nodeView -> {
            // Deselect all nodes
            nodeViews.forEach(nv -> nv.setSelected(false));
            // Select the clicked node
            nodeView.setSelected(true);
        });

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

    public static void main(String[] args) {
        launch(args);
    }
}