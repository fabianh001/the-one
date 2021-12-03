package movement.state.states;

public class MainWCState extends NodeState {
    @Override
    public String getStateName() {
        return "Main WC";
    }

    @Override
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                0,
                0,
                38,
                12,
                7,
                2,
                5,
                15,
                5,
                3,
                5,
                1,
                1,
                2,
                4
        };
    }
}
