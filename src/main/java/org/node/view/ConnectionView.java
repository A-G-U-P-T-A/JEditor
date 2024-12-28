package org.node.view;

import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.StrokeLineCap;
import org.node.model.Connection;
import javafx.geometry.Point2D;

public class ConnectionView extends CubicCurve {
    private Connection connection;
    private static final double CURVE_OFFSET = 100;

    public ConnectionView(Connection connection, PinView sourcePinView, PinView targetPinView) {
        this.connection = connection;
        setStroke(connection.getColor());
        setStrokeWidth(2);
        setFill(null);
        setStrokeLineCap(StrokeLineCap.ROUND);

        updateConnection(sourcePinView, targetPinView);
    }

    public void updateConnection(PinView sourcePinView, PinView targetPinView) {
        Point2D sourcePoint = getCenter(sourcePinView);
        Point2D targetPoint = getCenter(targetPinView);

        setStartX(sourcePoint.getX());
        setStartY(sourcePoint.getY());
        setEndX(targetPoint.getX());
        setEndY(targetPoint.getY());

        // Control points for smooth curve
        setControlX1(sourcePoint.getX() + CURVE_OFFSET);
        setControlY1(sourcePoint.getY());
        setControlX2(targetPoint.getX() - CURVE_OFFSET);
        setControlY2(targetPoint.getY());
    }

    private Point2D getCenter(PinView pinView) {
        return new Point2D(
            pinView.localToScene(pinView.getCircle().getCenterX(), 0).getX(),
            pinView.localToScene(0, pinView.getCircle().getCenterY()).getY()
        );
    }

    public Connection getConnection() {
        return connection;
    }
}
