package org.node.view;

import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import org.node.model.Pin;

public class PinView extends Region {
    private Pin pin;
    private Circle circle;
    private static final double PIN_RADIUS = 5;

    public PinView(Pin pin) {
        this.pin = pin;
        setupPinView();
    }

    private void setupPinView() {
        circle = new Circle(PIN_RADIUS);
        circle.setFill(pin.getColor());
        circle.setStroke(javafx.scene.paint.Color.WHITE);
        circle.setStrokeWidth(1);
        getChildren().add(circle);

        // Make pin interactive
        circle.setOnMouseEntered(e -> circle.setRadius(PIN_RADIUS * 1.2));
        circle.setOnMouseExited(e -> circle.setRadius(PIN_RADIUS));
    }

    public Pin getPin() {
        return pin;
    }

    public Circle getCircle() {
        return circle;
    }
}
