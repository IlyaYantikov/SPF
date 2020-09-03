package info.yantikov.graph.view;

import info.yantikov.graph.model.EdgeObject;
import info.yantikov.graph.model.GraphMatrix;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

/**
 * Created by Ilya on 01.04.2018. 14:22
 */
public class Edge {
    private Graph graph;
    private Vertex start;
    private Vertex end;
    private Line line;
    private Label label;
    private StringProperty weight;

    public Edge(Graph graph, Vertex start, Vertex end, int weight) {
        this.graph = graph;
        this.start = start;
        this.end = end;

        line = new Line();
        line.setStrokeWidth(2);
        line.setStroke(Color.BLACK);

        label = new Label("" + weight);
        this.weight = label.textProperty();
        label.setFont(Font.font(14));
        label.setTextFill(Color.GREEN);
    }

    void addToPane() {
        Group c1 = start.getGroup();
        Group c2 = end.getGroup();

        line.startXProperty().bind(c1.layoutXProperty());
        line.startYProperty().bind(c1.layoutYProperty());
        line.endXProperty().bind(c2.layoutXProperty());
        line.endYProperty().bind(c2.layoutYProperty());

        label.setLayoutX((c1.getLayoutX() + c2.getLayoutX())/2);
        label.setLayoutY((c1.getLayoutY() + c2.getLayoutY())/2);
        label.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        label.widthProperty().addListener((observable, oldValue, newValue) -> {
            label.setTranslateX(-(newValue.doubleValue()/2));
        });
        label.heightProperty().addListener((observable, oldValue, newValue) -> {
            label.setTranslateY(-(newValue.doubleValue()/2));
        });

        line.startXProperty().addListener((observable, oldValue, newValue) -> {
            double s = newValue.doubleValue();
            double e = line.getEndX();
            double x = (s+e)/2;
            label.setLayoutX(x);
        });

        line.endXProperty().addListener((observable, oldValue, newValue) -> {
            double e = newValue.doubleValue();
            double s = line.getStartX();
            double x = (s+e)/2;
            label.setLayoutX(x);
        });

        line.startYProperty().addListener((observable, oldValue, newValue) -> {
            double s = newValue.doubleValue();
            double e = line.getEndY();
            double y = (s+e)/2;
            label.setLayoutY(y);
        });

        line.endYProperty().addListener((observable, oldValue, newValue) -> {
            double e = newValue.doubleValue();
            double s = line.getStartY();
            double y = (s+e)/2;
            label.setLayoutY(y);
        });

        HandleClicking clicking = new HandleClicking();
        line.setOnMouseEntered(clicking);
        line.setOnMousePressed(clicking);
        line.setOnMouseReleased(clicking);
        label.setOnMouseEntered(clicking);
        label.setOnMousePressed(clicking);
        label.setOnMouseReleased(clicking);


        Pane pane = graph.getPane();
        pane.getChildren().add(0, label);
        pane.getChildren().add(0, line);
    }

    void removeFromPane() {
        Pane pane = graph.getPane();
        pane.getChildren().remove(line);
        pane.getChildren().remove(label);
    }

    public Vertex getStart() {
        return start;
    }

    public Vertex getEnd() {
        return end;
    }

    public String getWeight() {
        return weight.get();
    }

    public StringProperty weightProperty() {
        return weight;
    }

    public EdgeObject getEdgeObject() {
        return new EdgeObject(start.getIndex(), end.getIndex(), Integer.parseInt(weight.getValue()));
    }

    class Delta { double x, y; }

    class HandleClicking implements EventHandler<MouseEvent> {
        Delta pressed = new Delta();
        @Override
        public void handle(MouseEvent event) {
            if(event.getEventType().equals(MouseEvent.MOUSE_ENTERED)){
                line.setCursor(Cursor.HAND);
                label.setCursor(Cursor.HAND);
            } else if(event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                pressed.x = event.getSceneX();
                pressed.y = event.getSceneY();
            } else if(event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                if(pressed.x == event.getSceneX() && pressed.y == event.getSceneY()) {
                    if (graph.getMode() == Graph.Mode.RemoveEdge) {
                        graph.removeEdge(Edge.this);
                    } else if(graph.getMode() == Graph.Mode.AddEdge) {
                        graph.edgeClicked(Edge.this);
                    }
                }
            }
        }
    }
}
