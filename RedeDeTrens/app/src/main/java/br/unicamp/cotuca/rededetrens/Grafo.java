package br.unicamp.cotuca.rededetrens;

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


    public String Caminho(int inicioDoPercurso, int finalDoPercurso, ListBox lista)
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
            AjustarMenorCaminho(lista);
        }

        return ExibirPercursos(inicioDoPercurso, finalDoPercurso, lista);
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

    public void AjustarMenorCaminho(ListBox lista)
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
                    ExibirTabela(lista);
                }
            }
        lista.Items.Add("==================Caminho ajustado==============");
    }

    public void ExibirTabela(ListBox lista)
    {
        String dist = "";
        lista.Items.Add("Vértice\tVisitado?\tPeso\tVindo de");
        for (int i = 0; i < numVerts; i++)
        {
            if (percurso[i].distancia == infinity)
                dist = "inf";
            else
                dist = Convert.ToString(percurso[i].distancia);

            lista.Items.Add(vertices[i].rotulo + "\t" + vertices[i].foiVisitado +
                    "\t\t" + dist + "\t" + vertices[percurso[i].verticePai].rotulo);
        }
        lista.Items.Add("-----------------------------------------------------");
    }

    public String ExibirPercursos(int inicioDoPercurso, int finalDoPercurso, ListBox lista) {
        String linha = "", resultado = "";
        for (int j = 0; j < numVerts; j++) {
            linha += vertices[j].rotulo + "=";
            if (percurso[j].distancia == infinity)
                linha += "inf";
            else
                linha += percurso[j].distancia;
            String pai = vertices[percurso[j].verticePai].rotulo;
            linha += "(" + pai + ") ";
        }
        lista.Items.Add(linha);
        lista.Items.Add("");
        lista.Items.Add("");
        lista.Items.Add("Caminho entre " + vertices[inicioDoPercurso].rotulo +
                " e " + vertices[finalDoPercurso].rotulo);
        lista.Items.Add("");

        int onde = finalDoPercurso;
        Stack<string> pilha = new Stack<string>();

        int cont = 0;
        while (onde != inicioDoPercurso) {
            onde = percurso[onde].verticePai;
            pilha.Push(vertices[onde].rotulo);
            cont++;
        }

        while (pilha.Count != 0) {
            resultado += pilha.Pop();
            if (pilha.Count != 0)
                resultado += " --> ";
        }

        if ((cont == 1) && (percurso[finalDoPercurso].distancia == infinity))
            resultado = "Não há caminho";
        else
            resultado += " --> " + vertices[finalDoPercurso].rotulo;
        return resultado;
    }
}
