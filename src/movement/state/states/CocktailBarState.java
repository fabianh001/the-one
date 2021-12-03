package movement.state.states;

import core.Coord;

import java.util.Arrays;
import java.util.List;

public class CocktailBarState extends NodeState {
    @Override
    public String getStateName() {
        return "Cocktail Bar";
    }

    @Override
    public List<Coord> getPolygon() {
        return Arrays.asList(
                new Coord(100.59,84.18),
                new Coord(98.03,86.95),
                new Coord(93.77,84.82),
                new Coord(91.21,82.05),
                new Coord(100.59,84.18)
        );
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
