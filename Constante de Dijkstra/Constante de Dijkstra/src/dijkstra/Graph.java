package dijkstra;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private final int numVertices;
    private final List<List<Edge>> adjacencyList;

    // Clase interna para representar una arista
    public static class Edge {
        public int destination;
        public int weight;

        public Edge(int destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }
    }

    public Graph(int numVertices) {
        this.numVertices = numVertices;
        this.adjacencyList = new ArrayList<>();

        for (int i = 0; i < numVertices; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    public void addEdge(int source, int destination, int weight) {
        if (source < 0 || source >= numVertices ||
                destination < 0 || destination >= numVertices) {
            throw new IllegalArgumentException("Índice de vértice inválido");
        }
        adjacencyList.get(source).add(new Edge(destination, weight));
    }

    public List<Edge> getAdjacentEdges(int vertex) {
        return adjacencyList.get(vertex);
    }

    public int getNumVertices() {
        return numVertices;
    }

    public void printGraph() {
        System.out.println("Representación del grafo (lista de adyacencia):");
        for (int i = 0; i < numVertices; i++) {
            System.out.print("Vértice " + i + ": ");
            for (Edge edge : adjacencyList.get(i)) {
                System.out.print("→ (" + edge.destination + ", peso: " + edge.weight + ") ");
            }
            System.out.println();
        }
    }
}