package org.node.model;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class Pin {
    private String id;
    private PinType type;
    private String label;
    private Point2D position;
    private Node parentNode;
    private boolean isInput;
    private Color color;

    public enum PinType {
        FLOW,       // Execution flow (white)
        BOOLEAN,    // Boolean (red)
        NUMBER,     // Number (green)
        STRING,     // String (yellow)
        OBJECT,     // Object reference (blue)
        ARRAY       // Array (purple)
    }

    public Pin(String id, PinType type, String label, boolean isInput) {
        this.id = id;
        this.type = type;
        this.label = label;
        this.isInput = isInput;
        this.color = getPinColor(type);
    }

    private Color getPinColor(PinType type) {
        switch (type) {
            case FLOW: return Color.WHITE;
            case BOOLEAN: return Color.RED;
            case NUMBER: return Color.GREEN;
            case STRING: return Color.YELLOW;
            case OBJECT: return Color.BLUE;
            case ARRAY: return Color.PURPLE;
            default: return Color.GRAY;
        }
    }

    // Getters and setters
    public String getId() { return id; }
    public PinType getType() { return type; }
    public String getLabel() { return label; }
    public Point2D getPosition() { return position; }
    public void setPosition(Point2D position) { this.position = position; }
    public Node getParentNode() { return parentNode; }
    public void setParentNode(Node parentNode) { this.parentNode = parentNode; }
    public boolean isInput() { return isInput; }
    public Color getColor() { return color; }
}
