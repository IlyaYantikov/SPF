package info.yantikov.graph.model;

import info.yantikov.graph.view.Graph;
import javafx.scene.control.TextArea;

/**
 * Created by Ilya on 05.04.2018. 21:50
 */
public class Dijkstra {
    private TextArea output;

    public Dijkstra(TextArea output) {
        this.output = output;
    }

    public void dijkstra(int GR[][], int st)
    {
        output.clear();
        final int V = GR.length;
        int distance[] = new int[V], count, index = 0, i, u, m=st+1;
        boolean visited[] = new boolean[V];
        for (i=0; i<V; i++)
        {
            distance[i]=Integer.MAX_VALUE; visited[i]=false;
        }
        distance[st]=0;
        for (count=0; count<V-1; count++)
        {
            int min=Integer.MAX_VALUE;
            for (i=0; i<V; i++)
                if (!visited[i] && distance[i]<=min)
                {
                    min=distance[i]; index=i;
                }
            u=index;
            visited[u]=true;
            for (i=0; i<V; i++)
                if (!visited[i] && GR[u][i] != -1 && distance[u]!=Integer.MAX_VALUE &&
                        distance[u]+GR[u][i]<distance[i])
                    distance[i]=distance[u]+GR[u][i];
        }
        output.appendText("Стоимость пути из начальной\nвершины до остальных:\n");
        for (i=0; i<V; i++)
            if (distance[i]!=Integer.MAX_VALUE) {
                output.appendText(m + " -> " + (i+1) +  " = " + distance[i] + "\n");
            } else {
                output.appendText(m + " -> " + (i+1) + " маршрут недоступен\n");
            }

    }
}
