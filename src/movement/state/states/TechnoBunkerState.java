package movement.state.states;

public class TechnoBunkerState extends NodeState {
    @Override
    public String getStateName() {
        return "Techno Bunker";
    }

    @Override
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                0,
                0,
                20,
                20,
                5,
                20,
                2,
                10,
                5,
                5,
                5,
                2,
                2,
                1,
                3
        };
    }
}
