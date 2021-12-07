package report;

import core.*;
import movement.state.states.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnityMovementReport extends Report implements MovementListener {

    private HashMap<Integer, HashMap<String, Integer>> nodeMap = new HashMap<>();
    private static final NodeState[] states = new NodeState[]{
            new QueueState(),
            new EntryState(),
            new WardrobeState(),
            new MainStageState(),
            new OutdoorAreaState(),
            new MetalBunkerState(),
            new TechnoBunkerState(),
            new CocktailBarState(),
            new BeerBarState(),
            new ShotBarState(),
            new ShishaBarState(),
            new PizzaBarState(),
            new SideWCState(),
            new MainWCState(),
            new ExitState(),
            new WardrobeBeforeLeavingState()
    };

    @Override
    public void newDestination(DTNHost host, Coord destination, double speed) {
        HashMap<String, Integer> locationHashMap = nodeMap.get(host.getAddress());
//        destination
        String labelLocation = "Other";
        for (NodeState state : states) {
            if (isInside(state.getPolygon(), destination)) {
                labelLocation = state.getStateName();
                break;
            }
        }
        Integer count = locationHashMap.get(labelLocation);
        if (count == null) {
            locationHashMap.put(labelLocation, 1);
        } else {
            locationHashMap.put(labelLocation, count + 1);
        }
    }

    @Override
    public void initialLocation(DTNHost host, Coord location) {
        HashMap<String, Integer> locationHashMap = new HashMap<>();
        locationHashMap.put(new EntryState().getStateName(), 1);
        nodeMap.put(host.getAddress(), locationHashMap);
    }

    @Override
    public void done() {
        //write summary
        write("---- DONE ----");
        //HashMap.SimpleEntry<Integer, InfectionData>
        for(Map.Entry<Integer, HashMap<String, Integer>> entry : nodeMap.entrySet()){
            String locations = "";
            int total = 0;
            for(Map.Entry<String, Integer> counts : entry.getValue().entrySet()) {
                locations += counts.getKey() + ": " + counts.getValue() + "   |   ";
                total += counts.getValue();
            }
            locations += "Total Locations: " + total;
            write(entry.getKey()  + ": " + locations);
        }

        super.done();
    }

    //--------------- methods from ProhibitedPolygonRwp, methods needed to map point to a certain area on the map -------------

    private static boolean isInside(
            final List<Coord> polygon,
            final Coord point ) {
        final int count = countIntersectedEdges( polygon, point,
                new Coord( -10,0 ) );
        return ( ( count % 2 ) != 0 );
    }

    private static int countIntersectedEdges(
            final List<Coord> polygon,
            final Coord start,
            final Coord end ) {
        int count = 0;
        for ( int i = 0; i < polygon.size() - 1; i++ ) {
            final Coord polyP1 = polygon.get( i );
            final Coord polyP2 = polygon.get( i + 1 );

            final Coord intersection = intersection( start, end, polyP1, polyP2 );
            if ( intersection == null ) continue;

            if ( isOnSegment( polyP1, polyP2, intersection )
                    && isOnSegment( start, end, intersection ) ) {
                count++;
            }
        }
        return count;
    }

    private static boolean isOnSegment(
            final Coord L0,
            final Coord L1,
            final Coord point ) {
        final double crossProduct
                = ( point.getY() - L0.getY() ) * ( L1.getX() - L0.getX() )
                - ( point.getX() - L0.getX() ) * ( L1.getY() - L0.getY() );
        if ( Math.abs( crossProduct ) > 0.0000001 ) return false;

        final double dotProduct
                = ( point.getX() - L0.getX() ) * ( L1.getX() - L0.getX() )
                + ( point.getY() - L0.getY() ) * ( L1.getY() - L0.getY() );
        if ( dotProduct < 0 ) return false;

        final double squaredLength
                = ( L1.getX() - L0.getX() ) * ( L1.getX() - L0.getX() )
                + (L1.getY() - L0.getY() ) * (L1.getY() - L0.getY() );
        return !(dotProduct > squaredLength);
    }

    private static Coord intersection(
            final Coord L0_p0,
            final Coord L0_p1,
            final Coord L1_p0,
            final Coord L1_p1 ) {
        final double[] p0 = getParams( L0_p0, L0_p1 );
        final double[] p1 = getParams( L1_p0, L1_p1 );
        final double D = p0[ 1 ] * p1[ 0 ] - p0[ 0 ] * p1[ 1 ];
        if ( D == 0.0 ) return null;

        final double x = ( p0[ 2 ] * p1[ 1 ] - p0[ 1 ] * p1[ 2 ] ) / D;
        final double y = ( p0[ 2 ] * p1[ 0 ] - p0[ 0 ] * p1[ 2 ] ) / D;

        return new Coord( x, y );
    }

    private static double[] getParams(
            final Coord c0,
            final Coord c1 ) {
        final double A = c0.getY() - c1.getY();
        final double B = c0.getX() - c1.getX();
        final double C = c0.getX() * c1.getY() - c0.getY() * c1.getX();
        return new double[] { A, B, C };
    }


}
