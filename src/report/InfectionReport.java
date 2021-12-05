package report;

import core.Coord;
import core.DTNHost;
import core.Message;
import core.MessageListener;
import movement.state.states.*;

import java.util.*;



public class InfectionReport
        extends Report
        implements MessageListener {

    private HashMap<Integer, InfectionData> infectionTracker;
    private Random random;
    private double[] factorDistances;
    private double[] distances;
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

    public InfectionReport(){
        init();
    }

    @Override
    protected void init() {
        super.init();
        random = new Random();
        infectionTracker = new HashMap<>();
        factorDistances = new double[] {1.0, 1.0, 0.5, 0.05, 0.001};
        distances = new double[]{0.0, 0.5, 1.0, 1.5, 3.0};
    }

    @Override
    public void newMessage(Message m) {

    }

    @Override
    public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {

    }

    @Override
    public void messageDeleted(Message m, DTNHost where, boolean dropped) {

    }

    @Override
    public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {

    }

    @Override
    public void messageTransferred(Message m, DTNHost from, DTNHost to, boolean firstDelivery) {
        if(m.getFrom().getAddress() != from.getAddress()) return; //only count messages that are from the first sender
        // infected group starts with group ID "I"
        if(to.toString().startsWith("I")) return;
        //if(m.getFrom().getAddress() == to.getAddress()) return;  // message travelled in a loop
        int toAddr = to.getAddress();
        boolean isFirstInfection;
        // find correct factor
        double curDist = from.getLocation().distance(to.getLocation());
        double factorDist = factorDistances[0];
        if(curDist >= distances[distances.length -1]){
            // distance greater than maximum transmission range
            return;
        }
        for(int i = 1; i < factorDistances.length; i++){
            if(curDist <= distances[i]){
                // interpolate distance factor linearly
                double percentage = (curDist - distances[i-1])/(distances[i] - distances[i-1]);
                factorDist = factorDistances[i-1] + percentage * (factorDistances[i] - factorDistances[i-1]);
                break;
            }
        }
        //write(factorDist + "," + curDist);

        if(infectionTracker.containsKey(toAddr)){
            isFirstInfection = infectionTracker.get(toAddr).addParticles((int)(factorDist * m.getSize()));
        }else{

            // 1/(30 * 4)
            // One person distributes 1000 particles per second to another person
            // mean time for infection: 5 minutes
            // ==> 300,000 points
            // test after one hour (1000 particles/second): highest value approx. 28,000 particles
            InfectionData data = new InfectionData(random, 1.0/600.0);
            isFirstInfection = data.addParticles((int)(factorDist * m.getSize()));
            infectionTracker.put(toAddr, data);
        }
        if(isFirstInfection){
            String labelLocation = "Other";
            for (NodeState state : states) {
                if (isInside(state.getPolygon(), to.getLocation())) {
                    labelLocation = state.getStateName();
                    break;
                }
            }
            write(getSimTime() + ", " + from.getAddress() + ","+ toAddr + ","  + labelLocation);
        }
    }

    @Override
    public void done() {
        //write summary
        write("-------");
        //HashMap.SimpleEntry<Integer, InfectionData>
        for(Map.Entry<Integer, InfectionData> entry:infectionTracker.entrySet()){
            InfectionData data = entry.getValue();
            write(entry.getKey()  + "," + data.isInfected + "," + data.receivedParticles);
        }

        super.done();
    }

    public static class InfectionData {

        private boolean isInfected;
        private int receivedParticles;
        private final double probInfection;
        private final Random r;

        public InfectionData(Random r, double probInfection){
            isInfected = false;
            receivedParticles = 0;
            this.r = r;
            this.probInfection = probInfection;
            if(probInfection < 0 || probInfection > 1) throw new IllegalArgumentException("Probability must be between 0 and 1");
        }

        /**
         *
         * @param amount    size of message, indicates virus load in message (higher from unvaccinated or non-mask sender, lower from vaccinated person)
         * @return did an infection occur?
         */
        public boolean addParticles(int amount){
            if(isInfected) return false;
            if(amount < 0) throw new IllegalArgumentException("Virus load can't be negative");
            int currentLevel = receivedParticles / 1000;
            receivedParticles += amount;
            int amountRolls = (receivedParticles/1000) - currentLevel;
            for(int i = 0; i < amountRolls; i++){
                if (r.nextDouble() < probInfection){
                    isInfected = true;
                    return true;
                }
            }
            return false;
        }
    }


    //--------------- methods from ProhibitedPolygonRwp, methods needed to map point to a certain area on the map -------------

    private static boolean isInside(
            final List <Coord> polygon,
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
