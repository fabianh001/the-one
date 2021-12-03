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
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                0,
                0,
                38,
                12,
                7,
                2,
                5,
                15,
                5,
                3,
                5,
                1,
                1,
                2,
                4
        };
    }
}
