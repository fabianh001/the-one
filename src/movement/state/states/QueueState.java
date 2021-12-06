package movement.state.states;

import core.Coord;

import java.util.Arrays;
import java.util.List;

public class QueueState extends NodeState {

    @Override
    public String getStateName() {
        return "Queue";
    }

    @Override
    public List<Coord> getPolygon() {
        return Arrays.asList(
                new Coord(241.07,9.05),
                new Coord(266.08,12.24),
                new Coord(218.19,114.95),
                new Coord(202.22,112.29),
                new Coord(241.07,9.05)
        );
    }

    @Override
    protected Integer[] getIntervals() {
        return new Integer[] {
                50,
                50,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0
        };
    }
}



