package dijkstra;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    // Códigos de colores ANSI
    private static final String RESET = "\u001B[0m";

    // Colores para títulos de menús
    private static final String COLOR_TITULO = "\u001B[38;2;165;77;143m"; // #a54d8f

    // Colores para opciones de menús
    private static final String COLOR_OPCION = "\u001B[38;2;120;124;236m"; // #787cec

    // Colores para resultados exitosos
    private static final String COLOR_EXITO_1 = "\u001B[38;2;117;156;253m"; // #759cfd
    private static final String COLOR_EXITO_2 = "\u001B[38;2;185;83;166m"; // #b953a6
    private static final String COLOR_EXITO_3 = "\u001B[38;2;8;150;193m";  // #0896c1
    private static final String COLOR_EXITO_4 = "\u001B[38;2;170;158;224m"; // #aa9ee0

    // Colores adicionales
    private static final String COLOR_INFO = "\u001B[38;2;72;201;176m";    // #48c9b0
    private static final String COLOR_ADVERTENCIA = "\u001B[38;2;255;193;7m"; // #ffc107
    private static final String COLOR_ERROR = "\u001B[38;2;220;53;69m";    // #dc3545
    private static final String COLOR_DESTACADO = "\u001B[38;2;40;167;69m"; // #28a745
    private static final String COLOR_SECUNDARIO = "\u001B[38;2;108;117;125m"; // #6c757d
    private static final String COLOR_TABLA = "\u001B[38;2;52;152;219m";    // #3498db

    private static int totalAristasArchivo = 0; // Para guardar el número de aristas del archivo
    private static Graph graph = null;
    private static File selectedFile = null;

    public static void main(String[] args) {
        System.out.println(COLOR_TITULO + "=== IMPLEMENTACIÓN DEL ALGORITMO DE DIJKSTRA ===" + RESET + "\n");

        boolean salir = false;

        while (!salir) {
            // Buscar archivos .txt en la carpeta resources
            List<File> txtFiles = findTxtFilesInResources();

            if (txtFiles.isEmpty()) {
                System.out.println(COLOR_ERROR + " No se encontraron archivos .txt en la carpeta resources/" + RESET);
                System.out.println(COLOR_SECUNDARIO + "Por favor, coloca los archivos de grafo en:" + RESET);
                System.out.println(COLOR_SECUNDARIO + "• src/resources/" + RESET);
                System.out.println(COLOR_SECUNDARIO + "• resources/" + RESET);
                return;
            }

            // Menú principal
            int opcionPrincipal = menuPrincipal();

            switch (opcionPrincipal) {
                case 1: // Cargar nuevo archivo
                    selectedFile = selectFileMenu(txtFiles);
                    if (selectedFile == null) break;

                    graph = loadGraphFromFile(selectedFile);
                    if (graph == null) break;

                    showGraphInfo(selectedFile, graph);
                    printGraphWithEdgeInfo(graph);

                    // Ir directamente al menú de Dijkstra
                    menuDijkstra();
                    break;

                case 2: // Ejecutar Dijkstra (si ya hay un grafo cargado)
                    if (graph == null) {
                        System.out.println(COLOR_ADVERTENCIA + "\n  Primero debes cargar un archivo de grafo." + RESET);
                        System.out.println(COLOR_SECUNDARIO + "Por favor, selecciona la opción 1.\n" + RESET);
                        break;
                    }
                    menuDijkstra();
                    break;

                case 3: // Ver información del grafo actual
                    if (graph == null) {
                        System.out.println(COLOR_ADVERTENCIA + "\n  No hay ningún grafo cargado." + RESET);
                        System.out.println(COLOR_SECUNDARIO + "Por favor, selecciona la opción 1 para cargar un archivo.\n" + RESET);
                        break;
                    }
                    showGraphInfo(selectedFile, graph);
                    printGraphWithEdgeInfo(graph);
                    break;

                case 4: // Salir
                    salir = true;
                    System.out.println(COLOR_EXITO_4 + "\n Gracias y Bendiciones Abundantes." + RESET);
                    break;

                default:
                    System.out.println(COLOR_ERROR + " Opción inválida. Intenta nuevamente." + RESET);
            }
        }
    }

    /**
     * Menú principal del programa
     */
    private static int menuPrincipal() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println(COLOR_TITULO + "\n" + "━".repeat(50) + RESET);
            System.out.println(COLOR_TITULO + "  MENÚ PRINCIPAL" + RESET);
            System.out.println(COLOR_TITULO + "━".repeat(50) + RESET);

            if (graph != null) {
                System.out.println(COLOR_INFO + " Grafo actual: " +
                        (selectedFile != null ? selectedFile.getName() : "No disponible") + RESET);
                System.out.println(COLOR_SECUNDARIO + "   Nodos: " + graph.getNumVertices() +
                        ", Aristas: " + totalAristasArchivo + RESET);
            } else {
                System.out.println(COLOR_SECUNDARIO + " Grafo actual: Ninguno cargado" + RESET);
            }

            System.out.println(COLOR_SECUNDARIO + "─".repeat(50) + RESET);
            System.out.println(COLOR_OPCION + "1.  Cargar nuevo archivo de grafo" + RESET);
            System.out.println(COLOR_OPCION + "2.  Ejecutar algoritmo de Dijkstra" + RESET);
            System.out.println(COLOR_OPCION + "3.   Ver información del grafo actual" + RESET);
            System.out.println(COLOR_OPCION + "4.  Salir del programa" + RESET);
            System.out.println(COLOR_SECUNDARIO + "─".repeat(50) + RESET);

            try {
                System.out.print(COLOR_EXITO_1 + "\nSelecciona una opción (1-4): " + RESET);
                int choice = scanner.nextInt();

                if (choice >= 1 && choice <= 4) {
                    return choice;
                } else {
                    System.out.println(COLOR_ERROR + " Opción inválida. Intenta nuevamente." + RESET);
                }
            } catch (Exception e) {
                System.out.println(COLOR_ERROR + " Entrada inválida. Ingresa un número." + RESET);
                scanner.nextLine();
            }
        }
    }

    /**
     * Menú para ejecutar Dijkstra
     */
    private static void menuDijkstra() {
        Scanner scanner = new Scanner(System.in);
        boolean volverMenuPrincipal = false;

        while (!volverMenuPrincipal) {
            System.out.println(COLOR_TITULO + "\n" + "━".repeat(50) + RESET);
            System.out.println(COLOR_TITULO + "  ALGORITMO DE DIJKSTRA" + RESET);
            System.out.println(COLOR_TITULO + "━".repeat(50) + RESET);

            System.out.println(COLOR_INFO + "Grafo: " +
                    (selectedFile != null ? selectedFile.getName() : "Desconocido") + RESET);
            System.out.println(COLOR_INFO + "Nodos disponibles: 0 al " + (graph.getNumVertices() - 1) + RESET);
            System.out.println(COLOR_SECUNDARIO + "─".repeat(50) + RESET);

            System.out.println(COLOR_OPCION + "1.  Ejecutar Dijkstra desde Nodo 0 hasta último nodo" + RESET);
            System.out.println(COLOR_OPCION + "2.  Elegir nodo origen y destino específicos" + RESET);
            System.out.println(COLOR_OPCION + "3.   Volver al menú principal" + RESET);
            System.out.println(COLOR_SECUNDARIO + "─".repeat(50) + RESET);

            try {
                System.out.print(COLOR_EXITO_1 + "\nSelecciona una opción (1-3): " + RESET);
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1: // Ejecutar Dijkstra completo desde nodo 0 hasta último nodo
                        executeDijkstraCompleto(graph, 0);
                        break;
                    case 2: // Elegir nodo origen y destino específicos
                        seleccionarOrigenDestino();
                        break;
                    case 3: // Volver al menú principal
                        volverMenuPrincipal = true;
                        System.out.println(COLOR_EXITO_3 + "\n  Volviendo al menú principal...\n" + RESET);
                        break;
                    default:
                        System.out.println(COLOR_ERROR + " Opción inválida. Intenta nuevamente." + RESET);
                }
            } catch (Exception e) {
                System.out.println(COLOR_ERROR + " Entrada inválida. Ingresa un número." + RESET);
                scanner.nextLine();
            }
        }
    }

    /**
     * Permite al usuario seleccionar nodo origen y destino
     */
    private static void seleccionarOrigenDestino() {
        Scanner scanner = new Scanner(System.in);
        int maxNode = graph.getNumVertices() - 1;

        System.out.println(COLOR_EXITO_2 + "\n SELECCIÓN DE ORIGEN Y DESTINO" + RESET);
        System.out.println(COLOR_SECUNDARIO + "─".repeat(40) + RESET);

        // Seleccionar nodo origen
        int origen = -1;
        while (origen == -1) {
            System.out.print(COLOR_EXITO_1 + "Ingresa el nodo ORIGEN (0-" + maxNode + "): " + RESET);
            try {
                origen = scanner.nextInt();
                if (origen < 0 || origen > maxNode) {
                    System.out.println(COLOR_ERROR + " Nodo fuera de rango. Intenta nuevamente." + RESET);
                    origen = -1;
                }
            } catch (Exception e) {
                System.out.println(COLOR_ERROR + " Entrada inválida. Ingresa un número." + RESET);
                scanner.nextLine();
            }
        }

        // Seleccionar nodo destino
        int destino = -1;
        while (destino == -1) {
            System.out.print(COLOR_EXITO_1 + "Ingresa el nodo DESTINO (0-" + maxNode + ", diferente de " + origen + "): " + RESET);
            try {
                destino = scanner.nextInt();
                if (destino < 0 || destino > maxNode) {
                    System.out.println(COLOR_ERROR + " Nodo fuera de rango. Intenta nuevamente." + RESET);
                    destino = -1;
                } else if (destino == origen) {
                    System.out.println(COLOR_ADVERTENCIA + "  El destino no puede ser el mismo que el origen. Intenta nuevamente." + RESET);
                    destino = -1;
                }
            } catch (Exception e) {
                System.out.println(COLOR_ERROR + " Entrada inválida. Ingresa un número." + RESET);
                scanner.nextLine();
            }
        }

        // Ejecutar Dijkstra para el origen y destino específicos
        executeDijkstraEspecifico(graph, origen, destino);
    }

    /**
     * Ejecuta Dijkstra desde un nodo específico hacia un destino específico
     */
    private static void executeDijkstraEspecifico(Graph graph, int origen, int destino) {
        System.out.println(COLOR_EXITO_4 + "\n" + "━".repeat(60) + RESET);
        System.out.println(COLOR_EXITO_4 + "  DIJKSTRA ESPECÍFICO" + RESET);
        System.out.println(COLOR_EXITO_4 + "Origen: Nodo " + origen + " → Destino: Nodo " + destino + RESET);
        System.out.println(COLOR_EXITO_4 + "━".repeat(60) + RESET + "\n");

        // Mostrar información de los nodos seleccionados
        System.out.println(COLOR_EXITO_2 + " INFORMACIÓN DE LOS NODOS SELECCIONADOS:" + RESET);
        System.out.println(COLOR_SECUNDARIO + "─".repeat(50) + RESET);

        // Información del nodo origen
        List<Graph.Edge> aristasOrigen = graph.getAdjacentEdges(origen);
        System.out.println(COLOR_INFO + "Nodo ORIGEN " + origen + ":" + RESET);
        System.out.println(COLOR_INFO + "  • Conexiones directas: " + aristasOrigen.size() +
                " arista(s) saliente(s)" + RESET);
        if (!aristasOrigen.isEmpty()) {
            for (Graph.Edge edge : aristasOrigen) {
                System.out.println(COLOR_OPCION + "  └─ Nodo " + edge.destination +
                        COLOR_EXITO_2 + " (peso: " + edge.weight + ")" + RESET);
            }
        }

        System.out.println();

        // Información del nodo destino
        List<Graph.Edge> aristasDestino = graph.getAdjacentEdges(destino);
        System.out.println(COLOR_INFO + "Nodo DESTINO " + destino + ":" + RESET);
        System.out.println(COLOR_INFO + "  • Conexiones directas: " + aristasDestino.size() +
                " arista(s) saliente(s)" + RESET);
        if (!aristasDestino.isEmpty()) {
            for (Graph.Edge edge : aristasDestino) {
                System.out.println(COLOR_OPCION + "  └─ Nodo " + edge.destination +
                        COLOR_EXITO_2 + " (peso: " + edge.weight + ")" + RESET);
            }
        }

        System.out.println(COLOR_SECUNDARIO + "─".repeat(50) + RESET + "\n");

        // Crear e ejecutar Dijkstra desde el origen
        System.out.println(COLOR_EXITO_3 + "⚡ Calculando camino más corto...\n" + RESET);
        Dijkstra dijkstra = new Dijkstra(graph);
        dijkstra.execute(origen);

        // Verificar si el destino es alcanzable
        if (!dijkstra.isReachable(destino)) {
            System.out.println(COLOR_ERROR + " NO HAY CAMINO DISPONIBLE" + RESET);
            System.out.println(COLOR_SECUNDARIO + "─".repeat(40) + RESET);
            System.out.println(COLOR_ADVERTENCIA + "El nodo destino " + destino +
                    " no es alcanzable desde el nodo origen " + origen + RESET);
            System.out.println(COLOR_SECUNDARIO + "─".repeat(40) + RESET);
        } else {
            // Mostrar distancias desde el origen
            System.out.println(COLOR_EXITO_1 + " DISTANCIAS DESDE EL NODO " + origen + ":" + RESET);
            System.out.println(COLOR_SECUNDARIO + "═".repeat(40) + RESET);
            dijkstra.printDistances();
            System.out.println(COLOR_SECUNDARIO + "═".repeat(40) + RESET + "\n");

            // Mostrar el camino específico al destino
            System.out.println(COLOR_EXITO_2 + " CAMINO MÁS CORTO ESPECÍFICO:" + RESET);
            System.out.println(COLOR_SECUNDARIO + "─".repeat(50) + RESET);
            System.out.println(COLOR_TABLA + "Desde: Nodo " + origen + RESET);
            System.out.println(COLOR_TABLA + "Hacia: Nodo " + destino + RESET);
            System.out.println(COLOR_SECUNDARIO + "─".repeat(50) + RESET);

            dijkstra.printPathTo(destino);

            System.out.println(COLOR_SECUNDARIO + "─".repeat(50) + RESET + "\n");

            // Mostrar información adicional del camino
            int distancia = dijkstra.getDistances()[destino];
            System.out.println(COLOR_EXITO_3 + " INFORMACIÓN ADICIONAL:" + RESET);
            System.out.println(COLOR_SECUNDARIO + "─".repeat(40) + RESET);
            System.out.println(COLOR_INFO + "• Distancia total: " + RESET +
                    COLOR_DESTACADO + distancia + RESET + COLOR_INFO + " unidades" + RESET);

            // Calcular número de nodos intermedios
            int[] pred = dijkstra.getPredecessors();
            int nodosIntermedios = 0;
            int current = destino;
            while (current != origen && current != -1) {
                nodosIntermedios++;
                current = pred[current];
            }
            if (nodosIntermedios > 0) {
                nodosIntermedios--; // Restar el nodo destino
            }

            System.out.println(COLOR_INFO + "• Nodos intermedios: " + RESET +
                    COLOR_DESTACADO + nodosIntermedios + RESET);
            System.out.println(COLOR_INFO + "• Total nodos en ruta: " + RESET +
                    COLOR_DESTACADO + (nodosIntermedios + 2) + RESET +
                    COLOR_INFO + " (origen + destino + intermedios)" + RESET);
            System.out.println(COLOR_SECUNDARIO + "─".repeat(40) + RESET);
        }

        // Preguntar si quiere continuar
        System.out.print(COLOR_EXITO_2 + "\n¿Deseas buscar otro camino específico? (s/n): " + RESET);
        Scanner scanner = new Scanner(System.in);
        String respuesta = scanner.next().toLowerCase();

        if (!respuesta.equals("s") && !respuesta.equals("si")) {
            System.out.println(COLOR_EXITO_3 + "\n️  Regresando al menú de Dijkstra...\n" + RESET);
        }
    }

    /**
     * Ejecuta Dijkstra completo desde nodo 0 hasta el último nodo
     */
    private static void executeDijkstraCompleto(Graph graph, int startNode) {
        int ultimoNodo = graph.getNumVertices() - 1;

        System.out.println(COLOR_EXITO_4 + "\n" + "━".repeat(70) + RESET);
        System.out.println(COLOR_EXITO_4 + "  EJECUCIÓN COMPLETA DE DIJKSTRA" + RESET);
        System.out.println(COLOR_EXITO_4 + "Desde: Nodo " + startNode + " → Hasta: Nodo " + ultimoNodo + RESET);
        System.out.println(COLOR_EXITO_4 + "━".repeat(70) + RESET + "\n");

        // Crear e ejecutar Dijkstra
        System.out.println(COLOR_EXITO_3 + "⚡ Calculando todos los caminos más cortos...\n" + RESET);
        Dijkstra dijkstra = new Dijkstra(graph);
        dijkstra.execute(startNode);

        // Mostrar todas las distancias
        System.out.println(COLOR_EXITO_1 + " DISTANCIAS MÍNIMAS DESDE EL NODO " + startNode + ":" + RESET);
        System.out.println(COLOR_SECUNDARIO + "═".repeat(40) + RESET);
        dijkstra.printDistances();
        System.out.println(COLOR_SECUNDARIO + "═".repeat(40) + RESET + "\n");

        // Mostrar caminos a TODOS los nodos
        System.out.println(COLOR_EXITO_2 + " CAMINOS MÁS CORTOS A TODOS LOS NODOS:" + RESET);
        System.out.println(COLOR_SECUNDARIO + "─".repeat(50) + RESET);

        for (int i = 0; i < graph.getNumVertices(); i++) {
            if (i != startNode) {
                System.out.println(COLOR_TABLA + "Hacia el Nodo " + i + ":" + RESET);
                dijkstra.printPathTo(i);
                System.out.println();
            }
        }

        System.out.println(COLOR_SECUNDARIO + "─".repeat(50) + RESET + "\n");

        // Mostrar tabla resumen de todos los caminos
        System.out.println(COLOR_EXITO_4 + " RESUMEN COMPLETO DE CAMINOS:" + RESET);
        System.out.println(COLOR_SECUNDARIO + "=".repeat(70) + RESET);

        System.out.printf(COLOR_TABLA + "%-6s " + RESET + COLOR_SECUNDARIO + "| " + RESET +
                        COLOR_EXITO_1 + "%-10s " + RESET + COLOR_SECUNDARIO + "| " + RESET +
                        COLOR_EXITO_2 + "%-8s " + RESET + COLOR_SECUNDARIO + "| " + RESET +
                        COLOR_OPCION + "%-30s\n" + RESET,
                "Nodo", "Distancia", "Estado", "Camino más corto");

        System.out.println(COLOR_SECUNDARIO + "-".repeat(70) + RESET);

        for (int i = 0; i < graph.getNumVertices(); i++) {
            if (i != startNode) {
                boolean alcanzable = dijkstra.isReachable(i);
                String estado = (alcanzable ? COLOR_DESTACADO + "✓ Alcanzable" : COLOR_ERROR + "✗ Inalcanzable") + RESET;
                String distancia = alcanzable ?
                        COLOR_EXITO_1 + String.valueOf(dijkstra.getDistances()[i]) + RESET :
                        COLOR_ERROR + "INF" + RESET;

                // Reconstruir camino para mostrar
                String camino = "";
                if (alcanzable) {
                    int[] pred = dijkstra.getPredecessors();
                    StringBuilder pathBuilder = new StringBuilder();
                    int current = i;

                    while (current != -1) {
                        pathBuilder.insert(0, COLOR_TABLA + current + RESET);
                        current = pred[current];
                        if (current != -1) {
                            pathBuilder.insert(0, COLOR_SECUNDARIO + " → " + RESET);
                        }
                    }
                    camino = pathBuilder.toString();
                } else {
                    camino = COLOR_SECUNDARIO + "Sin ruta disponible" + RESET;
                }

                System.out.printf(COLOR_TABLA + "%-6d " + RESET + COLOR_SECUNDARIO + "| " + RESET +
                                "%-10s " + COLOR_SECUNDARIO + "| " + RESET +
                                "%-11s " + COLOR_SECUNDARIO + "| " + RESET +
                                "%-30s\n",
                        i, distancia, estado, camino);
            }
        }

        System.out.println(COLOR_SECUNDARIO + "=".repeat(70) + RESET);
        System.out.println(COLOR_EXITO_3 + " Leyenda:" + RESET);
        System.out.println(COLOR_DESTACADO + "  ✓ Alcanzable" + RESET + COLOR_SECUNDARIO +
                " = Existe un camino desde el nodo origen" + RESET);
        System.out.println(COLOR_ERROR + "  ✗ Inalcanzable" + RESET + COLOR_SECUNDARIO +
                " = No hay camino desde el nodo origen" + RESET);
        System.out.println(COLOR_ERROR + "  INF" + RESET + COLOR_SECUNDARIO +
                " = Distancia infinita (no alcanzable)\n" + RESET);

        // Estadísticas finales
        System.out.println(COLOR_EXITO_4 + " ESTADÍSTICAS FINALES:" + RESET);
        System.out.println(COLOR_SECUNDARIO + "─".repeat(40) + RESET);
        int alcanzables = 0;
        int inalcanzables = 0;

        for (int i = 0; i < graph.getNumVertices(); i++) {
            if (i != startNode) {
                if (dijkstra.isReachable(i)) {
                    alcanzables++;
                } else {
                    inalcanzables++;
                }
            }
        }

        System.out.println(COLOR_INFO + "• Nodos totales: " + RESET +
                COLOR_TABLA + graph.getNumVertices() + RESET);
        System.out.println(COLOR_INFO + "• Nodo origen: " + RESET +
                COLOR_TABLA + startNode + RESET);
        System.out.println(COLOR_INFO + "• Nodos alcanzables: " + RESET +
                COLOR_DESTACADO + alcanzables + RESET);
        System.out.println(COLOR_INFO + "• Nodos inalcanzables: " + RESET +
                (inalcanzables > 0 ? COLOR_ERROR : COLOR_SECUNDARIO) + inalcanzables + RESET);

        if (alcanzables > 0) {
            // Encontrar nodo más cercano y más lejano
            int[] distancias = dijkstra.getDistances();
            int minDist = Integer.MAX_VALUE;
            int maxDist = 0;
            int nodoCercano = -1;
            int nodoLejano = -1;

            for (int i = 0; i < graph.getNumVertices(); i++) {
                if (i != startNode && dijkstra.isReachable(i)) {
                    if (distancias[i] < minDist) {
                        minDist = distancias[i];
                        nodoCercano = i;
                    }
                    if (distancias[i] > maxDist) {
                        maxDist = distancias[i];
                        nodoLejano = i;
                    }
                }
            }

            System.out.println(COLOR_INFO + "• Nodo más cercano: " + RESET +
                    COLOR_TABLA + nodoCercano + RESET + COLOR_INFO + " (distancia: " + RESET +
                    COLOR_EXITO_1 + minDist + COLOR_INFO + ")" + RESET);
            System.out.println(COLOR_INFO + "• Nodo más lejano: " + RESET +
                    COLOR_TABLA + nodoLejano + RESET + COLOR_INFO + " (distancia: " + RESET +
                    COLOR_EXITO_1 + maxDist + COLOR_INFO + ")" + RESET);

            // Información del camino al último nodo
            if (dijkstra.isReachable(ultimoNodo)) {
                System.out.println(COLOR_INFO + "• Camino al último nodo (" + ultimoNodo + "): " + RESET +
                        COLOR_EXITO_1 + distancias[ultimoNodo] + COLOR_INFO + " unidades" + RESET);
            }
        }
        System.out.println(COLOR_SECUNDARIO + "─".repeat(40) + RESET);

        // Preguntar si quiere continuar
        System.out.print(COLOR_EXITO_2 + "\n¿Deseas realizar otra operación? (s/n): " + RESET);
        Scanner scanner = new Scanner(System.in);
        String respuesta = scanner.next().toLowerCase();

        if (!respuesta.equals("s") && !respuesta.equals("si")) {
            System.out.println(COLOR_EXITO_3 + "\n  Regresando al menú de Dijkstra...\n" + RESET);
        }
    }

    /**
     * Busca archivos .txt en las carpetas de recursos
     */
    private static List<File> findTxtFilesInResources() {
        List<File> txtFiles = new ArrayList<>();

        // Directorios posibles donde buscar
        String[] possiblePaths = {
                "src/resources",
                "resources",
                "./resources"
        };

        for (String path : possiblePaths) {
            File resourcesDir = new File(path);
            if (resourcesDir.exists() && resourcesDir.isDirectory()) {
                File[] files = resourcesDir.listFiles((dir, name) ->
                        name.toLowerCase().endsWith(".txt"));

                if (files != null) {
                    for (File file : files) {
                        if (!txtFiles.contains(file)) {
                            txtFiles.add(file);
                        }
                    }
                }
            }
        }

        return txtFiles;
    }

    /**
     * Menú para seleccionar archivo de entrada
     */
    private static File selectFileMenu(List<File> txtFiles) {
        Scanner scanner = new Scanner(System.in);

        System.out.println(COLOR_EXITO_2 + "\n ARCHIVOS DISPONIBLES EN resources/:" + RESET);
        System.out.println(COLOR_SECUNDARIO + "─".repeat(50) + RESET);

        for (int i = 0; i < txtFiles.size(); i++) {
            File file = txtFiles.get(i);
            System.out.println(COLOR_OPCION + (i + 1) + ". " + file.getName() + RESET);
        }
        System.out.println(COLOR_OPCION + "0.   Cancelar y volver" + RESET);
        System.out.println(COLOR_SECUNDARIO + "─".repeat(50) + RESET);

        while (true) {
            try {
                System.out.print(COLOR_EXITO_1 + "\nSelecciona un archivo (1-" + txtFiles.size() + "): " + RESET);
                int choice = scanner.nextInt();

                if (choice == 0) {
                    System.out.println(COLOR_EXITO_3 + "\n️  Cancelando..." + RESET);
                    return null;
                }

                if (choice >= 1 && choice <= txtFiles.size()) {
                    File selected = txtFiles.get(choice - 1);
                    System.out.println(COLOR_EXITO_4 + "\n Seleccionado: " + selected.getName() + RESET);
                    return selected;
                } else {
                    System.out.println(COLOR_ERROR + " Opción inválida. Intenta nuevamente." + RESET);
                }
            } catch (Exception e) {
                System.out.println(COLOR_ERROR + " Entrada inválida. Ingresa un número." + RESET);
                scanner.nextLine();
            }
        }
    }

    /**
     * Carga el grafo desde un archivo
     */
    private static Graph loadGraphFromFile(File file) {
        try {
            Scanner scanner = new Scanner(file);

            System.out.println(COLOR_EXITO_3 + "\n Leyendo grafo desde: " + file.getPath() + RESET);

            int n = scanner.nextInt(); // número de nodos
            totalAristasArchivo = scanner.nextInt(); // número de aristas

            System.out.println(COLOR_EXITO_4 + "✓ Archivo leído correctamente" + RESET);
            System.out.println(COLOR_INFO + "  Nodos en archivo: " + n + RESET);
            System.out.println(COLOR_INFO + "  Aristas en archivo: " + totalAristasArchivo + RESET);

            // Crear el grafo
            Graph graph = new Graph(n);

            // Leer todas las aristas
            int aristasLeidas = 0;
            for (int i = 0; i < totalAristasArchivo; i++) {
                int u = scanner.nextInt();
                int v = scanner.nextInt();
                int w = scanner.nextInt();
                graph.addEdge(u, v, w);
                aristasLeidas++;
            }

            scanner.close();

            if (aristasLeidas != totalAristasArchivo) {
                System.out.println(COLOR_ADVERTENCIA + "  Nota: Se leyeron " + aristasLeidas + " de " +
                        totalAristasArchivo + " aristas esperadas" + RESET);
            }

            System.out.println(COLOR_EXITO_1 + "\n Grafo cargado exitosamente\n" + RESET);
            return graph;

        } catch (FileNotFoundException e) {
            System.out.println(COLOR_ERROR + " Error: Archivo no encontrado." + RESET);
            System.out.println(COLOR_SECUNDARIO + "Ruta: " + file.getAbsolutePath() + RESET);
            return null;
        } catch (Exception e) {
            System.out.println(COLOR_ERROR + " Error al leer el archivo: " + e.getMessage() + RESET);
            return null;
        }
    }

    /**
     * Muestra la información del grafo cargado
     */
    private static void showGraphInfo(File file, Graph graph) {
        System.out.println(COLOR_EXITO_4 + "\n" + "━".repeat(50) + RESET);
        System.out.println(COLOR_EXITO_4 + "GRAFO CARGADO EXITOSAMENTE" + RESET);
        System.out.println(COLOR_EXITO_4 + "━".repeat(50) + RESET);
        System.out.println(COLOR_EXITO_2 + " INFORMACIÓN DEL ARCHIVO:" + RESET);
        System.out.println(COLOR_INFO + "  └─ Archivo: " + file.getName() + RESET);
        System.out.println(COLOR_INFO + "  └─ Nodos: " + graph.getNumVertices() + RESET);
        System.out.println(COLOR_INFO + "  └─ Aristas: " + totalAristasArchivo + RESET);
        System.out.println(COLOR_EXITO_4 + "━".repeat(50) + RESET);
    }

    /**
     * Muestra la representación del grafo con información de aristas por nodo
     */
    private static void printGraphWithEdgeInfo(Graph graph) {
        System.out.println(COLOR_EXITO_3 + "\n REPRESENTACIÓN DEL GRAFO:" + RESET);
        System.out.println(COLOR_SECUNDARIO + "═".repeat(60) + RESET);

        int totalAristasSalientes = 0;

        for (int i = 0; i < graph.getNumVertices(); i++) {
            List<Graph.Edge> aristas = graph.getAdjacentEdges(i);
            int numAristas = aristas.size();
            totalAristasSalientes += numAristas;

            System.out.print(COLOR_TABLA + "Nodo " + i + RESET +
                    COLOR_EXITO_1 + " → " + RESET +
                    COLOR_DESTACADO + numAristas + RESET +
                    COLOR_EXITO_1 + " arista(s) saliente(s): " + RESET);

            if (numAristas == 0) {
                System.out.println(COLOR_SECUNDARIO + "Ninguna" + RESET);
            } else {
                for (int j = 0; j < aristas.size(); j++) {
                    Graph.Edge edge = aristas.get(j);
                    System.out.print(COLOR_OPCION + "→ [" + edge.destination + "]" + RESET +
                            COLOR_EXITO_2 + " (peso: " + edge.weight + ")" + RESET);
                    if (j < aristas.size() - 1) {
                        System.out.print(COLOR_SECUNDARIO + " | " + RESET);
                    }
                }
                System.out.println();
            }
        }

        System.out.println(COLOR_SECUNDARIO + "═".repeat(60) + RESET);
        System.out.println(COLOR_EXITO_2 + " RESUMEN DE CONEXIONES:" + RESET);
        System.out.println(COLOR_INFO + "  • Total aristas salientes en grafo: " +
                totalAristasSalientes + RESET);

        if (totalAristasSalientes != totalAristasArchivo) {
            System.out.println(COLOR_ADVERTENCIA + "    Nota: " + totalAristasSalientes +
                    " aristas válidas de " + totalAristasArchivo + " en archivo" + RESET);
        }
        System.out.println(COLOR_SECUNDARIO + "═".repeat(60) + RESET + "\n");
    }

    /**
     * Permite al usuario seleccionar un nodo origen personalizado
     */
    private static int selectCustomStartNode(int maxNode) {
        Scanner scanner = new Scanner(System.in);

        System.out.println(COLOR_EXITO_1 + "\n NODOS DISPONIBLES: del 0 al " + maxNode + RESET);
        System.out.print(COLOR_EXITO_2 + "Ingresa el número del nodo origen: " + RESET);

        while (true) {
            try {
                int startNode = scanner.nextInt();

                if (startNode >= 0 && startNode <= maxNode) {
                    System.out.println(COLOR_EXITO_4 + "\n Configuración: Dijkstra se ejecutará desde el Nodo " +
                            startNode + RESET);
                    return startNode;
                } else {
                    System.out.print(COLOR_ERROR + " Nodo fuera de rango. Ingresa un número entre 0 y " +
                            maxNode + ": " + RESET);
                }
            } catch (Exception e) {
                System.out.print(COLOR_ERROR + " Entrada inválida. Ingresa un número entre 0 y " +
                        maxNode + ": " + RESET);
                scanner.nextLine();
            }
        }
    }

    /**
     * Ejecuta el algoritmo de Dijkstra y muestra los resultados
     * (Método original para compatibilidad)
     */
    private static void executeDijkstra(Graph graph, int startNode) {
        // Por compatibilidad, llama al método completo
        executeDijkstraCompleto(graph, startNode);
    }
}