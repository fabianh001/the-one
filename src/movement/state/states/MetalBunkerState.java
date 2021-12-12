package movement.state.states;

import core.Coord;

import java.util.Arrays;
import java.util.List;

public class MetalBunkerState extends NodeState {
    @Override
    public String getStateName() {
        return "Metal Bunker";
    }

    @Override
    public List<Coord> getPolygon() {
        return Arrays.asList(
                new Coord(124.99,123.74),
                new Coord(124.99,126.24),
                new Coord(122.84,132.68),
                new Coord(116.41,132.14),
                new Coord(124.99,123.74)
        );
    }

    @Override
    public double minTimeInThisState() {
        return 1200;
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
                50.0,
                3.0,
                2.0,
                8.0,
                1.7,
                0.2,
                1.0,
                1.0,
                3.0,
                0.0,
                0.1
        };
    }
}
