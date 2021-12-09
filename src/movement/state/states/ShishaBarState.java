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
        return 900;
    }

    @Override
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                0,
                0,
                20,
                50,
                2,
                2,
                2,
                5,
                5,
                1,
                5,
                1,
                3,
                1,
                3
        };
    }
}
