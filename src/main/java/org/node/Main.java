package org.node;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
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

    @Override
    public void start(Stage primaryStage) {
        initializeCanvas();
        createSampleNodes();
        setupInteractions();
        setupStage(primaryStage);
    }

    private void initializeCanvas() {
        canvas = new Pane();
        canvas.setStyle("-fx-background-color: #1E1E1E;");
        nodeViews = new ArrayList<>();
        connectionViews = new ArrayList<>();
    }

    private void createSampleNodes() {
        // Create a function node
        Node functionNode = createFunctionNode(100, 100);
        Node variableNode = createVariableNode(400, 100);
        Node printNode = createPrintNode(100, 300);

        // Add nodes to canvas
        addNodeToCanvas(functionNode);
        addNodeToCanvas(variableNode);
        addNodeToCanvas(printNode);
    }

    private Node createFunctionNode(double x, double y) {
        Node node = new Node("Calculate Sum", Node.NodeType.FUNCTION, new Point2D(x, y));
        node.addInputPin(new Pin("exec_in", Pin.PinType.FLOW, "Exec", true));
        node.addInputPin(new Pin("num1", Pin.PinType.NUMBER, "Number 1", true));
        node.addInputPin(new Pin("num2", Pin.PinType.NUMBER, "Number 2", true));
        node.addOutputPin(new Pin("exec_out", Pin.PinType.FLOW, "Exec", false));
        node.addOutputPin(new Pin("result", Pin.PinType.NUMBER, "Result", false));
        return node;
    }

    private Node createVariableNode(double x, double y) {
        Node node = new Node("Number Variable", Node.NodeType.VARIABLE, new Point2D(x, y));
        node.addOutputPin(new Pin("value", Pin.PinType.NUMBER, "Value", false));
        return node;
    }

    private Node createPrintNode(double x, double y) {
        Node node = new Node("Print Result", Node.NodeType.FUNCTION, new Point2D(x, y));
        node.addInputPin(new Pin("exec_in", Pin.PinType.FLOW, "Exec", true));
        node.addInputPin(new Pin("value", Pin.PinType.NUMBER, "Value", true));
        node.addOutputPin(new Pin("exec_out", Pin.PinType.FLOW, "Exec", false));
        return node;
    }

    private void addNodeToCanvas(Node node) {
        NodeView nodeView = new NodeView(node);
        nodeViews.add(nodeView);
        canvas.getChildren().add(nodeView);
        
        // Make node draggable
        makeDraggable(nodeView);
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
            dragDelta.x = nodeView.getLayoutX() - e.getSceneX();
            dragDelta.y = nodeView.getLayoutY() - e.getSceneY();
            e.consume();
        });

        nodeView.setOnMouseDragged(e -> {
            nodeView.setLayoutX(e.getSceneX() + dragDelta.x);
            nodeView.setLayoutY(e.getSceneY() + dragDelta.y);
            e.consume();
        });
    }

    private void setupStage(Stage primaryStage) {
        ScrollPane scrollPane = new ScrollPane(canvas);
        scrollPane.setPannable(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Scene scene = new Scene(scrollPane, 1200, 800);
        primaryStage.setTitle("Blueprint Node Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Helper class for drag functionality
    private static class Delta {
        double x, y;
    }

    public static void main(String[] args) {
        launch(args);
    }
}