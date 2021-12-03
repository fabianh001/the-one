package movement.state.states;

public class OutdoorAreaState extends NodeState {
    @Override
    public String getStateName() {
        return "Outdoor Area";
    }

    @Override
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                0,
                0,
                20,
                22,
                5,
                8,
                8,
                10,
                5,
                5,
                8,
                3,
                2,
                1,
                3
        };
    }
}
