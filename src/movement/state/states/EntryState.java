package movement.state.states;

import core.Coord;

import java.util.Arrays;
import java.util.List;

public class EntryState extends NodeState {

    @Override
    public String getStateName() {
        return "Entry";
    }

    @Override
    public List<Coord> getPolygon() {
        return Arrays.asList(
                new Coord(184.25,108.94),
                new Coord(183.06,113.57),
                new Coord(170.35,110.28),
                new Coord(173.34,104.75),
                new Coord(177.23,107.89),
                new Coord(184.25,108.94)
        );
    }

    @Override
    public double minTimeInThisState() {
        return 20;
    }

    @Override
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                0,
                90,
                2,
                2,
                0,
                0,
                1,
                2,
                0,
                1,
                1,
                0,
                1,
                0,
                0
        };
    }
}
