package info.yantikov.graph.model;

import java.io.Serializable;

/**
 * Created by Ilya on 10.12.2018. 10:44
 */
public class VertexObject implements Serializable{
    public VertexObject(double x, double y, double r, int index, double fontSize) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.index = index;
        this.fontSize = fontSize;
    }

    public double x;
    public double y;
    public double r;
    public int index;
    public double fontSize;
}
