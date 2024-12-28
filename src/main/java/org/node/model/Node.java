package org.node.model;

import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Node {
    private String id;
    private String title;
    private Point2D position;
    private List<Pin> inputPins;
    private List<Pin> outputPins;
    private NodeType type;

    public enum NodeType {
        FUNCTION,
        VARIABLE,
        EVENT,
        CUSTOM_EVENT,
        BRANCH,
        LOOP
    }

    public Node(String title, NodeType type, Point2D position) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.type = type;
        this.position = position;
        this.inputPins = new ArrayList<>();
        this.outputPins = new ArrayList<>();
    }

    public void addInputPin(Pin pin) {
        pin.setParentNode(this);
        inputPins.add(pin);
    }

    public void addOutputPin(Pin pin) {
        pin.setParentNode(this);
        outputPins.add(pin);
    }

    // Getters and setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public Point2D getPosition() { return position; }
    public void setPosition(Point2D position) { this.position = position; }
    public List<Pin> getInputPins() { return inputPins; }
    public List<Pin> getOutputPins() { return outputPins; }
    public NodeType getType() { return type; }
}
