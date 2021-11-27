package movement.state.states;

public class WardrobeBeforeLeavingState extends NodeState {
    @Override
    public String getStateName() {
        return "Wardrobe before leaving";
    }

    @Override
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                100,
                0
        };
    }
}
