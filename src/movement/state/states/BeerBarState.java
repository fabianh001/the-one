package movement.state.states;

public class BeerBarState extends NodeState {
    @Override
    public String getStateName() {
        return "Beer Bar";
    }

    @Override
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                0,
                0,
                40,
                11,
                10,
                8,
                4,
                5,
                4,
                4,
                5,
                2,
                3,
                1,
                3
        };
    }
}
