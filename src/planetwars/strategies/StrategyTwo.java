package planetwars.strategies;

import planetwars.publicapi.*;
import java.util.*;


public class StrategyTwo implements IStrategy{
    private Map<Integer, IPlanet> planetMap = new HashMap<>();
    private int numOfPlanets, turn=0, numOfEdges=0;
    List<Vertex> vertexes;
    List<Edge> edges;
    Graph graph;

    @Override
    public void takeTurn(List<IPlanet> planets, IPlanetOperations planetOperations, Queue<IEvent> eventsToExecute) {
        if (turn == 0) {
            makeGraph(planets);
            turn++;
        }

        numOfPlanets = 0;
        for (IPlanet planet : planets) {
            int id = planet.getId();
            planetMap.put(id, planet);
            numOfPlanets++;
        }

        for (IPlanet planet : planetMap.values()) {
            if (planet instanceof IVisiblePlanet) {
                IVisiblePlanet visiblePlanet = (IVisiblePlanet) planet;
                if (visiblePlanet.getOwner() == Owner.SELF) {
                    IVisiblePlanet myPlanet = visiblePlanet;
                    int pop = (int)myPlanet.getPopulation();

                    Set<IEdge> edges = visiblePlanet.getEdges();
                    Iterator<IEdge> iterator = edges.iterator();

                    while (iterator.hasNext()) {
                        IEdge nextEdge = iterator.next();
                        int nextPlanetID = nextEdge.getDestinationPlanetId();
                        IVisiblePlanet nextPlanet = (IVisiblePlanet) findPlanet(nextPlanetID);
                        if (nextPlanet.getOwner() == Owner.NEUTRAL && pop > 1) {
                            eventsToExecute.add(planetOperations.transferPeople(visiblePlanet, nextPlanet, 1));
                            pop--;
                        }
                        if (nextPlanet.getOwner() == Owner.OPPONENT && pop >= nextPlanet.getPopulation() * 0.85) {
                            eventsToExecute.add(planetOperations.transferPeople(visiblePlanet, nextPlanet, (long) Math.ceil(pop*0.3)));
                            pop -= Math.ceil(pop*0.3);
                        }

                        if (nextPlanet.getOwner() == Owner.SELF) {
                        }

                        planetMap.put(nextPlanetID, nextPlanet);
                        planetMap.put(myPlanet.getId(), myPlanet);
                    }


                } else if (visiblePlanet.getOwner() == Owner.NEUTRAL) {

                } else if (visiblePlanet.getOwner() == Owner.OPPONENT) {

                }
            }
        }


    }

    public IPlanet findPlanet(int id ){
        IPlanet planet = planetMap.get(id);
        return planet;
    }


    @Override
    public String getName () {
        return "StrategyOne";
    }

    @Override
    public boolean compete () {
        return false;
    }

    public void makeGraph(List<IPlanet> planets) {
        makeEdgeList(planets);
        makeVertexList(planets);

        graph = new Graph(vertexes, edges);
    }

    //TODO
    public void makeEdgeList(List<IPlanet> planets) {

    }

    //TODO
    public void makeVertexList(List<IPlanet> planets) {

    }


    public class Vertex {
        final private String id;
        final private String name;


        public Vertex(String id, String name) {
            this.id = id;
            this.name = name;
        }
        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Vertex other = (Vertex) obj;
            if (id == null) {
                if (other.id != null)
                    return false;
            } else if (!id.equals(other.id))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return name;
        }
    }


    public class Edge  {
        private final String id;
        private final Vertex source;
        private final Vertex destination;
        private final int weight;

        public Edge(String id, Vertex source, Vertex destination, int weight) {
            this.id = id;
            this.source = source;
            this.destination = destination;
            this.weight = weight;
        }

        public String getId() {
            return id;
        }
        public Vertex getDestination() {
            return destination;
        }

        public Vertex getSource() {
            return source;
        }
        public int getWeight() {
            return weight;
        }

        @Override
        public String toString() {
            return source + " " + destination;
        }
    }


    public class Graph {
        private final List<Vertex> vertexes;
        private final List<Edge> edges;

        public Graph(List<Vertex> vertexes, List<Edge> edges) {
            this.vertexes = vertexes;
            this.edges = edges;
        }

        public List<Vertex> getVertexes() {
            return vertexes;
        }

        public List<Edge> getEdges() {
            return edges;
        }
    }


    public class DijkstraAlgorithm {

        private final List<Vertex> nodes;
        private final List<Edge> edges;
        private Set<Vertex> settledNodes;
        private Set<Vertex> unSettledNodes;
        private Map<Vertex, Vertex> predecessors;
        private Map<Vertex, Integer> distance;

        public DijkstraAlgorithm(Graph graph) {
            // create a copy of the array so that we can operate on this array
            this.nodes = new ArrayList<Vertex>(graph.getVertexes());
            this.edges = new ArrayList<Edge>(graph.getEdges());
        }

        public void execute(Vertex source) {
            settledNodes = new HashSet<Vertex>();
            unSettledNodes = new HashSet<Vertex>();
            distance = new HashMap<Vertex, Integer>();
            predecessors = new HashMap<Vertex, Vertex>();
            distance.put(source, 0);
            unSettledNodes.add(source);
            while (unSettledNodes.size() > 0) {
                Vertex node = getMinimum(unSettledNodes);
                settledNodes.add(node);
                unSettledNodes.remove(node);
                findMinimalDistances(node);
            }
        }

        private void findMinimalDistances(Vertex node) {
            List<Vertex> adjacentNodes = getNeighbors(node);
            for (Vertex target : adjacentNodes) {
                if (getShortestDistance(target) > getShortestDistance(node)
                        + getDistance(node, target)) {
                    distance.put(target, getShortestDistance(node)
                            + getDistance(node, target));
                    predecessors.put(target, node);
                    unSettledNodes.add(target);
                }
            }

        }

        private int getDistance(Vertex node, Vertex target) {
            for (Edge edge : edges) {
                if (edge.getSource().equals(node)
                        && edge.getDestination().equals(target)) {
                    return edge.getWeight();
                }
            }
            throw new RuntimeException("Should not happen");
        }

        private List<Vertex> getNeighbors(Vertex node) {
            List<Vertex> neighbors = new ArrayList<Vertex>();
            for (Edge edge : edges) {
                if (edge.getSource().equals(node)
                        && !isSettled(edge.getDestination())) {
                    neighbors.add(edge.getDestination());
                }
            }
            return neighbors;
        }

        private Vertex getMinimum(Set<Vertex> vertexes) {
            Vertex minimum = null;
            for (Vertex vertex : vertexes) {
                if (minimum == null) {
                    minimum = vertex;
                } else {
                    if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
                        minimum = vertex;
                    }
                }
            }
            return minimum;
        }

        private boolean isSettled(Vertex vertex) {
            return settledNodes.contains(vertex);
        }

        private int getShortestDistance(Vertex destination) {
            Integer d = distance.get(destination);
            if (d == null) {
                return Integer.MAX_VALUE;
            } else {
                return d;
            }
        }

        /*
         * This method returns the path from the source to the selected target and
         * NULL if no path exists
         */
        public LinkedList<Vertex> getPath(Vertex target) {
            LinkedList<Vertex> path = new LinkedList<Vertex>();
            Vertex step = target;
            // check if a path exists
            if (predecessors.get(step) == null) {
                return null;
            }
            path.add(step);
            while (predecessors.get(step) != null) {
                step = predecessors.get(step);
                path.add(step);
            }
            // Put it into the correct order
            Collections.reverse(path);
            return path;
        }
    }

}