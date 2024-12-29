package org.node;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.node.model.*;
import org.node.view.*;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.io.File;
import java.io.IOException;

public class Main extends Application {
    private final Pane canvas;
    private final List<NodeView> nodeViews = new ArrayList<>(); // Initialize here instead
    private List<ConnectionView> connectionViews;
    private PinView dragSourcePin;
    private ConnectionView previewConnection;
    private Point2D lastMousePosition;
    private NodeExplorer nodeExplorer;

    public Main() {
        canvas = new Pane();
        canvas.setStyle("-fx-background-color: #1E1E1E;");
        connectionViews = new ArrayList<>();
        dragSourcePin = null;
        previewConnection = null;
        lastMousePosition = null;
        nodeExplorer = null;
    }

    @Override
    public void start(Stage primaryStage) {
        initializeCanvas();
        setupStage(primaryStage);
    }

    private void initializeCanvas() {
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
        // Setup node interaction
        setupNodeDragging(nodeView);
        
        // Add to canvas and list
        nodeViews.add(nodeView);
        canvas.getChildren().add(nodeView);
        
        // Update explorer immediately
        if (nodeExplorer != null) {
            Platform.runLater(() -> {
                List<NodeView> currentNodes = new ArrayList<>(nodeViews);
                nodeExplorer.updateNodeList(currentNodes);
            });
        }
        
        // Add selection handling
        nodeView.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                // Handle multi-selection with CTRL key
                if (!e.isControlDown()) {
                    nodeViews.forEach(nv -> nv.setSelected(false));
                }
                nodeView.setSelected(!nodeView.isSelected());
                
                // Update explorer to reflect selection
                if (nodeExplorer != null) {
                    nodeExplorer.updateNodeList(new ArrayList<>(nodeViews));
                }
            }
        });
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
        System.out.println("Initial nodeViews size: " + nodeViews.size());

        // Create menu bar
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        
        MenuItem newProject = new MenuItem("New Project");
        newProject.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Project Directory");
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            
            if (selectedDirectory != null) {
                try {
                    Project project = Project.createNew(selectedDirectory.getAbsolutePath());
                    System.out.println("Created new project at: " + project.getRootPath());
                    // TODO: Update UI with project info
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Project Creation Failed");
                    alert.setContentText("Failed to create project: " + ex.getMessage());
                    alert.showAndWait();
                }
            }
        });
        
        MenuItem openProject = new MenuItem("Open Project");
        MenuItem saveProject = new MenuItem("Save Project");
        
        fileMenu.getItems().addAll(newProject, openProject, saveProject);
        menuBar.getMenus().add(fileMenu);

        // Create main content area
        HBox mainContent = new HBox(10);  
        mainContent.setStyle("-fx-background-color: #1E1E1E;");
        
        // Create node palette for left panel
        NodePalette palette = new NodePalette();
        palette.setOnNodeCreated((className, methodName, x, y) -> {
            try {
                System.out.println("\n=== Creating New Node ===");
                System.out.println("Class: " + className);
                System.out.println("Method: " + methodName);
                System.out.println("Position: " + x + "," + y);
                
                Class<?> cls = Class.forName(className);
                Node node;
                if ("Create".equals(methodName)) {
                    Constructor<?> constructor = cls.getConstructors()[0];
                    node = ClassScanner.createConstructorNode(constructor, new Point2D(x, y));
                } else {
                    Method method = Arrays.stream(cls.getMethods())
                            .filter(m -> m.getName().equals(methodName))
                            .findFirst()
                            .orElseThrow();
                    node = ClassScanner.createMethodNode(method, new Point2D(x, y));
                }

                NodeView nodeView = new NodeView(node);
                nodeView.setLayoutX(x);
                nodeView.setLayoutY(y);
                
                addNodeToCanvas(nodeView);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        // Add canvas to a scroll pane
        ScrollPane canvasScroll = new ScrollPane(canvas);
        canvasScroll.setFitToWidth(true);
        canvasScroll.setFitToHeight(true);
        HBox.setHgrow(canvasScroll, Priority.ALWAYS);
        
        // Create node explorer
        nodeExplorer = new NodeExplorer(this::focusOnNode);
        nodeExplorer.updateNodeList(new ArrayList<>(nodeViews));
        
        mainContent.getChildren().addAll(palette, canvasScroll, nodeExplorer);

        // Create root layout
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #1E1E1E;");
        root.getChildren().addAll(menuBar, mainContent);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setTitle("Blueprint Node Editor");
        primaryStage.setScene(scene);
        primaryStage.show();

        setupInteractions();
    }

    public static void main(String[] args) {
        launch(args);
    }
}