package info.yantikov.graph.model;

import info.yantikov.graph.controllers.MainController;
import info.yantikov.graph.view.Edge;
import info.yantikov.graph.view.Graph;
import info.yantikov.graph.view.Vertex;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.TextAlignment;
import javafx.util.StringConverter;

import java.util.*;

/**
 * Created by Ilya on 02.04.2018. 12:51
 */
public class GraphMatrix implements Observer{
    public static int defaultValue = 1;
    private List<List<StringProperty>> sMatrix;
    private Graph graph;
    private TableView<List<StringProperty>> tableView;
    private Label status;
    private TimerTask statusTask;
    private Timer timer;
    private TextArea output;
    private MainController controller;

    public GraphMatrix(Graph graph, MainController controller) {
        this.graph = graph;
        this.tableView = controller.getMatrixTableView();
        this.status = controller.getLeftStatus();
        this.output = controller.getOutput();
        this.controller = controller;
        timer = new Timer();
        statusTask = new StatusTimerTask("");
        sMatrix = new ArrayList<>();
    }

    private void initMatrix() {
        sMatrix.clear();
        sMatrix.add(new ArrayList<>());
        sMatrix.get(0).add(new SimpleStringProperty(" "));
        List<Vertex> vertices = graph.getVertices();
        for(Vertex v : vertices) {
            sMatrix.get(0).add(v.nameProperty());
            List<StringProperty> line = new ArrayList<>();
            sMatrix.add(line);
            line.add(v.nameProperty());
            for(Vertex vertex : vertices) {
                line.add(new SimpleStringProperty("-"));
                if(sMatrix.indexOf(line) == line.size() - 1)
                    line.set(line.size() - 1, new SimpleStringProperty("0"));
            }
        }
        List<Edge> edges = graph.getEdges();
        for(Edge e : edges) {
            int i1 = vertices.indexOf(e.getStart()) + 1;
            int i2 = vertices.indexOf(e.getEnd()) + 1;
            StringProperty sp = e.weightProperty();
            sMatrix.get(i1).set(i2, sp);
            sMatrix.get(i2).set(i1, sp);
        }

    }

    private void out() {
        for(List<StringProperty> lines : sMatrix) {
            for(StringProperty s : lines) {
                System.out.print(s.getValue() + " ");
            }
            System.out.println();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof Vertex) {
            Vertex vertex = (Vertex) arg;
            int i = graph.getVertices().indexOf(vertex);
            if(controller.getDijkstraButton().isFocused())
                runDijkstra(i);
            else if(controller.getFordButton().isFocused())
                runFord(i);
            return;
        } else if(arg instanceof Edge)
            edgeUpdate((Edge) arg);
        totalUpdate();
        tableView.refresh();
    }

    private void edgeUpdate(Edge edge) {
        List<Vertex> vertices = graph.getVertices();
        int i1 = vertices.indexOf(edge.getStart()) + 1;
        int i2 = vertices.indexOf(edge.getEnd()) + 1;
        if(graph.getEdges().contains(edge)) {
            StringProperty sp = edge.weightProperty();
            sMatrix.get(i1).set(i2, sp);
            sMatrix.get(i2).set(i1, sp);
        } else {
            SimpleStringProperty ssp = new SimpleStringProperty("-");
            sMatrix.get(i1).set(i2, ssp);
            sMatrix.get(i2).set(i1, ssp);
        }
    }

