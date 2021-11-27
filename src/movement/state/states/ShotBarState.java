package movement.state.states;

public class ShotBarState extends NodeState {
    @Override
    public String getStateName() {
        return "Shot Bar";
    }

    @Override
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                0,
                0,
                40,
                16,
                8,
                5,
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
