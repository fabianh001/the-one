package movement.state.states;

import core.Coord;

import java.util.Arrays;
import java.util.List;

public class OutdoorAreaState extends NodeState {
    @Override
    public String getStateName() {
        return "Outdoor Area";
    }

    @Override
    public List<Coord> getPolygon() {
        return Arrays.asList(
                new Coord(98.33,101.51),
                new Coord(116.86,106.45),
                new Coord(111.04,124.45),
                new Coord(93.91,117.39),
                new Coord(98.33,101.51)
        );
    }

    @Override
    public double minTimeInThisState() {
        return 900;
    }

    @Override
    protected Double[] getIntervals() {
        return new Double[] {
                0.0,
                0.0,
                0.0,
                20.6,
                42.0,
                3.0,
                5.0,
                3.0,
                5.0,
                8.0,
                0.3,
                8.0,
                2.0,
                3.0,
                0.0,
                0.1
        };
    }
}
