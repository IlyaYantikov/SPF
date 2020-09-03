package info.yantikov.graph.model;

import javafx.scene.control.TextArea;

/**
 * Created by Ilya on 06.04.2018. 21:48
 */
public class FloydWarshall {

    TextArea output;

    public FloydWarshall(TextArea output) {
        this.output = output;
    }

    public void start(int D[][], int V)
    {
        int i, j;
        int k;
        for (i=0; i<V; i++) D[i][i]=0;

        for (k=0; k<V; k++)
            for (i=0; i<V; i++)
                for (j=0; j<V; j++)
                    if (D[i][k] != 0 && D[k][j] != 0 && i!=j)
                        if (D[i][k]+D[k][j]<D[i][j] || D[i][j]==0)
                            D[i][j]=D[i][k]+D[k][j];

        output.appendText("Матрица кратчайших путей:\n");
        for(i = 0; i < V; i++)
            output.appendText("\t" + "V" + (i+1));
        output.appendText("\n");
        for (i=0; i<V; i++)
        {
            output.appendText("V" + (i+1) + "\t");
            for (j=0; j<V; j++)
                if(i != j && D[i][j] == 0)
                    output.appendText("-" + "\t");
                else
                    output.appendText(D[i][j] + "\t");
            output.appendText("\n");
        }
    }
}
