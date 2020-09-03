package info.yantikov.graph.controllers;

import info.yantikov.graph.Main;
import info.yantikov.graph.model.FloydWarshall;
import info.yantikov.graph.model.GraphMatrix;
import info.yantikov.graph.model.GraphObject;
import info.yantikov.graph.view.Edge;
import info.yantikov.graph.view.Graph;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

public class MainController {

    private Graph graph;

    private GraphMatrix graphMatrix;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private VBox root;

    @FXML
    private Menu actions;

    @FXML
    private Menu handling;

    @FXML
    private SplitPane mainPane;

    @FXML
    private ToggleButton vertexToggleButton;

    @FXML
    private ToggleButton deleteVertexToggleButton;

    @FXML
    private ToggleGroup controls;

    @FXML
    private ToggleButton edgeToggleButton;

    @FXML
    private ToggleButton deleteEdgeToggleButton;

    @FXML
    private Button clearAllButton;

    @FXML
    private Button dijkstraButton;

    @FXML
    private Button fordButton;

    @FXML
    private Button floydButton;

    @FXML
    private Pane graphPane;

    @FXML
    private Label leftStatus;

    @FXML
    private Label rightStatus;

    @FXML
    private TextArea output;

    @FXML
    private TableView<List<StringProperty>> matrixTableView;

    @FXML
    private AnchorPane graphAnchorPane;

    @FXML
    private AnchorPane outInfo;

    private Color themeColor;

    private Color vertexColor;

    private VBox colorRoot;

    @FXML
    void aboutProgram(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Программа для нахождения кратчайших расстояний в графе");
        alert.setTitle(((MenuItem)event.getSource()).getText());
        alert.show();
    }

