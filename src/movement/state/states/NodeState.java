package movement.state.states;

public abstract class NodeState {
    protected double random;
    private Class[] allStates = getAllStates();

    public abstract String getStateName();
    protected abstract Integer[] getIntervals();

    public NodeState getNextState() {
        random = Math.random();
        Integer[] intervals = getIntervals();
        int lower = 0;
        int upper = 0;
        for (int i = 0; i < allStates.length; i++) {
            upper += intervals[i];
            if (isBetween(lower, upper)) {
                try {
                    return (NodeState) allStates[i].newInstance();
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
        double lowerDouble = lower / 100;
        double upperDouble = upper / 100;
        return lowerDouble <= random && random < upperDouble;
    }
}