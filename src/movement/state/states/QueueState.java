package movement.state.states;

public class QueueState extends NodeState {

    @Override
    public String getStateName() {
        return "Queue";
    }

    @Override
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                100,
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
                0
        };
    }
}



