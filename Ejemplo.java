import java.util.*;

public class Ejemplo {

    // Una arista guarda el vecino (destino) y el peso
    private static class Arista {
        String destino;
        int peso;

        Arista(String destino, int peso) {
            this.destino = destino;
            this.peso = peso;
        }
    }

    // Nodo para la cola de prioridad de Dijkstra
    private static class Nodo implements Comparable<Nodo> {
        String vertice;
        int distancia;

        Nodo(String vertice, int distancia) {
            this.vertice = vertice;
            this.distancia = distancia;
        }

        @Override
        public int compareTo(Nodo otro) {
            return Integer.compare(this.distancia, otro.distancia);
        }
    }

    // Lista de adyacencia: vértice -> lista de aristas
    private final Map<String, List<Arista>> adyacencia = new LinkedHashMap<>();
    private final boolean dirigido;

    public Ejemplo(boolean dirigido) {
        this.dirigido = dirigido;
    }

    public void agregarVertice(String v) {
        adyacencia.putIfAbsent(v, new ArrayList<>());
    }

    public boolean existeVertice(String v) {
        return adyacencia.containsKey(v);
    }

    public boolean estaVacio() {
        return adyacencia.isEmpty();
    }

    public void agregarArista(String origen, String destino, int peso) {
        agregarVertice(origen);
        agregarVertice(destino);
        adyacencia.get(origen).add(new Arista(destino, peso));
        if (!dirigido) { // si no es dirigido, la arista va en ambos sentidos
            adyacencia.get(destino).add(new Arista(origen, peso));
        }
    }

    public void mostrar() {
        for (Map.Entry<String, List<Arista>> entrada : adyacencia.entrySet()) {
            StringBuilder sb = new StringBuilder(entrada.getKey() + " -> ");
            for (Arista a : entrada.getValue()) {
                sb.append(a.destino).append("(").append(a.peso).append(") ");
            }
            System.out.println(sb);
        }
    }

    // Dijkstra: distancia mínima desde 'inicio' a todos los vértices (pesos no negativos)
    public Map<String, Integer> dijkstra(String inicio, Map<String, String> previo) {
        Map<String, Integer> dist = new HashMap<>();
        for (String v : adyacencia.keySet()) {
            dist.put(v, Integer.MAX_VALUE);
            previo.put(v, null);
        }
        dist.put(inicio, 0);

        PriorityQueue<Nodo> cola = new PriorityQueue<>();
        cola.add(new Nodo(inicio, 0));

        while (!cola.isEmpty()) {
            Nodo actual = cola.poll();

            // Si ya hay un camino mejor, ignoramos esta entrada
            if (actual.distancia > dist.get(actual.vertice)) {
                continue;
            }

            for (Arista a : adyacencia.get(actual.vertice)) {
                int nueva = actual.distancia + a.peso;
                if (nueva < dist.get(a.destino)) { // "relajación" de la arista
                    dist.put(a.destino, nueva);
                    previo.put(a.destino, actual.vertice);
                    cola.add(new Nodo(a.destino, nueva));
                }
            }
        }
        return dist;
    }

    // Reconstruye e imprime el camino más corto entre dos vértices
    public void imprimirCamino(String inicio, String fin) {
        Map<String, String> previo = new HashMap<>();
        Map<String, Integer> dist = dijkstra(inicio, previo);

        if (dist.get(fin) == Integer.MAX_VALUE) {
            System.out.println("No hay camino de " + inicio + " a " + fin);
            return;
        }

        LinkedList<String> camino = new LinkedList<>();
        for (String v = fin; v != null; v = previo.get(v)) {
            camino.addFirst(v);
        }
        System.out.println(String.join(" -> ", camino) + " (costo total: " + dist.get(fin) + ")");
    }

    // Pide un número entero y vuelve a preguntar si el usuario escribe otra cosa
    private static int leerEntero(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Debes escribir un numero entero.");
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Ejemplo g = new Ejemplo(false); // no dirigido
        int opcion;

        do {
            System.out.println("\n===== RED SOCIAL (GRAFO PONDERADO) =====");
            System.out.println("1. Agregar una persona");
            System.out.println("2. Conectar dos personas (con un peso)");
            System.out.println("3. Mostrar todas las conexiones");
            System.out.println("4. Buscar el camino mas corto entre dos personas");
            System.out.println("5. Cargar el ejemplo (Ana, Luis, Carla, Pedro)");
            System.out.println("0. Salir");
            opcion = leerEntero(sc, "Elige una opcion: ");

            switch (opcion) {
                case 1:
                    System.out.print("Nombre de la persona: ");
                    String nombre = sc.nextLine().trim();
                    if (nombre.isEmpty()) {
                        System.out.println("El nombre no puede estar vacio.");
                    } else if (g.existeVertice(nombre)) {
                        System.out.println(nombre + " ya existe.");
                    } else {
                        g.agregarVertice(nombre);
                        System.out.println(nombre + " fue agregada.");
                    }
                    break;

                case 2:
                    System.out.print("Primera persona: ");
                    String p1 = sc.nextLine().trim();
                    System.out.print("Segunda persona: ");
                    String p2 = sc.nextLine().trim();
                    if (p1.isEmpty() || p2.isEmpty()) {
                        System.out.println("Los nombres no pueden estar vacios.");
                    } else if (p1.equals(p2)) {
                        System.out.println("Una persona no se puede conectar consigo misma.");
                    } else {
                        int peso = leerEntero(sc, "Peso (menor = mas cercanos, mayor = mas lejanos): ");
                        if (peso <= 0) {
                            System.out.println("El peso debe ser mayor que 0.");
                        } else {
                            g.agregarArista(p1, p2, peso);
                            System.out.println("Conexion agregada: " + p1 + " - " + p2 + " (" + peso + ")");
                        }
                    }
                    break;

                case 3:
                    if (g.estaVacio()) {
                        System.out.println("Todavia no hay personas en el grafo.");
                    } else {
                        System.out.println("\n=== Lista de adyacencia ===");
                        g.mostrar();
                    }
                    break;

                case 4:
                    System.out.print("Persona de inicio: ");
                    String inicio = sc.nextLine().trim();
                    System.out.print("Persona de destino: ");
                    String fin = sc.nextLine().trim();
                    if (!g.existeVertice(inicio) || !g.existeVertice(fin)) {
                        System.out.println("Alguna de las dos personas no existe en el grafo.");
                    } else {
                        System.out.println("\n=== Camino mas corto ===");
                        g.imprimirCamino(inicio, fin);
                    }
                    break;

                case 5:
                    g.agregarArista("Ana", "Luis", 1);
                    g.agregarArista("Luis", "Carla", 1);
                    g.agregarArista("Carla", "Pedro", 2);
                    g.agregarArista("Ana", "Carla", 4);
                    g.agregarArista("Luis", "Pedro", 5);
                    System.out.println("Ejemplo cargado.");
                    break;

                case 0:
                    System.out.println("¡Hasta luego!");
                    break;

                default:
                    System.out.println("Opcion no valida.");
            }
        } while (opcion != 0);

        sc.close();
    }
}
