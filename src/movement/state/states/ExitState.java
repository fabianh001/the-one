package movement.state.states;

import core.Coord;

import java.util.Arrays;
import java.util.List;

public class ExitState extends NodeState {
    @Override
    public String getStateName() {
        return "Exit";
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
    protected Double[] getIntervals() {
        return new Double[] {
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                100.0,
                0.0
        };
    }

    @Override
    public NodeState getNextState() {
        return null;
    }
}
