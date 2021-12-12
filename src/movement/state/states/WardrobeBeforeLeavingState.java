package movement.state.states;

import core.Coord;

import java.util.Arrays;
import java.util.List;

public class WardrobeBeforeLeavingState extends NodeState {
    @Override
    public String getStateName() {
        return "Wardrobe before leaving";
    }

    @Override
    public List<Coord> getPolygon() {
        return Arrays.asList(
                new Coord(171.60,94.70),
                new Coord(171.25,98.13),
                new Coord(169.88,97.44),
                new Coord(170.40,95.22),
                new Coord(171.60,94.70)
        );
    }

    @Override
    protected Double[] getIntervals() {
        return new Double[] {
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                100.0,
                0.0
        };
    }
}
