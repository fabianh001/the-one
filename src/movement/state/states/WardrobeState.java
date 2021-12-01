package movement.state.states;

public class WardrobeState extends NodeState {
    @Override
    public String getStateName() {
        return "Wardrobe";
    }

    @Override
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                0,
                0,
                25,
                25,
                3,
                4,
                8,
                15,
                5,
                5,
                5,
                1,
                3,
                0,
                1
        };
    }
}
