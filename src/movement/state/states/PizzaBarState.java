package movement.state.states;

public class PizzaBarState extends NodeState {
    @Override
    public String getStateName() {
        return "Pizza Bar";
    }

    @Override
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                0,
                0,
                26,
                30,
                5,
                5,
                5,
                8,
                5,
                3,
                5,
                1,
                3,
                1,
                3
        };
    }
}
