package movement.state.states;

import core.Coord;

import java.util.Arrays;
import java.util.List;

public class ShotBarState extends NodeState {
    @Override
    public String getStateName() {
        return "Shot Bar";
    }

    @Override
    public List<Coord> getPolygon() {
        return Arrays.asList(
                new Coord(117.53,96.00),
                new Coord(124.55,98.23),
                new Coord(122.16,102.06),
                new Coord(116.10,100.79),
                new Coord(117.53,96.00)
        );
    }

    @Override
    protected Double[] getIntervals() {
        return new Double[] {
                0.0,
                0.0,
                0.0,
                48.0,
                30.8,
                4.0,
                4.0,
                1.0,
                1.0,
                3.0,
                0.1,
                3.0,
                2.0,
                3.0,
                0.0,
                0.1
        };
    }
}