    private void totalUpdate() {
        tableView.getColumns().clear();
        tableView.getItems().clear();
        initMatrix();
        int rows = sMatrix.size();
        int columns = sMatrix.size();
        for(int i = 0; i < columns; i++) {
            final int index = i;
            TableColumn<List<StringProperty>, String> column = new TableColumn<>(sMatrix.get(0).get(i).getValue());
            column.setStyle( "-fx-alignment: CENTER;");
            column.setSortable(false);
            column.setMinWidth(40);
            if(i == 0)
                column.setEditable(false);
            else
                column.setEditable(true);
            column.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().get(index).getValue()));
            column.setCellFactory(param -> new MyTextFieldCell<>(new StringConverter<String>() {
                @Override
                public String toString(String object) {
                    return object;
                }

                @Override
                public String fromString(String string) {
                    return string;
                }

            }));
            column.setOnEditCommit(event -> {
                int row = event.getTablePosition().getRow();
                int col = event.getTablePosition().getColumn();
                if(!event.getOldValue().equals("-") && event.getNewValue().equals("-")) {
                    graph.removeEdge(graph.getVertices().get(row), graph.getVertices().get(col-1));
                    return;
                }
                String s = sMatrix.get(row+1).get(col).getValue();
                int n = 0;
                try {
                    n = Integer.parseInt(event.getNewValue());
                    if(n < 0) {
                        tableView.refresh();
                        statusTask.cancel();
                        status.setText("Используйте положительные целые числа");
                        statusTask = new StatusTimerTask("");
                        timer.schedule(statusTask, 5000);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    tableView.refresh();
                    statusTask.cancel();
                    status.setText("Используйте положительные целые числа");
                    statusTask = new StatusTimerTask("");
                    timer.schedule(statusTask, 5000);
                    return;
                }

                if(s.equals("-")){
                    graph.connect(graph.getVertices().get(row), graph.getVertices().get(col-1), n);
                    /*tableView.refresh();
                    statusTask.cancel();
                    String v1name = graph.getVertices().get(row).nameProperty().getValue();
                    String v2name = graph.getVertices().get(col-1).nameProperty().getValue();
                    status.setText("Вершины " + v1name + " и " + v2name + " не соединеды");
                    statusTask = new StatusTimerTask("");
                    timer.schedule(statusTask, 5000);*/
                    return;
                }
                sMatrix.get(event.getTablePosition().getRow()+1).get(event.getTablePosition().getColumn()).setValue("" + n);
            });
            tableView.getColumns().add(column);
        }

        for(int i = 1; i < rows; i++) {
            tableView.getItems().add(sMatrix.get(i));
        }
    }

    public List<List<StringProperty>> getMatrix() {
        return sMatrix;
    }

    public void runFord(int selected) {
        BellmanFord bellmanFord = new BellmanFord(output);
        ArrayList<BellmanFord.Edge> list = new ArrayList<>();

        for(Edge edge : graph.getEdges()) {
            list.add(new BellmanFord.Edge(edge.getStart().getIndex(), edge.getEnd().getIndex(), Integer.parseInt(edge.getWeight())));
            list.add(new BellmanFord.Edge(edge.getEnd().getIndex(), edge.getStart().getIndex(), Integer.parseInt(edge.getWeight())));
        }
        Collections.sort(list);
        BellmanFord.Edge [] edges = new BellmanFord.Edge[list.size()];
        for(int i = 0; i < list.size(); i++) {
            edges[i] = list.get(i);
        }
        bellmanFord.bellman_ford(edges,graph.getVertices().size(), selected);
    }

    public void runDijkstra(int index) {
        if(sMatrix.size() < 2) {
            statusTask.cancel();
            status.setText("Добавьте вершины");
            statusTask = new StatusTimerTask("");
            timer.schedule(statusTask, 5000);
            return;
        }
        int matrix[][] = new int[sMatrix.size()-1][sMatrix.get(0).size()-1];
        for(int i = 0; i < matrix.length; i++) {
            matrix[i] = new int[sMatrix.get(i).size()];
            for(int j = 0; j < matrix.length; j++) {
                String s = sMatrix.get(i+1).get(j+1).getValue();
                if(s.equals("-")) {
                    matrix[i][j] = -1;
                } else {
                    matrix[i][j] = Integer.parseInt(s);
                }
            }
        }
        Dijkstra dijkstra = new Dijkstra(output);
        dijkstra.dijkstra(matrix, index);
    }

    class MyTextFieldCell<S, T> extends TextFieldTableCell<S, T> {
        MyTextFieldCell(StringConverter<T> converter) {
            super(converter);
        }

        @Override
        public void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            setTextAlignment(TextAlignment.CENTER);
            int row = getTableRow().getIndex();
            TableColumn c = getTableColumn();
            int column = tableView.getColumns().indexOf(c);
            if(row+1 == column) {
                setDisable(true);
            }
            if(column == 0 && row < sMatrix.size()-1) {
                getStylesheets().add(0, "css/modena.css");
                getStyleClass().add("column-header");
                setDisable(true);
            }
        }

        @Override
        public void commitEdit(T newValue) {
            super.commitEdit(newValue);
            tableView.refresh();
        }
    }

    class StatusTimerTask extends TimerTask {
        String defaultText;

        StatusTimerTask(String text) {
            defaultText = text;
        }

        @Override
        public void run() {
            Platform.runLater(() -> status.setText(defaultText));
        }
    }
}
