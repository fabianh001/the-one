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
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                0,
                0,
                35,
                13,
                3,
                10,
                15,
                5,
                5,
                3,
                5,
                1,
                1,
                1,
                3
        };
    }
}
