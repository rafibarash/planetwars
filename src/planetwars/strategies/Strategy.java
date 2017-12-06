//package planetwars.strategies;
//
//import planetwars.publicapi.*;
//import java.util.*;
//
//
//public class Strategy implements IStrategy{
//    private Map<Integer, IPlanet> planetMap = new HashMap<>();
//    private PriorityQueue<Integer> planetEdgePQ;
//    private List<IVisiblePlanet> OwnedPlanets;
//
//    @Override
//    public void takeTurn(List<IPlanet> planets, IPlanetOperations planetOperations, Queue<IEvent> eventsToExecute) {
//        // Initialize ArrayList and HashMap data structures
//        OwnedPlanets = new ArrayList<>();
//        for (IPlanet planet : planets) {
//            int id = planet.getId();
//            planetMap.put(id, planet);
//            if (planet instanceof IVisiblePlanet){
//                IVisiblePlanet visiblePlanet = (IVisiblePlanet) planet;
//                if(visiblePlanet.getOwner()==Owner.SELF){
//                    OwnedPlanets.add(visiblePlanet);
//                }
//            }
//        }
//
//        for (IVisiblePlanet myPlanet : OwnedPlanets) {
//            int pop = (int) myPlanet.getPopulation();
//
//            Set<IEdge> edges = myPlanet.getEdges();
//            Iterator<IEdge> iterator = edges.iterator();
//
//            // Initialize planetEdgePQ
//            Comparator<Integer> planetEdgeComparator = new PlanetEdgeComparator();
//            planetEdgePQ = new PriorityQueue<>(edges.size() + 1, planetEdgeComparator);
//            planetEdgePQ.add(myPlanet.getId());
//
//            // for use after iterating through neighbors
//            boolean surroundedByMyPlanets = true;
//
//            // iterate through neighbors and check if need to attack or colonize
//            while (iterator.hasNext()) {
//                // get next planet
//                IEdge nextEdge = iterator.next();
//                int nextPlanetID = nextEdge.getDestinationPlanetId();
//                IVisiblePlanet nextPlanet = (IVisiblePlanet) findPlanet(nextPlanetID);
//                // if next planet neutral colonize and send one person
//                if (nextPlanet.getOwner() == Owner.NEUTRAL && pop > 1) {
//                    eventsToExecute.add(planetOperations.transferPeople(myPlanet, nextPlanet, 1));
//                    pop--;
//                }
//                // if next planet opponent and I have enough people attack
//                if (nextPlanet.getOwner() == Owner.OPPONENT && pop >= nextPlanet.getPopulation() * 0.85) {
//                    eventsToExecute.add(planetOperations.transferPeople(myPlanet, nextPlanet, (long) Math.ceil(pop * 0.3)));
//                    pop -= Math.ceil(pop * 0.3);
//                }
//                // if next planet is my own add to priority queue
//                if (nextPlanet.getOwner() != Owner.SELF) {
//                    surroundedByMyPlanets = false;
//                } else {
//                    planetEdgePQ.add(nextPlanetID);
//                }
//            }
//
//            // if planet in good situation, send people to neighbor planet with most edges
//            IVisiblePlanet priorityEdgePlanet = (IVisiblePlanet) findPlanet(planetEdgePQ.peek());
//            if (pop >= 0.54 * myPlanet.getSize() &&
//                    surroundedByMyPlanets &&
//                    priorityEdgePlanet != myPlanet) {
//                eventsToExecute.add(planetOperations.transferPeople(myPlanet, priorityEdgePlanet, (long) Math.ceil(pop * 0.1)));
//                pop -= Math.ceil(pop * 0.1);
//            }
//        }
//    }
//
//    private IPlanet findPlanet(int id ){
//        IPlanet planet = planetMap.get(id);
//        return planet;
//    }
//
//
//    private class PlanetEdgeComparator implements Comparator<Integer> {
//        @Override
//        public int compare(Integer p1, Integer p2) {
//            IPlanet planet1 = findPlanet(p1);
//            IPlanet planet2 = findPlanet(p2);
//            return -1*Integer.compare(planet1.getEdges().size(), planet2.getEdges().size());
//        }
//    }
//
//
//
//    @Override
//    public String getName () {
//        return "Guardians Of The Galaxy";
//    }
//
//    @Override
//    public boolean compete () {
//        return true;
//    }
//
//}