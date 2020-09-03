package info.yantikov.graph.view;

import info.yantikov.graph.controllers.MainController;
import info.yantikov.graph.model.EdgeObject;
import info.yantikov.graph.model.GraphMatrix;
import info.yantikov.graph.model.GraphObject;
import info.yantikov.graph.model.VertexObject;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by Ilya on 01.04.2018. 14:19
 */

public class Graph extends Observable {
    public enum Mode {Default, AddVertex, RemoveVertex, AddEdge, RemoveEdge};
    private Mode mode = Mode.Default;
    private Pane pane;
    private List<Vertex> vertices;
    private List<Edge> edges;
    private double radius;
    private Vertex selectedVertex;
    private MainController controller;
    public boolean algorithmVertex;

    public Graph(Pane pane, MainController controller) {
        this.pane = pane;
        this.controller = controller;
        pane.setOnMouseClicked(new HandlePaneClick());
        pane.widthProperty().addListener((observable, oldValue, newValue) -> {
            for(Vertex vertex : vertices) {
                vertex.getGroup().setLayoutX(vertex.getGroup().getLayoutX()+1);
                vertex.getGroup().setLayoutX(vertex.getGroup().getLayoutX()-1);
            }
        });
        pane.heightProperty().addListener((observable, oldValue, newValue) -> {
            for(Vertex vertex : vertices) {
                vertex.getGroup().setLayoutY(vertex.getGroup().getLayoutY()+1);
                vertex.getGroup().setLayoutY(vertex.getGroup().getLayoutY()-1);
            }
        });

        radius = 14;
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public void newGraph(GraphObject graphObject) {
        clearAll();
        VertexObject [] voArray = graphObject.vertexObjects;
        for (VertexObject vo : voArray) {
            addVertex(vo.x, vo.y, vo.r, vo.index, vo.fontSize);
        }
        EdgeObject [] eoArray = graphObject.edgeObjects;
        for (EdgeObject eo : eoArray) {

            connect(findByIndex(eo.end), findByIndex(eo.start), eo.weight);
        }
    }

    private Vertex findByIndex(int index) {
        for(Vertex v : vertices) {
            if(v.getIndex() == index)
                return v;
        }
        throw new NullPointerException();
    }

    public void edgeClicked(Edge edge) {
        int r = edge.getStart().getIndex();
        int c = edge.getEnd().getIndex() + 1;

        controller.getMatrixTableView().edit(r, controller.getMatrixTableView().getColumns().get(c));
    }

    void vertexClicked(Vertex vertex) {
        if(selectedVertex == vertex) {
            selectedVertex = null;
            vertex.setDefaultStyle();
        } else {
            if(selectedVertex != null) {
                selectedVertex.setDefaultStyle();
                connect(selectedVertex, vertex, GraphMatrix.defaultValue);
            }
            selectedVertex = vertex;
            vertex.setSelectedStyle();
        }
    }

    public void connect(Vertex selectedVertex, Vertex vertex, int weight) {
        for(Edge e : edges) {
            if((e.getStart() == selectedVertex && e.getEnd() == vertex) || (e.getEnd() == selectedVertex && e.getStart() == vertex))
                return;
        }
        Edge edge = new Edge(this, selectedVertex, vertex, weight);
        edge.addToPane();
        edges.add(edge);

        setChanged();
        notifyObservers(edge);

    }

    public Color getCurrentVertexColor() {
        return controller.getVertexColor();
    }

    public void unselect() {
        if(selectedVertex != null) {
            selectedVertex.setDefaultStyle();
            selectedVertex = null;
        }
    }

    public void vertexSelected(Vertex vertex) {
        algorithmVertex = false;
        setChanged();
        notifyObservers(vertex);
    }

    public void setVerticesColor(Color color) {
        for(Vertex vertex : vertices) {
            vertex.setColor(color);
        }
    }

    void removeVertex(Vertex vertex)
    {
        List<Edge> willBeRemoved = new ArrayList<>();
        for(Edge e : edges) {
            if(e.getStart() == vertex || e.getEnd() == vertex)
                willBeRemoved.add(e);
        }
        for(Edge e : willBeRemoved)
            removeEdge(e);
        for(int i = vertices.indexOf(vertex) + 1; i < vertices.size(); i++) {
            Vertex v = vertices.get(i);
            v.setIndex(i-1);
        }
        vertex.removeFromPane();
        vertices.remove(vertex);

        setChanged();
        notifyObservers();
    }

    void removeEdge(Edge edge) {

        edge.removeFromPane();
        edges.remove(edge);

        setChanged();
        notifyObservers(edge);
    }

    public void removeEdge(Vertex start, Vertex end) {
        for(Edge edge : edges) {
            if((edge.getStart() == start && edge.getEnd() == end) || (edge.getStart() == end && edge.getEnd() == start)) {
                removeEdge(edge);
                return;
            }
        }
    }

    public void clearAll() {
        for(Vertex v : vertices) {
            v.removeFromPane();
        }
        vertices.clear();
        for(Edge e : edges) {
            e.removeFromPane();
        }
        edges.clear();

        setChanged();
        notifyObservers();
    }

    private void addVertex(double x, double y, double r, int index, double fontSize) {
        Vertex vertex = new Vertex(Graph.this, x, y, r, index, fontSize);
        vertex.addToPane();
        vertices.add(vertex);
        setChanged();
        notifyObservers();
    }

    class HandlePaneClick implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent event) {
            if(!event.getTarget().equals(pane))
                return;

            switch (mode) {
                case Default:
                    break;
                case AddVertex:
                    addVertex(event);
                case AddEdge:
                    break;
            }
        }

        private void addVertex(MouseEvent event) {
            Vertex vertex = new Vertex(Graph.this, event.getX(), event.getY(), radius, vertices.size(), 13);
            vertex.addToPane();
            vertices.add(vertex);
            setChanged();
            notifyObservers();
        }
    }

    Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    Pane getPane() {
        return pane;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public GraphObject getGraphObject() {
        GraphObject graphObject = new GraphObject();
        graphObject.vertexObjects = new VertexObject[vertices.size()];
        for (int i = 0; i < graphObject.vertexObjects.length; i++) {
            graphObject.vertexObjects[i] = vertices.get(i).getVerticesObject();
        }
        graphObject.edgeObjects = new EdgeObject[edges.size()];
        for (int i = 0; i < graphObject.edgeObjects.length; i++) {
            graphObject.edgeObjects[i] = edges.get(i).getEdgeObject();
        }
        return graphObject;
    }
}
