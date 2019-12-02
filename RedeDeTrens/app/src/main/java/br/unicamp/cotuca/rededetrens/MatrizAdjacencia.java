package br.unicamp.cotuca.rededetrens;

import android.content.res.AssetManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MatrizAdjacencia
{
    int DOTS_QTD;
    String[] dots;

    public MatrizAdjacencia(int qtdVertices, ArrayList<String> lista)
    {
        DOTS_QTD = qtdVertices;
        dots = new String[DOTS_QTD];
        for(int i = 0; i < DOTS_QTD; i++)
            dots[i] = lista.get(i);
    }

    private int[][] buildAdjacencyMatrix()
    {
        int[][] adjacencyMatrix = new int[DOTS_QTD][DOTS_QTD];			//Matriz NxN
        this.initializeAdjacencyMatrix(adjacencyMatrix);

        adjacencyMatrix[0][1] = 1;										//AB
        adjacencyMatrix[0][3] = 1;										//AD

        adjacencyMatrix[1][0] = 1;										//BA
        adjacencyMatrix[1][2] = 1;										//BC
        adjacencyMatrix[1][3] = 1;										//BD

        adjacencyMatrix[2][1] = 1;										//CB

        adjacencyMatrix[3][0] = 1;										//DA
        adjacencyMatrix[3][1] = 1;										//DB

        return adjacencyMatrix;
    }

    private void initializeAdjacencyMatrix(int[][] adjacencyMatrix){
        for(int i=0; i<adjacencyMatrix.length; i++){
            for(int j=0; j<adjacencyMatrix[i].length; j++){
                adjacencyMatrix[i][j] = 0;								//Inicialização da matriz
            }
        }
    }

    private void displayAdjacencyMatrix(int[][] adjacencyMatrix){
        for(int i=0; i<adjacencyMatrix.length; i++){					  //itero as linhas
            for(int j=0; j<adjacencyMatrix[i].length; j++){				//itero as colunas
                if(adjacencyMatrix[i][j] != 0){							          //quero imprimir somente as ligações
                    System.out.println(dots[i] + "->" + dots[j]);		  //Imprime as arestas
                }
            }
        }
    }

    /*
    public static void main(String[] args) {
        MatrizAdjacencia adjMatrix = new MatrizAdjacencia();
        int[][] adjacencyMatrix = adjMatrix.buildAdjacencyMatrix();
        adjMatrix.displayAdjacencyMatrix(adjacencyMatrix);
    }

     */

    public boolean isNumber(String n)
    {
        try{
            Integer.parseInt(n);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }


    public boolean jaTem(String n, List lista)
    {
        for(int i = 0; i < lista.size();i++)
            if(lista.get(i).equals(n))
                return true;
        return false;
    }


}
