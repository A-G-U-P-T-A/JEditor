package org.node.model;

import javafx.scene.paint.Color;

public class Connection {
    private String id;
    private Pin sourcePin;
    private Pin targetPin;
    private Color color;

    public Connection(Pin sourcePin, Pin targetPin) {
        this.sourcePin = sourcePin;
        this.targetPin = targetPin;
        this.color = sourcePin.getColor();
    }

    public boolean isValid() {
        return sourcePin != null && 
               targetPin != null && 
               sourcePin.getType() == targetPin.getType() &&
               !sourcePin.isInput() && 
               targetPin.isInput();
    }

    // Getters
    public Pin getSourcePin() { return sourcePin; }
    public Pin getTargetPin() { return targetPin; }
    public Color getColor() { return color; }
}
