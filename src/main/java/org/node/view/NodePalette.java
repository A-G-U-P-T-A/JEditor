package org.node.view;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.node.model.*;
import java.util.List;
import java.util.function.Consumer;
import javafx.geometry.Point2D;
import java.lang.reflect.Method;

public class NodePalette extends VBox {
    private TreeView<String> treeView;
    private Consumer<Node> onNodeCreated;

    public NodePalette(Consumer<Node> onNodeCreated) {
        this.onNodeCreated = onNodeCreated;
        setupUI();
    }

    private void setupUI() {
        setPrefWidth(200);
        setStyle("-fx-background-color: #2D2D2D;");

        // Create search field
        TextField searchField = new TextField();
        searchField.setPromptText("Search nodes...");
        searchField.setStyle("-fx-background-color: #3D3D3D; -fx-text-fill: white;");

        // Create tree view
        treeView = new TreeView<>();
        treeView.setStyle("-fx-background-color: #2D2D2D;");
        VBox.setVgrow(treeView, javafx.scene.layout.Priority.ALWAYS);

        // Set cell factory for tree view to style cells
        treeView.setCellFactory(tv -> {
            TreeCell<String> cell = new TreeCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                        setOnDragDetected(null);
                    } else {
                        setText(item);
                        setStyle("-fx-text-fill: white; -fx-background-color: transparent;");
                        
                        // Highlight on hover
                        setOnMouseEntered(e -> setStyle("-fx-text-fill: white; -fx-background-color: #3D3D3D;"));
                        setOnMouseExited(e -> setStyle("-fx-text-fill: white; -fx-background-color: transparent;"));
                        
                        // Setup drag and drop
                        if (getTreeItem() != null && getTreeItem().getParent() != null && 
                            !getTreeItem().getParent().getValue().equals("Java Util Classes")) {
                            setOnDragDetected(event -> {
                                javafx.scene.input.Dragboard db = startDragAndDrop(javafx.scene.input.TransferMode.COPY);
                                javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                                content.putString(getTreeItem().getParent().getValue() + "." + getItem());
                                db.setContent(content);
                                event.consume();
                            });
                        }
                    }
                }
            };
            return cell;
        });

        // Populate tree with classes
        populateTree();

        // Add components
        getChildren().addAll(searchField, treeView);

        // Setup search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTree(newValue);
        });
    }

    private void populateTree() {
        TreeItem<String> root = new TreeItem<>("Java Util Classes");
        root.setExpanded(true);

        List<Class<?>> classes = ClassScanner.scanJavaUtilClasses();
        for (Class<?> cls : classes) {
            TreeItem<String> classNode = new TreeItem<>(cls.getSimpleName());
            
            // Add constructor
            TreeItem<String> constructor = new TreeItem<>("Create");
            classNode.getChildren().add(constructor);
            
            // Add methods
            for (Method method : ClassScanner.getRelevantMethods(cls)) {
                if (!method.getName().startsWith("get") && !method.getName().startsWith("set")) {
                    TreeItem<String> methodNode = new TreeItem<>(method.getName());
                    classNode.getChildren().add(methodNode);
                }
            }
            
            root.getChildren().add(classNode);
        }

        treeView.setRoot(root);
    }

    private void filterTree(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            populateTree();
            return;
        }

        TreeItem<String> root = new TreeItem<>("Java Util Classes");
        root.setExpanded(true);

        List<Class<?>> classes = ClassScanner.scanJavaUtilClasses();
        for (Class<?> cls : classes) {
            if (cls.getSimpleName().toLowerCase().contains(searchText.toLowerCase())) {
                TreeItem<String> classNode = new TreeItem<>(cls.getSimpleName());
                for (Method method : ClassScanner.getRelevantMethods(cls)) {
                    if (!method.getName().startsWith("get") && !method.getName().startsWith("set")) {
                        TreeItem<String> methodNode = new TreeItem<>(method.getName());
                        classNode.getChildren().add(methodNode);
                    }
                }
                root.getChildren().add(classNode);
            }
        }

        treeView.setRoot(root);
    }
}
