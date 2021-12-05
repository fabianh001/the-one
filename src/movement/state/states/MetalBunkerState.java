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
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                0,
                0,
                20,
                20,
                20,
                5,
                2,
                10,
                5,
                5,
                5,
                1,
                3,
                1,
                3
        };
    }
}
