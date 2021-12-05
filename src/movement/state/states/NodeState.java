package movement.state.states;

import core.Coord;

import java.util.Arrays;
import java.util.List;

public abstract class NodeState {
    protected double random;
    private Class[] allStates = getAllStates();

    public abstract String getStateName();
    protected abstract Integer[] getIntervals();

    // abstract and implemented in all subclasses
    public abstract List<Coord> getPolygon();

    public NodeState getNextState() {
        random = Math.random();
        Integer[] intervals = getIntervals();
        int lower = 0;
        int upper = 0;
        for (int i = 0; i < allStates.length; i++) {
            upper += intervals[i];
            if (isBetween(lower, upper)) {
                try {
                    NodeState ret = (NodeState) allStates[i].newInstance();
                    System.out.println(ret.getStateName());
                    return ret;
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            lower = upper;
        }
        //should never be called
        return null;
    }

    private Class[] getAllStates() {
        return new Class[] {
                QueueState.class,
                EntryState.class,
                WardrobeState.class,
                MainStageState.class,
                OutdoorAreaState.class,
                MetalBunkerState.class,
                TechnoBunkerState.class,
                CocktailBarState.class,
                BeerBarState.class,
                ShotBarState.class,
                ShishaBarState.class,
                PizzaBarState.class,
                SideWCState.class,
                MainWCState.class,
                ExitState.class,
                WardrobeBeforeLeavingState.class
        };
    }
    private boolean isBetween(int lower, int upper) {
        double lowerDouble = lower / 100.0;
        double upperDouble = upper / 100.0;
        return lowerDouble <= random && random < upperDouble;
    }
}
