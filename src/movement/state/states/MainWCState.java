package movement.state.states;

import core.Coord;

import java.util.Arrays;
import java.util.List;

public class MainWCState extends NodeState {
    @Override
    public String getStateName() {
        return "Main WC";
    }

    @Override
    public List<Coord> getPolygon() {
        return Arrays.asList(
                new Coord(156.61,88.46),
                new Coord(156.46,90.56),
                new Coord(152.42,89.06),
                new Coord(152.87,87.12),
                new Coord(156.61,88.46)
        );
    }

    @Override
    protected Double[] getIntervals() {
        return new Double[] {
                0.0,
                0.0,
                0.0,
                61.4,
                7.0,
                4.0,
                2.0,
                5.0,
                15.0,
                2.0,
                0.1,
                3.0,
                0.0,
                0.0,
                0.0,
                0.5
        };
    }
}
