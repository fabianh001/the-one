package movement.state.states;

import core.Coord;

import java.util.Arrays;
import java.util.List;

public class SideWCState extends NodeState {
    @Override
    public String getStateName() {
        return "Side WC";
    }

    @Override
    public List<Coord> getPolygon() {
        return Arrays.asList(
                new Coord(88.51,89.78),
                new Coord(94.41,91.38),
                new Coord(94.57,95.68),
                new Coord(87.87,94.57),
                new Coord(88.51,89.78)
        );
    }

    @Override
    protected Double[] getIntervals() {
        return new Double[] {
                0.0,
                0.0,
                0.0,
                55.0,
                5.0,
                1.8,
                4.0,
                15.0,
                10.0,
                4.0,
                0.1,
                5.0,
                0.0,
                0.0,
                0.0,
                0.1
        };
    }
}
