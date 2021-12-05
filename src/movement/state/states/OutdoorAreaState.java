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
    protected Integer[] getIntervals() {
        return new Integer[] {
                0,
                0,
                0,
                20,
                22,
                5,
                8,
                8,
                10,
                5,
                5,
                8,
                3,
                2,
                1,
                3
        };
    }
}
