package movement.state.states;

import core.Coord;

import java.util.Arrays;
import java.util.List;

public class PizzaBarState extends NodeState {
    @Override
    public String getStateName() {
        return "Pizza Bar";
    }

    @Override
    public List<Coord> getPolygon() {
        return Arrays.asList(
                new Coord(94.27,119.16),
                new Coord(97.62,118.10),
                new Coord(98.33,124.45),
                new Coord(92.50,123.57),
                new Coord(94.27,119.16)
        );
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
