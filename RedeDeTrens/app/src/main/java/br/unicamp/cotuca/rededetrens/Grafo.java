package br.unicamp.cotuca.rededetrens;

import java.util.Stack;

public class Grafo
{

    private final int NUM_VERTICES = 20;
    private Vertice[] vertices;
    private int[][] adjMatrix;
    int numVerts;

    /// DJIKSTRA
    DistOriginal[] percurso;
    int infinity = 1000000;
    int verticeAtual;   // global usada para indicar o vértice atualmente sendo visitado
    int doInicioAteAtual;   // global usada para ajustar menor caminho com Djikstra
    int nTree;

    public Grafo()
    {
        vertices = new Vertice[NUM_VERTICES];
        adjMatrix = new int[NUM_VERTICES][NUM_VERTICES];
        numVerts = 0;
        nTree = 0;

        for (int j = 0; j < NUM_VERTICES; j++)      // zera toda a matriz
            for (int k = 0; k < NUM_VERTICES; k++)
                adjMatrix[j][k] = infinity;         // distância tão grande que não existe

        percurso = new DistOriginal[NUM_VERTICES];
    }

    public void NovoVertice(String label)
    {
        vertices[numVerts] = new Vertice(label);
        numVerts++;
    }


    public void NovaAresta(int origem, int destino, int peso)
    {
        adjMatrix[origem][destino] = peso;
    }



    public int SemSucessores() 	// encontra e retorna a linha de um vértice sem sucessores
    {
        boolean temAresta;
        for (int linha = 0; linha < numVerts; linha++)
        {
            temAresta = false;
            for (int col = 0; col < numVerts; col++)
                if (adjMatrix[linha][col] != infinity)
            {
                temAresta = true;
                break;
            }
            if (!temAresta)
                return linha;
        }
        return -1;
    }

    public void removerVertice(int vert)
    {
        if (vert != numVerts - 1)
        {
            for (int j = vert; j < numVerts - 1; j++)
                vertices[j] = vertices[j + 1];


            for (int row = vert; row < numVerts; row++)
                moverLinhas(row, numVerts - 1);
            for (int col = vert; col < numVerts; col++)
                moverColunas(col, numVerts - 1);
        }
        numVerts--;

    }
    private void moverLinhas(int row, int length)
    {
        if (row != numVerts - 1)
            for (int col = 0; col < length; col++)
                adjMatrix[row][col] = adjMatrix[row + 1][col];  // desloca para excluir
    }
    private void moverColunas(int col, int length)
    {
        if (col != numVerts - 1)
            for (int row = 0; row < length; row++)
                adjMatrix[row][col] = adjMatrix[row][col + 1]; // desloca para excluir
    }


    private int ObterVerticeAdjacenteNaoVisitado(int v)
    {
        for (int j = 0; j <= numVerts - 1; j++)
            if ((adjMatrix[v][j] != infinity) && (!vertices[j].foiVisitado))
        return j;
        return -1;
    }


    public String[] Caminho(int inicioDoPercurso, int finalDoPercurso)
    {
        for (int j = 0; j < numVerts; j++)
            vertices[j].foiVisitado = false;

        vertices[inicioDoPercurso].foiVisitado = true;
        for (int j = 0; j < numVerts; j++)
        {
            // anotamos no vetor percurso a distância entre o inicioDoPercurso e cada vértice
            // se não há ligação direta, o valor da distância será infinity
            int tempDist = adjMatrix[inicioDoPercurso][j];
            percurso[j] = new DistOriginal(inicioDoPercurso, tempDist);
        }

        for (int nTree = 0; nTree < numVerts; nTree++)
        {
            // Procuramos a saída não visitada do vértice inicioDoPercurso com a menor distância
            int indiceDoMenor = ObterMenor();

            // e anotamos essa menor distância
            int distanciaMinima = percurso[indiceDoMenor].distancia;


            // o vértice com a menor distância passa a ser o vértice atual
            // para compararmos com a distância calculada em AjustarMenorCaminho()
            verticeAtual = indiceDoMenor;
            doInicioAteAtual = percurso[indiceDoMenor].distancia;

            // visitamos o vértice com a menor distância desde o inicioDoPercurso
            vertices[verticeAtual].foiVisitado = true;
            AjustarMenorCaminho();
        }

        return ExibirPercursos(inicioDoPercurso, finalDoPercurso);
    }

    public int ObterMenor()
    {
        int distanciaMinima = infinity;
        int indiceDaMinima = 0;
        for (int j = 0; j < numVerts; j++)
            if (!(vertices[j].foiVisitado) && (percurso[j].distancia < distanciaMinima))
            {
                distanciaMinima = percurso[j].distancia;
                indiceDaMinima = j;
            }
        return indiceDaMinima;
    }

    public void AjustarMenorCaminho()
    {
        for (int coluna = 0; coluna < numVerts; coluna++)
            if (!vertices[coluna].foiVisitado)       // para cada vértice ainda não visitado
            {
                // acessamos a distância desde o vértice atual (pode ser infinity)
                int atualAteMargem = adjMatrix[verticeAtual][coluna];

                // calculamos a distância desde inicioDoPercurso passando por vertice atual até
                // esta saída
                int doInicioAteMargem = doInicioAteAtual + atualAteMargem;

                // quando encontra uma distância menor, marca o vértice a partir do
                // qual chegamos no vértice de índice coluna, e a soma da distância
                // percorrida para nele chegar
                int distanciaDoCaminho = percurso[coluna].distancia;
                if (doInicioAteMargem < distanciaDoCaminho)
                {
                    percurso[coluna].verticePai = verticeAtual;
                    percurso[coluna].distancia = doInicioAteMargem;
                }
            }
    }


    public String[] ExibirPercursos(int inicioDoPercurso, int finalDoPercurso)
    {

        String[] oCaminho = new String[percurso.length];

        int onde = finalDoPercurso;
        Stack<String> pilha = new Stack<String>();

        int cont = 0;
        while (onde != inicioDoPercurso)
        {
            onde = percurso[onde].verticePai;
            pilha.push(vertices[onde].rotulo);
            cont++;
        }

        int contador = 0;
        while (pilha.size() != 0)
        {
            oCaminho[contador] = pilha.pop();

            contador++;
        }

        if ((cont == 1) && (percurso[finalDoPercurso].distancia == infinity))
            return null;
        else
            oCaminho[contador] = vertices[finalDoPercurso].rotulo;

        oCaminho[contador + 1] = percurso[finalDoPercurso].distancia + "";
        return oCaminho;
    }
}
