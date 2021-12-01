package movement.state.states;

public class ShishaBarState extends NodeState {
    @Override
    public String getStateName() {
        return "Shisha Bar";
    }

    @Override
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                0,
                0,
                20,
                50,
                2,
                2,
                2,
                5,
                5,
                1,
                5,
                1,
                3,
                1,
                3
        };
    }
}
