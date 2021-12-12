package movement.state.states;

import core.Coord;

import java.util.Arrays;
import java.util.List;

public class BeerBarState extends NodeState {
    @Override
    public String getStateName() {
        return "Beer Bar";
    }

    @Override
    public List<Coord> getPolygon() {
        return Arrays.asList(
            new Coord(151.07,105.52),
            new Coord(115.64,96.17),
            new Coord(115.64,93.84),
            new Coord(150.10,102.60),
            new Coord(151.07,105.52)
        );
    }

    @Override
    protected Double[] getIntervals() {
        return new Double[] {
                0.0,
                0.0,
                0.0,
                76.8,
                7.0,
                4.0,
                4.0,
                1.0,
                2.0,
                1.0,
                0.1,
                1.0,
                1.0,
                2.0,
                0.0,
                0.1
        };
    }
}
