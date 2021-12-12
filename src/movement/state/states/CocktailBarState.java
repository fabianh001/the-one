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
    protected Double[] getIntervals() {
        return new Double[] {
                0.0,
                0.0,
                0.0,
                78.0,
                7.0,
                3.0,
                5.3,
                2.0,
                1.0,
                1.0,
                0.1,
                1.0,
                1.0,
                0.5,
                0.0,
                0.1
        };
    }
}
