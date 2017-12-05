package planetwars.strategies;

import planetwars.publicapi.*;
import java.util.*;


public class StrategyTwo implements IStrategy{
    private Map<Integer, IPlanet> planetMap = new HashMap<>();
//    private PriorityQueue<IPlanet> planetPQ;
//    private int turn=0;

    @Override
    public void takeTurn(List<IPlanet> planets, IPlanetOperations planetOperations, Queue<IEvent> eventsToExecute) {
//        if (turn == 0) {
//            initializePlanetPriority();
//            turn++;
//        }
        for (IPlanet planet : planets) {
            int id = planet.getId();
            planetMap.put(id, planet);
        }

        for (IPlanet planet : planetMap.values()) {
            if (planet instanceof IVisiblePlanet) {
                IVisiblePlanet visiblePlanet = (IVisiblePlanet) planet;
                if (visiblePlanet.getOwner() == Owner.SELF) {
                    IVisiblePlanet myPlanet = visiblePlanet;
                    int pop = (int)myPlanet.getPopulation();

                    Set<IEdge> edges = visiblePlanet.getEdges();
                    Iterator<IEdge> iterator = edges.iterator();

                    boolean myPlanetHasMostEdges = true;
                    IVisiblePlanet planetWithMostEdges = myPlanet;
                    boolean surroundedByMyPlanets = true;

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

                        // Check if neighboring planet has more edges than current
                        if (nextPlanet.getEdges().size() > myPlanet.getEdges().size()) {
                            planetWithMostEdges = nextPlanet;
                            myPlanetHasMostEdges = false;
                        }

                        if (nextPlanet.getOwner() != Owner.SELF) {
                            surroundedByMyPlanets = false;
                        }
                    }

                    if (pop >= 0.54*myPlanet.getSize() &&
                            !myPlanetHasMostEdges &&
                            surroundedByMyPlanets) {
                        eventsToExecute.add(planetOperations.transferPeople(myPlanet, planetWithMostEdges, (long)Math.ceil(pop*0.1)));
                        pop -= pop*0.1;
                    }
                }
            }
        }


    }

    public IPlanet findPlanet(int id ){
        IPlanet planet = planetMap.get(id);
        return planet;
    }

//    // add planets to the queue in order of number of edges
//    public void initializePlanetPriority() {
//        Comparator<IPlanet> comparator = new PlanetEdgeComparator();
//        int numPlanets = planetMap.size();
//        planetPQ = new PriorityQueue<IPlanet>(numPlanets, comparator);
//        for (IPlanet planet : planetMap.values()) {
//            planetPQ.add(planet);
//        }
//    }
//
//    private class PlanetEdgeComparator implements Comparator<IPlanet> {
//        @Override
//        public int compare(IPlanet p1, IPlanet p2) {
//            if (p1.getEdges().size() < p2.getEdges().size()) {
//                return -1;
//            } else if (p1.getEdges().size() > p2.getEdges().size()) {
//                return 1;
//            }
//            return 0;
//        }
//    }


    @Override
    public String getName () {
        return "StrategyOne";
    }

    @Override
    public boolean compete () {
        return false;
    }

}