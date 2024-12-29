package org.node.view;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class NavigationBar extends HBox {
    private final Stage stage;
    private Runnable onNewProject;
    private Runnable onExit;

    public NavigationBar(Stage stage) {
        this.stage = stage;
        setupUI();
    }

    private void setupUI() {
        MenuBar menuBar = new MenuBar();
        
        // File Menu
        Menu fileMenu = new Menu("File");
        
        MenuItem newProject = new MenuItem("New Project");
        newProject.setOnAction(e -> {
            if (onNewProject != null) onNewProject.run();
        });

        MenuItem openProject = new MenuItem("Open Project");
        openProject.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Project");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Node Editor Projects", "*.nedp")
            );
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                // TODO: Implement project loading
                System.out.println("Opening project: " + file.getAbsolutePath());
            }
        });

        MenuItem saveProject = new MenuItem("Save Project");
        saveProject.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Project");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Node Editor Projects", "*.nedp")
            );
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                // TODO: Implement project saving
                System.out.println("Saving project to: " + file.getAbsolutePath());
            }
        });

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> {
            if (onExit != null) onExit.run();
        });

        SeparatorMenuItem separator = new SeparatorMenuItem();
        
        fileMenu.getItems().addAll(newProject, openProject, saveProject, separator, exit);
        menuBar.getMenus().add(fileMenu);
        
        getChildren().add(menuBar);
    }

    public void setOnNewProject(Runnable handler) {
        this.onNewProject = handler;
    }

    public void setOnExit(Runnable handler) {
        this.onExit = handler;
    }
}
