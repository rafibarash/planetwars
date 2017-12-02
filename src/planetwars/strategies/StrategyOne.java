package planetwars.strategies;

import planetwars.publicapi.*;

import java.util.*;



public class StrategyOne implements IStrategy{
    private Map<Integer, IPlanet> planetMap = new HashMap<>();

    @Override
    public void takeTurn(List<IPlanet> planets, IPlanetOperations planetOperations, Queue<IEvent> eventsToExecute) {
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

}