package info.yantikov.graph.view;

import info.yantikov.graph.model.VertexObject;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

/**
 * Created by Ilya on 01.04.2018. 14:22
 */
public class Vertex {
    private Graph graph;
    private Circle circle;
    private Label label;
    private Group group;
    private StringProperty name;
    private int index;
    Vertex(Graph graph, double x, double y, double r, int i, double fontSize) {
        this.graph = graph;
        circle = new Circle(r);
        circle.setFill(graph.getCurrentVertexColor());
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(1);
        index = i;
        label = new Label("V" + (i+1));
        this.name = label.textProperty();
        label.setFont(Font.font(fontSize));
        label.widthProperty().addListener((observable, oldValue, newValue) -> {
            label.setTranslateX(-(newValue.doubleValue()/2));
        });
        label.heightProperty().addListener(((observable, oldValue, newValue) -> {
            label.setTranslateY(-(newValue.doubleValue()/2));
        }));
        group = new Group(circle, label);

        Pane pane = graph.getPane();
        if(x - circle.getRadius() - circle.getStrokeWidth() < 0)
            group.setLayoutX(circle.getRadius() + circle.getStrokeWidth());
        else if(x + circle.getRadius() + circle.getStrokeWidth() > pane.getWidth())
            group.setLayoutX(pane.getWidth() - circle.getRadius() - circle.getStrokeWidth());
        else
            group.setLayoutX(x);
        if(y - circle.getRadius() - circle.getStrokeWidth() < 0)
            group.setLayoutY(circle.getRadius() + circle.getStrokeWidth());
        else if(y + circle.getRadius() + circle.getStrokeWidth() > pane.getHeight())
            group.setLayoutY(pane.getHeight() - circle.getRadius() - circle.getStrokeWidth());
        else
            group.setLayoutY(y);
    }

    void addToPane() {
        Pane pane = graph.getPane();
        pane.getChildren().add(group);
        HandleDragging dragging = new HandleDragging();
        group.setOnMousePressed(dragging);
        group.setOnMouseReleased(dragging);
        group.setOnMouseDragged(dragging);
        group.setOnMouseEntered(dragging);

        HandleClicking clicking = new HandleClicking();
        group.setOnMouseClicked(clicking);
        group.addEventHandler(MouseEvent.MOUSE_PRESSED, clicking);
        group.addEventHandler(MouseEvent.MOUSE_RELEASED, clicking);

        group.layoutXProperty().addListener((observable, oldValue, newValue) -> {
            double x = newValue.doubleValue();
            if(x - circle.getRadius() - circle.getStrokeWidth() < 0)
                group.setLayoutX(circle.getRadius() + circle.getStrokeWidth());
            else if(x + circle.getRadius() + circle.getStrokeWidth() > pane.getWidth())
                group.setLayoutX(pane.getWidth() - circle.getRadius() - circle.getStrokeWidth());
        });
        group.layoutYProperty().addListener(((observable, oldValue, newValue) -> {
            double y = newValue.doubleValue();
            if(y - circle.getRadius() - circle.getStrokeWidth() < 0)
                group.setLayoutY(circle.getRadius() + circle.getStrokeWidth());
            else if(y + circle.getRadius() + circle.getStrokeWidth() > pane.getHeight())
                group.setLayoutY(pane.getHeight() - circle.getRadius() - circle.getStrokeWidth());
        }));
    }

    void removeFromPane() {
        Pane pane = graph.getPane();
        pane.getChildren().remove(group);
    }

    public void setColor(Color color) {
        circle.setFill(color);
    }

    public void setSelectedStyle() {
        circle.setStrokeWidth(2);
        circle.setStroke(Color.RED);
    }

    public void setDefaultStyle() {
        circle.setStrokeWidth(1);
        circle.setStroke(Color.BLACK);
    }

    public Circle getCircle() {
        return circle;
    }

    public Group getGroup() {
        return group;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setIndex(int index) {
        this.index = index;
        name.setValue("V" + (index + 1));
    }

    public VertexObject getVerticesObject() {
        return new VertexObject(group.getLayoutX(), group.getLayoutY(), circle.getRadius(), index, label.getFont().getSize());
    }

    public int getIndex() {
        return index;
    }



    class Delta { double x, y; }

    class HandleDragging implements EventHandler<MouseEvent> {
        final Delta dragDelta = new Delta();
        @Override
        public void handle(MouseEvent event) {
            if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                dragDelta.x = group.getLayoutX() - event.getSceneX();
                dragDelta.y = group.getLayoutY() - event.getSceneY();
            } else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                group.setCursor(Cursor.HAND);
            } else if(event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
                group.setLayoutX(event.getSceneX() + dragDelta.x);
                group.setLayoutY(event.getSceneY() + dragDelta.y);
                group.setCursor(Cursor.MOVE);
            } else if(event.getEventType().equals(MouseEvent.MOUSE_ENTERED)) {
                group.setCursor(Cursor.HAND);
            }
        }
    }

    class HandleClicking implements EventHandler<MouseEvent> {
        Delta pressed = new Delta();
        @Override
        public void handle(MouseEvent event) {

            if(event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                pressed.x = event.getSceneX();
                pressed.y = event.getSceneY();
            } else if(event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                if(pressed.x == event.getSceneX() && pressed.y == event.getSceneY()) {
                    if(graph.algorithmVertex) {
                        graph.vertexSelected(Vertex.this);
                    } else if (graph.getMode() == Graph.Mode.RemoveVertex) {
                        graph.removeVertex(Vertex.this);
                    } else if (graph.getMode() == Graph.Mode.AddEdge) {
                        graph.vertexClicked(Vertex.this);
                    }
                }
            }
        }
    }
}
