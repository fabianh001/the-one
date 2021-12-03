package movement.state.states;

public class EntryState extends NodeState {

    @Override
    public String getStateName() {
        return "Entry";
    }

    @Override
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                0,
                90,
                2,
                2,
                0,
                0,
                1,
                2,
                0,
                1,
                1,
                0,
                1,
                0,
                0
        };
    }
}
