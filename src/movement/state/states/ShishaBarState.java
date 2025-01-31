package movement.state.states;

import core.Coord;

import java.util.Arrays;
import java.util.List;

public class ShishaBarState extends NodeState {
    @Override
    public String getStateName() {
        return "Shisha Bar";
    }

    @Override
    public List<Coord> getPolygon() {
        return Arrays.asList(
                new Coord(106.02,121.13),
                new Coord(111.76,125.05),
                new Coord(110.56,127.17),
                new Coord(105.12,126.56),
                new Coord(106.02,121.13)
        );
    }

    @Override
    public double minTimeInThisState() {
        return 1500;
    }

    @Override
    protected Double[] getIntervals() {
        return new Double[] {
                0.0,
                0.0,
                0.0,
                19.9,
                25.0,
                5.0,
                1.0,
                2.0,
                2.0,
                2.0,
                35.0,
                5.0,
                1.0,
                2.0,
                0.0,
                0.1
        };
    }
}
