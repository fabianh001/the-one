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
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                0,
                0,
                30,
                20,
                5,
                8,
                10,
                12,
                2,
                2,
                3,
                2,
                2,
                1,
                3,
        };
    }
}
