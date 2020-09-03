package info.yantikov.graph.model;

import javafx.scene.control.TextArea;

/**
 * Created by Ilya on 05.04.2018. 21:57
 */
public class BellmanFord {
    private TextArea output;

    public BellmanFord(TextArea output) {
        this.output = output;
    }

    public void bellman_ford(Edge[] edge, int n, int s)
    {

        output.clear();
        int d[] = new int[n];
        int i, j;

        for (i=0; i<n; i++) d[i]=Integer.MAX_VALUE;
        d[s]=0;

        for (i=0; i<n-1; i++)
            for (j=0; j<edge.length; j++)
                if (d[edge[j].v] != Integer.MAX_VALUE && d[edge[j].v]+edge[j].w<d[edge[j].u])
                    d[edge[j].u]=d[edge[j].v]+edge[j].w;

        output.appendText("Стоимость пути из начальной\nвершины до остальных:\n");
        for (i=0; i<n; i++) if (d[i]==Integer.MAX_VALUE)
            output.appendText((s+1) + " -> " + (i+1) + " = " + "маршрут недоступен" + "\n");
        else
            output.appendText((s+1) + " -> " + (i+1) + " = " + d[i] + "\n");
    }

    public static class Edge implements Comparable<Edge>{
        int u, v, w;

        public Edge(int v, int u, int w) {
            this.u = u;
            this.v = v;
            this.w = w;
        }

        @Override
        public int compareTo(Edge o) {
            if(v < o.v) {
                return -1;
            } else if(v > o.v) {
                return 1;
            } else {
                if(u < o.u)
                    return -1;
                else
                    return 1;
            }
        }
    };
}
