package movement.state.states;

import core.Coord;

import java.util.Arrays;
import java.util.List;

public class TechnoBunkerState extends NodeState {
    @Override
    public String getStateName() {
        return "Techno Bunker";
    }

    @Override
    public List<Coord> getPolygon() {
        return Arrays.asList(
                new Coord(86.47,63.24),
                new Coord(98.86,65.82),
                new Coord(98.34,73.31),
                new Coord(85.44,71.76),
                new Coord(86.47,63.24)
        );
    }

    @Override
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                0,
                0,
                20,
                20,
                5,
                20,
                2,
                10,
                5,
                5,
                5,
                2,
                2,
                1,
                3
        };
    }
}
