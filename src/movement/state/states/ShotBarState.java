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
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                0,
                0,
                40,
                16,
                8,
                5,
                4,
                5,
                4,
                4,
                5,
                2,
                3,
                1,
                3
        };
    }
}
