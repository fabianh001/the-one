package movement.state.states;

import core.Coord;

import java.util.Arrays;
import java.util.List;

public class MainStageState extends NodeState {

    @Override
    public String getStateName() {
        return "Main Stage";
    }

    @Override
    public List<Coord> getPolygon() {
        return Arrays.asList(
                new Coord(155.86,91.00),
                new Coord(155.26,100.27),
                new Coord(113.87,90.56),
                new Coord(123.88,87.42),
                new Coord(155.86,91.00)
        );
    }

    @Override
    public boolean shouldDoRwpInPolygon() {
        return true;
    }

    @Override
    public double minTimeInThisState() {
        return 1200;
    }

    @Override
    protected Double[] getIntervals() {
        return new Double[] {
                0.0,
                0.0,
                0.0,
                73.0,
                8.0,
                3.0,
                4.0,
                3.0,
                4.0,
                1.5,
                0.1,
                0.3,
                1.0,
                2.0,
                0.0,
                0.1,
        };
    }
}
