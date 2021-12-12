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
    public double minTimeInThisState() {
        return 600;
    }

    @Override
    public boolean shouldDoRwpInPolygon() {
        return true;
    }

    @Override
    protected Double[] getIntervals() {
        return new Double[] {
                0.0,
                0.0,
                0.0,
                20.0,
                10.0,
                2.0,
                50.0,
                5.8,
                5.0,
                2.0,
                0.1,
                1.0,
                2.0,
                2.0,
                0.0,
                0.1,
        };
    }
}
