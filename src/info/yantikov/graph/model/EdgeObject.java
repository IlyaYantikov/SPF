package info.yantikov.graph.model;

import info.yantikov.graph.view.Vertex;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;

import java.io.Serializable;

/**
 * Created by Ilya on 10.12.2018. 10:45
 */
public class EdgeObject implements Serializable{
    public EdgeObject(int start, int end, int weight) {
        this.start = start;
        this.end = end;
        this.weight = weight;
    }

    public int start;
    public int end;
    public int weight;
}