    @FXML
    void palette(ActionEvent event) {
        if (colorRoot != null) {
            return;
        }
        try {
            colorRoot = FXMLLoader.load(Main.class.getResource("fxml/color.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Палитра");
            stage.setScene(new Scene(colorRoot));
            stage.show();
            stage.setOnCloseRequest(event13 -> colorRoot = null);
            ColorPicker colorPicker1 = (ColorPicker) colorRoot.getChildrenUnmodifiable().get(1);
            colorPicker1.setOnAction(event1 -> setThemeColor(colorPicker1.getValue()));
            List<BackgroundFill> fills = mainPane.getBackground().getFills();
            Color color = (Color) fills.get(fills.size() - 1).getFill();
            colorPicker1.setValue(color);
            ColorPicker colorPicker2 = (ColorPicker) colorRoot.getChildrenUnmodifiable().get(3);
            colorPicker2.setOnAction(event12 -> setVerticesColor(colorPicker2.getValue()));
            colorPicker2.setValue(vertexColor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void printout(ActionEvent event) {
        Stage primaryStage = (Stage) root.getScene().getWindow();
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if(printerJob.showPrintDialog(primaryStage.getOwner())) {
            printerJob.printPage(graphAnchorPane);
            printerJob.printPage(outInfo);
            printerJob.endJob();
        }
    }

    @FXML
    void defaultColors(ActionEvent event) {
        Color themeColor = Color.web("f4f4f4");
        Color vertexColor = Color.DODGERBLUE;
        setThemeColor(themeColor);
        setVerticesColor(vertexColor);
        if (colorRoot != null) {
            ColorPicker colorPicker = (ColorPicker) colorRoot.getChildrenUnmodifiable().get(1);
            colorPicker.setValue(themeColor);
            colorPicker = (ColorPicker) colorRoot.getChildrenUnmodifiable().get(3);
            colorPicker.setValue(vertexColor);
        }
    }

    @FXML
    void showInstruction(ActionEvent event) {
        try {
            Parent instruction = FXMLLoader.load(Main.class.getResource("fxml/instruction.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Инструкция");
            stage.setScene(new Scene(instruction));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть граф");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Документ Graph(*.graph)", "*.graph");
        fileChooser.getExtensionFilters().add(extFilter);
        Stage primaryStage = (Stage) root.getScene().getWindow();
        File file = fileChooser.showOpenDialog(primaryStage);
        if(file != null) {
            try {
                FileInputStream fileIn =
                        new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                GraphObject graphObject = (GraphObject) in.readObject();
                in.close();
                fileIn.close();
                controls.selectToggle(null);
                output.clear();
                graph.newGraph(graphObject);
                System.out.printf("Loaded data is saved from " + file.toPath());
            } catch (IOException | ClassNotFoundException i) {
                i.printStackTrace();
            }
        }
    }

    @FXML
    void saveFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить граф");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Документ Graph(*.graph)", "*.graph");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("newfile.graph");
        Stage primaryStage = (Stage) root.getScene().getWindow();
        File file = fileChooser.showSaveDialog(primaryStage);
        if(file != null) {
            try {
                FileOutputStream fileOut =
                        new FileOutputStream(file);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(graph.getGraphObject());
                out.close();
                fileOut.close();
                System.out.printf("Serialized data is saved in " + file.toPath());
            } catch (IOException i) {
                i.printStackTrace();
            }
        }
    }

    @FXML
    void theory(ActionEvent event) {
        String filename = "Задача о кратчайшем пути.docx";
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить файл теории");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Документ Word(*.docx)", "*.docx");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName(filename);
        Stage primaryStage = (Stage) root.getScene().getWindow();
        File file = fileChooser.showSaveDialog(primaryStage);
        File fin = new File("files/" + filename);
        if (file != null) {
            try {
                Files.copy(Paths.get(fin.getAbsolutePath()), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void vertexMenuButton(ActionEvent event) {
        vertexToggleButton.fire();
    }

    @FXML
    void removeVertexMenuButton(ActionEvent event) {
        deleteEdgeToggleButton.fire();
    }

    @FXML
    void edgeMenuButton(ActionEvent event) {
        edgeToggleButton.fire();
    }

    @FXML
    void removeEdgeMenuButton(ActionEvent event) {
        deleteEdgeToggleButton.fire();
    }

    @FXML
    void clearAllMenuButton(ActionEvent event) {
        clearAllButton.fire();
    }

    @FXML
    void runDijkstraMenuButton(ActionEvent event) {
        dijkstraButton.fire();
    }

    @FXML
    void runFordMenuButton(ActionEvent event) {
        fordButton.fire();
    }

    @FXML
    void runFloydMenuButton(ActionEvent event) {
        floydButton.fire();
    }


    @FXML
    void quit(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void handleControls(ActionEvent event) {
        Node button = (Node) event.getSource();
        switch (button.getId()) {
            case "clearAllButton":
                if(graph.getVertices().size() == 0)
                    break;
                ButtonType yes = new ButtonType("Да", ButtonBar.ButtonData.YES);
                ButtonType no = new ButtonType("Нет", ButtonBar.ButtonData.NO);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", yes, no);
                alert.setHeaderText("Вы уверены?");
                alert.setTitle("Очистить все");
                alert.showAndWait();

                if (alert.getResult() == yes) {
                    controls.selectToggle(null);
                    graph.clearAll();
                    output.clear();
                }
                break;
        }
    }

    @FXML
    void runDijkstra(ActionEvent event) {
        if(graph.getVertices().size() != 0){
            output.clear();
            controls.selectToggle(null);
            graph.algorithmVertex = true;
            output.appendText("Выбирите начальную вершину...");
        }

    }

    @FXML
    void runFloyd(ActionEvent event) {
        if(graph.getVertices().size() == 0)
            return;
        output.clear();
        controls.selectToggle(null);
        int d[][] = new int[graph.getVertices().size()][graph.getVertices().size()];
        for(int i = 0; i < graph.getVertices().size(); i++) {
            d[i] = new int[graph.getVertices().size()];
            for(int j = 0; j < graph.getVertices().size(); j++) {
                d[i][j] = 0;
            }
        }
        for (Edge edge : graph.getEdges()) {
            d[edge.getStart().getIndex()][edge.getEnd().getIndex()] = Integer.parseInt(edge.getWeight());
            d[edge.getEnd().getIndex()][edge.getStart().getIndex()] = Integer.parseInt(edge.getWeight());
        }

        FloydWarshall floydWarshall = new FloydWarshall(output);
        floydWarshall.start(d, graph.getVertices().size());
    }

    @FXML
    void runFord(ActionEvent event) {
        if(graph.getVertices().size() != 0) {
            output.clear();
            controls.selectToggle(null);
            graph.algorithmVertex = true;
            output.appendText("Выбирите начальную вершину...");
        }
    }

    @FXML
    void initialize() {
        matrixTableView.setPlaceholder(new Label("Матрица пуста"));
        matrixTableView.focusedProperty().addListener((observable, oldValue, newValue) -> {
            root.requestFocus();
        });
        graph = new Graph(graphPane, this);
        leftStatus.setTextFill(Color.RED);
        leftStatus.setText("");
        rightStatus.setText("");
        output.setEditable(false);
        vertexColor = Color.DODGERBLUE;

        setPropertiesListeners();

        graphMatrix = new GraphMatrix(graph, this);
        graph.addObserver(graphMatrix);

        initTableView();
    }

    private void initTableView() {

       matrixTableView.setEditable(true);
        matrixTableView.getSelectionModel().setCellSelectionEnabled(true);
    }

    private void setPropertiesListeners() {
        vertexToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                graph.setMode(Graph.Mode.AddVertex);
            } else {
                graph.setMode(Graph.Mode.Default);
            }
        });
        edgeToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                graph.setMode(Graph.Mode.AddEdge);
            } else {
                graph.unselect();
                graph.setMode(Graph.Mode.Default);
            }
        });
        deleteVertexToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                graph.setMode(Graph.Mode.RemoveVertex);
            } else {
                graph.setMode(Graph.Mode.Default);
            }
        });
        deleteEdgeToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                graph.setMode(Graph.Mode.RemoveEdge);
            } else {
                graph.setMode(Graph.Mode.Default);
            }
        });

        dijkstraButton.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                graph.algorithmVertex = false;
                if(output.getText().equals("Выбирите начальную вершину..."))
                    output.clear();
            }
        });
        fordButton.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                graph.algorithmVertex = false;
                if(output.getText().equals("Выбирите начальную вершину..."))
                    output.clear();
            }
        });
    }

    public Label getLeftStatus() {
        return leftStatus;
    }

    public TextArea getOutput() {
        return output;
    }

    public TableView<List<StringProperty>> getMatrixTableView() {
        return matrixTableView;
    }

    public Button getDijkstraButton() {
        return dijkstraButton;
    }

    public Button getFordButton() {
        return fordButton;
    }

    public Color getThemeColor() {
        return themeColor;
    }

    public Color getVertexColor() {
        return vertexColor;
    }

    public void setVerticesColor(Color color) {
        this.vertexColor = color;
        graph.setVerticesColor(color);
    }

    public void setThemeColor(Color themeColor) {
        this.themeColor = themeColor;
        mainPane.setBackground(new Background(new BackgroundFill(themeColor, null, null)));
    }
}
