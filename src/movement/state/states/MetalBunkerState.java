package movement.state.states;

public class MetalBunkerState extends NodeState {
    @Override
    public String getStateName() {
        return "Metal Bunker";
    }

    @Override
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                0,
                0,
                20,
                20,
                20,
                5,
                2,
                10,
                5,
                5,
                5,
                1,
                3,
                1,
                3
        };
    }
}
