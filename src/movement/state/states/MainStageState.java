package movement.state.states;

public class MainStageState extends NodeState {

    @Override
    public String getStateName() {
        return "Main Stage";
    }

    @Override
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                0,
                0,
                30,
                20,
                5,
                8,
                10,
                12,
                2,
                2,
                3,
                2,
                2,
                1,
                3,
        };
    }
}
