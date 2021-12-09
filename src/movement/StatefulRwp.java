package movement;

import core.Coord;
import core.Settings;
import core.SimClock;
import movement.state.states.*;

import java.util.*;

/**
 * Example of a state-machine driven node mobility. Each node has two states
 * LEFT and RIGHT that influence the picking of the next waypoint. Nodes
 * transition between the states with some probability defined by the state
 * transition diagram.
 *
 * @author teemuk
 */

public class StatefulRwp
        extends MovementModel {

    //==========================================================================//
    // Instance vars
    //==========================================================================//
    private Coord lastWaypoint;

    private NodeState state;

    private boolean isActive = true;

    private double startTimeOfCurrentState = 0;
    //==========================================================================//


    private void addDoorCoordinate(String oldState, Path p) {

        // handle edge case if no new state was selected
        if (state == null) {
            return;
        }

        // new goal state
        final String newState = this.state.getStateName();

        // outdoor states
        final HashSet<String> outdoorStates = new HashSet<String>(Arrays.asList("Shisha Bar", "Pizza Bar", "Outdoor Area"));
        // indoor states
        final HashSet<String> indoorStates = new HashSet<>(Arrays.asList(
                "Main Stage", "Shot Bar", "Cocktail Bar", "Beer Bar", "Side WC", "Main WC", "Techno Bunker",
                "Wardrobe", "Wardrobe before leaving", "Entry", "Exit"
        ));

        // coordinate position of outdoor door
        final Coord doorOutdoor = new Coord(112.72, 105.35);
        // door between metal bunker and indoor states
        final Coord metalBunkerDoor = new Coord(126.00, 99.93);
        //
        final Coord entryCoorindate = new Coord(210,107);

        // add door coordinate if nodes go from indoor to outdoor state
        if (outdoorStates.contains(newState) && indoorStates.contains(oldState)) {
            p.addWaypoint(doorOutdoor);
        }
        // add door coordinate if nodes go from outdoor to indoor state
        if (outdoorStates.contains(oldState) && indoorStates.contains(newState)) {
            p.addWaypoint(doorOutdoor);
        }

        // add door if leaving from metal bunker to indoor state or vice versa
        if (oldState.equals("Metal Bunker") && indoorStates.contains(newState)) {
            p.addWaypoint(metalBunkerDoor);
        }
        if (indoorStates.contains(oldState) && newState.equals("Metal Bunker")) {
            p.addWaypoint(metalBunkerDoor);
        }

        // add waypoint before entering building
        if (newState.equals("Entry")) {
            p.addWaypoint(entryCoorindate);
        }
    }


    //==========================================================================//
    // Implementation
    //==========================================================================//
    @Override
    public Path getPath() {
        // deactivate node if he is at ubahn
        if (this.lastWaypoint.getX() == 450 && this.lastWaypoint.getY() == 0) {
            isActive = false;
        }

        // save current state as String
        final String oldState;
        if (state != null) {
            oldState = this.state.getStateName();
        } else {
            oldState = "no old state was found";
        }


        // Update state machine every time we pick a path
        this.state = this.updateState(this.state);

        // Create the path
        final Path p;
        p = new Path(generateSpeed());
        p.addWaypoint(lastWaypoint.clone());


        // Add waypoints for doors if switching between indoor and outdoor state or metal bunker
        addDoorCoordinate(oldState, p);

        //Go to uBahn if state is null (happens after exitState)
        if (state == null) {
            Coord c1 = new Coord(210,107);
            Coord c2 = new Coord(450, 0);
            p.addWaypoint(c1);
            p.addWaypoint(c2);
            this.lastWaypoint = c2;
            return p;
        }

        // Stop doing Rwp if it shouldn't do it in this state and the node is already in the polygon
        if (!this.state.shouldDoRwpInPolygon() && isInside(this.state.getPolygon(), this.lastWaypoint)) {
            return null;
        }

        //Rwp in (or to the) polygon of the current state
        Coord c;
        do {
            c = this.randomCoord(this.state.getPolygon());
        } while (!isInside(this.state.getPolygon(), c));
        p.addWaypoint(c);

        this.lastWaypoint = c;
        return p;
    }

    @Override
    public boolean isActive() {
        return this.isActive;
    }

    @Override
    public Coord getInitialLocation() {
        do {
            this.lastWaypoint = this.randomCoord(this.state.getPolygon());
        } while (!isInside(this.state.getPolygon(), this.lastWaypoint));
        return this.lastWaypoint;
    }

    @Override
    public MovementModel replicate() {
        return new StatefulRwp(this);
    }

    private Coord randomCoord(List<Coord> polygon) {
        Random r = new Random();
        double minX = minXFromPolygon(polygon);
        double minY = minYFromPolygon(polygon);
        double maxX = maxXFromPolygon(polygon);
        double maxY = maxYFromPolygon(polygon);
        double xCord = minX + (maxX - minX) * r.nextDouble();
        double yCord = minY + (maxY - minY) * r.nextDouble();
        return new Coord(xCord, yCord);
    }

    private Double minXFromPolygon(List<Coord> polygon) {
        double minX = super.getMaxX();
        for (Coord c : polygon) {
            if (c.getX() < minX) {
                minX = c.getX();
            }
        }
        return minX;
    }

    private Double maxXFromPolygon(List<Coord> polygon) {
        double maxX = 0;
        for (Coord c : polygon) {
            if (c.getX() > maxX) {
                maxX = c.getX();
            }
        }
        return maxX;
    }

    private Double maxYFromPolygon(List<Coord> polygon) {
        double maxY = 0;
        for (Coord c : polygon) {
            if (c.getY() > maxY) {
                maxY = c.getY();
            }
        }
        return maxY;
    }

    private Double minYFromPolygon(List<Coord> polygon) {
        double minY = super.getMaxY();
        for (Coord c : polygon) {
            if (c.getY() < minY) {
                minY = c.getY();
            }
        }
        return minY;
    }

    //==========================================================================//


    //==========================================================================//
    // Construction
    //==========================================================================//
    public StatefulRwp(final Settings settings) {
        super(settings);
        this.state = new QueueState();
    }

    public StatefulRwp(final StatefulRwp other) {
        super(other);

        // Pick a random state every time we replicate rather than copying!
        // Otherwise every node would start in the same state.
        this.state = new QueueState();
    }
    //==========================================================================//

    //==========================================================================//
    // Private - geometry
    //==========================================================================//
    private static boolean isInside(
            final List<Coord> polygon,
            final Coord point) {
        final int count = countIntersectedEdges(polygon, point,
                new Coord(-10, 0));
        return ((count % 2) != 0);
    }

    private static boolean pathIntersects(
            final List<Coord> polygon,
            final Coord start,
            final Coord end) {
        final int count = countIntersectedEdges(polygon, start, end);
        return (count > 0);
    }

    private static int countIntersectedEdges(
            final List<Coord> polygon,
            final Coord start,
            final Coord end) {
        int count = 0;
        for (int i = 0; i < polygon.size() - 1; i++) {
            final Coord polyP1 = polygon.get(i);
            final Coord polyP2 = polygon.get(i + 1);

            final Coord intersection = intersection(start, end, polyP1, polyP2);
            if (intersection == null) continue;

            if (isOnSegment(polyP1, polyP2, intersection)
                    && isOnSegment(start, end, intersection)) {
                count++;
            }
        }
        return count;
    }

    private static boolean isOnSegment(
            final Coord L0,
            final Coord L1,
            final Coord point) {
        final double crossProduct
                = (point.getY() - L0.getY()) * (L1.getX() - L0.getX())
                - (point.getX() - L0.getX()) * (L1.getY() - L0.getY());
        if (Math.abs(crossProduct) > 0.0000001) return false;

        final double dotProduct
                = (point.getX() - L0.getX()) * (L1.getX() - L0.getX())
                + (point.getY() - L0.getY()) * (L1.getY() - L0.getY());
        if (dotProduct < 0) return false;

        final double squaredLength
                = (L1.getX() - L0.getX()) * (L1.getX() - L0.getX())
                + (L1.getY() - L0.getY()) * (L1.getY() - L0.getY());
        if (dotProduct > squaredLength) return false;

        return true;
    }

    private static Coord intersection(
            final Coord L0_p0,
            final Coord L0_p1,
            final Coord L1_p0,
            final Coord L1_p1) {
        final double[] p0 = getParams(L0_p0, L0_p1);
        final double[] p1 = getParams(L1_p0, L1_p1);
        final double D = p0[1] * p1[0] - p0[0] * p1[1];
        if (D == 0.0) return null;

        final double x = (p0[2] * p1[1] - p0[1] * p1[2]) / D;
        final double y = (p0[2] * p1[0] - p0[0] * p1[2]) / D;

        return new Coord(x, y);
    }

    private static double[] getParams(
            final Coord c0,
            final Coord c1) {
        final double A = c0.getY() - c1.getY();
        final double B = c0.getX() - c1.getX();
        final double C = c0.getX() * c1.getY() - c0.getY() * c1.getX();
        return new double[]{A, B, C};
    }

    //==========================================================================//

    //==========================================================================//
    // Private - State machine
    //==========================================================================//

    /**
     * This method defines the transitions in the state machine.
     *
     * @param state the current state
     * @return the next state dependent on the time passed by
     */
    private NodeState updateState(NodeState state) {
        final double curTime = SimClock.getTime();
        final double random = Math.random();

        if (state == null) {
            return null;
        }

        if (curTime < startTimeOfCurrentState + state.minTimeInThisState()) {
            return this.state;
        }

        //20:30 - 21:00
        if (curTime < 1800) {
            return state;
        }

        startTimeOfCurrentState = curTime;

        if (state instanceof QueueState) {
            return state.getNextState();
        }

        //21:00 - 22:00 Beer Happy Hour
        if (curTime < 5400 && random < 0.15) {
            NodeState newState = new BeerBarState();
            return newState;
        }

        //21:00 - 22:00 & 3:30 - 4:00 People get more snacks
        if ((curTime < 5400 || curTime > 25200) && random < 0.1) {
            NodeState newState = new PizzaBarState();
            return newState;
        }

        //01:00 - 01:30 last regular u-bahn so more people are leaving
        if (curTime > 16200 && curTime < 18000 && random < 0.15) {
            NodeState newState = new WardrobeBeforeLeavingState();
            return newState;
        }

        //4:15 party closes (at 4:30) so people leave with very high probability
        if (curTime > 27900 && random < 0.9) {
            NodeState newState = new WardrobeBeforeLeavingState();
            return newState;
        }

        return state.getNextState();
    }
    //==========================================================================//
}
