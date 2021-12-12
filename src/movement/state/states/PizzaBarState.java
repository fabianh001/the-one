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
    protected Double[] getIntervals() {
        return new Double[] {
                0.0,
                0.0,
                0.0,
                30.8,
                32.0,
                7.0,
                3.0,
                3.0,
                8.0,
                5.0,
                0.3,
                7.0,
                2.8,
                1.0,
                0.0,
                0.1
        };
    }
}
