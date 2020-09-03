package info.yantikov.graph.model;

import java.io.Serializable;

/**
 * Created by Ilya on 10.12.2018. 10:46
 */
public class GraphObject implements Serializable{
    public VertexObject vertexObjects[] = new VertexObject[2];
    public EdgeObject edgeObjects[] = new EdgeObject[2];
}
