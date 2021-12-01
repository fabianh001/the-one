package movement.state.states;

public class CocktailBarState extends NodeState {
    @Override
    public String getStateName() {
        return "Cocktail Bar";
    }

    @Override
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                0,
                0,
                40,
                22,
                8,
                4,
                3,
                5,
                3,
                3,
                3,
                3,
                2,
                1,
                3
        };
    }
}
