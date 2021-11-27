package movement.state.states;

public class ExitState extends NodeState {
    @Override
    public String getStateName() {
        return "Exit";
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
