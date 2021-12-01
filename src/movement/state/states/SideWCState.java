package movement.state.states;

public class SideWCState extends NodeState {
    @Override
    public String getStateName() {
        return "Side WC";
    }

    @Override
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                0,
                0,
                35,
                13,
                3,
                10,
                15,
                5,
                5,
                3,
                5,
                1,
                1,
                1,
                3
        };
    }
}
