package dijkstra;

import java.util.Arrays;
import java.util.PriorityQueue;

public class Dijkstra {
    private final Graph graph;
    private int[] distances;
    private int[] predecessors;
    private boolean[] visited;
    private final int INFINITY = Integer.MAX_VALUE;

    public Dijkstra(Graph graph) {
        this.graph = graph;
        this.distances = new int[graph.getNumVertices()];
        this.predecessors = new int[graph.getNumVertices()];
        this.visited = new boolean[graph.getNumVertices()];
    }

    private static class Node implements Comparable<Node> {
        int vertex;
        int distance;

        Node(int vertex, int distance) {
            this.vertex = vertex;
            this.distance = distance;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.distance, other.distance);
        }
    }

    // ejecutar algoritmo dikjstra desde el nodo origen
    public void execute(int startVertex) {
        // Inicialización
        Arrays.fill(distances, INFINITY);
        Arrays.fill(predecessors, -1);
        Arrays.fill(visited, false);

        distances[startVertex] = 0;

        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(new Node(startVertex, 0));

        while (!priorityQueue.isEmpty()) {
            Node currentNode = priorityQueue.poll();
            int currentVertex = currentNode.vertex;

            // Si ya fue visitado, continuar
            if (visited[currentVertex]) {
                continue;
            }

            visited[currentVertex] = true;

            // Relajación de aristas adyacentes
            for (Graph.Edge edge : graph.getAdjacentEdges(currentVertex)) {
                int neighbor = edge.destination;
                int weight = edge.weight;

                if (distances[currentVertex] != INFINITY) {
                    int newDistance = distances[currentVertex] + weight;

                    if (newDistance < distances[neighbor]) {
                        distances[neighbor] = newDistance;
                        predecessors[neighbor] = currentVertex;
                        priorityQueue.add(new Node(neighbor, newDistance));
                    }
                }
            }
        }
    }

    public void printDistances() {
        System.out.println("\nDistancias mínimas desde el nodo origen:");
        System.out.println("Nodo\tDistancia");
        System.out.println("----\t---------");
        for (int i = 0; i < distances.length; i++) {
            if (distances[i] == INFINITY) {
                System.out.println(i + "\tINF");
            } else {
                System.out.println(i + "\t" + distances[i]);
            }
        }
    }

    public void printPathTo(int destination) {
        if (distances[destination] == INFINITY) {
            System.out.println("\nNo existe camino al nodo " + destination);
            return;
        }

        System.out.println("\nCamino mínimo al nodo " + destination + ":");

        int[] path = new int[graph.getNumVertices()];
        int pathLength = 0;
        int current = destination;

        while (current != -1) {
            path[pathLength++] = current;
            current = predecessors[current];
        }

        System.out.print("Ruta: ");
        for (int i = pathLength - 1; i >= 0; i--) {
            System.out.print(path[i]);
            if (i > 0) {
                System.out.print(" → ");
            }
        }

        System.out.println("\nDistancia total: " + distances[destination]);
    }

    public int[] getDistances() {
        return distances.clone();
    }

    public int[] getPredecessors() {
        return predecessors.clone();
    }

    public boolean isReachable(int vertex) {
        return distances[vertex] != INFINITY;
    }
}