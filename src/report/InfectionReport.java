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
    public double[] factorLocation, factorSender, factorReceiver;
    // constants

    public static final String FACTOR_DISTANCE_S = "factor_distance";
    public static final String DISTANCES_S = "distances";
    public static final String INFECTION_PROB_S = "infection_rate";
    public static final String FACTOR_LOCATION_S = "factor_location";
    public static final String FACTOR_SENDER_S = "factor_sender";
    public static final String FACTOR_RECEIVER_S = "factor_receiver";

    private static final int INDEX_INDOOR = 0;
    private static final int INDEX_MAIN_STAGE = 1;
    private static final int INDEX_SHISHA_BAR = 2;
    private static final int INDEX_OUTDOOR = 3;
    private static final int INDEX_UNVACCINATED = 0;
    private static final int INDEX_VACCINATED = 1;
    private static final int INDEX_BOOSTER = 2;
    // constants
    private double prob;
    private static final boolean showDebugOutput = false;

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

    private static final NodeState[] statesOutside = new NodeState[]{
            new QueueState(),
            new EntryState(),
            new OutdoorAreaState(),
            new PizzaBarState(),
            new ExitState(),
    };

    private static final MainStageState stateMainStage = new MainStageState();
    private static final ShishaBarState stateShishaBar = new ShishaBarState();
    private static final NodeState[] statesIndoor = new NodeState[] {
            new WardrobeState(),
            new MetalBunkerState(),
            new TechnoBunkerState(),
            new SideWCState(),
            new MainWCState(),
            new WardrobeBeforeLeavingState(),
            new CocktailBarState(),
            new BeerBarState(),
            new ShotBarState(),
    };

    public InfectionReport(){
        init();
    }

    @Override
    protected void init() {
        super.init();
        random = new Random();
        infectionTracker = new HashMap<>();
        double[] factorDistancesDefault = new double[] {1.0, 1.0, 1.0, 0.2, 0.001};
        double[] distancesDefault = new double[]{0.0, 0.5, 1.0, 1.5, 3.0};
        if(getSettings().contains(FACTOR_DISTANCE_S)) {
            factorDistances = getSettings().getCsvDoubles(FACTOR_DISTANCE_S);
            if(showDebugOutput) System.out.println("Applied config settings for factorDistances");
        }else{
            factorDistances = factorDistancesDefault;
        }
        if(getSettings().contains(DISTANCES_S)){
            distances = getSettings().getCsvDoubles(DISTANCES_S);
            if(showDebugOutput) System.out.println("Applied config settings for distances");
        }else{
            distances = distancesDefault;
        }
        if(getSettings().contains(INFECTION_PROB_S) && getSettings().getDouble(INFECTION_PROB_S) >= 0 && getSettings().getDouble(INFECTION_PROB_S) <= 1){
            prob = getSettings().getDouble(INFECTION_PROB_S);
            if (showDebugOutput) System.out.println("Applied config settings for infection rate");
        }else{
            prob = 0.005;
        }
        // factor location: Indoor, main stage, shisha bar, outside
        if(getSettings().contains(FACTOR_LOCATION_S)){
            factorLocation = getSettings().getCsvDoubles(FACTOR_LOCATION_S, 4);
            if(showDebugOutput) System.out.println("Applied config settings for factor location");
        }else{
            factorLocation = new double[]{1.0, 5.0, 10.0, 0.5};
        }
        // factor sender: unvaccinated, vaccinated, booster
        if(getSettings().contains(FACTOR_SENDER_S)){
            factorSender = getSettings().getCsvDoubles(FACTOR_SENDER_S, 3);
            if(showDebugOutput) System.out.println("Applied config settings for factor sender");
        }else{
            factorSender = new double[]{1.0, 0.8, 0.7};
        }
        // factor receiver: unvaccinated, vaccinated, booster
        if(getSettings().contains(FACTOR_RECEIVER_S)){
            factorReceiver = getSettings().getCsvDoubles(FACTOR_RECEIVER_S, 3);
            if(showDebugOutput) System.out.println("Applied config settings for factor receiver");
        }else{
            factorReceiver = new double[]{1.0, 0.9, 0.8};
        }

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
        // infected group starts with group ID "I", infected groups can't get infected
        if(to.toString().startsWith("I")) return;
        // if receiver is not active in the simulation
        if(!to.isMovementActive()) return;
        //if(m.getFrom().getAddress() == to.getAddress()) return;  // message travelled in a loop
        // people in the queue can't get infected
        if(isInside(new QueueState().getPolygon(), to.getLocation())) return;
        int toAddr = to.getAddress();


        boolean isFirstInfection;
        // find correct factor
        double curDist = from.getLocation().distance(to.getLocation());
        double factorDist = factorDistances[0];
        if(curDist >= distances[distances.length -1]){
            // distance greater than maximum transmission range
            return;
        }
        // Apply distance modifier
        for(int i = 1; i < factorDistances.length; i++){
            if(curDist <= distances[i]){
                // interpolate distance factor linearly
                double percentage = (curDist - distances[i-1])/(distances[i] - distances[i-1]);
                factorDist = factorDistances[i-1] + percentage * (factorDistances[i] - factorDistances[i-1]);
                break;
            }
        }
        // Apply sender side modifier
        // apply factor depending on sender vaccination status and location
        double factorSenderLocation = factorLocation[INDEX_INDOOR];
        if(isInside(stateMainStage.getPolygon(), from.getLocation())){
            factorSenderLocation = factorLocation[INDEX_MAIN_STAGE]; // check main stage
        }else if(isInside(stateShishaBar.getPolygon(), from.getLocation())){
            factorSenderLocation = factorLocation[INDEX_SHISHA_BAR]; // check shisha bar
        }else {
            for(NodeState node : statesOutside){
                if(isInside(node.getPolygon(), from.getLocation())){
                    factorSenderLocation = factorLocation[INDEX_OUTDOOR];
                    break;
                }
            }
        }
        double facSender = factorSender[INDEX_UNVACCINATED]; // unvaccinated
        if(from.toString().charAt(1) == 'V'){
            facSender = factorSender[INDEX_VACCINATED];
        }else if(from.toString().charAt(1) == 'B'){
            facSender = factorSender[INDEX_BOOSTER];
        }

        // Apply receiver vaccination modifier
        double facReceiver = factorReceiver[INDEX_UNVACCINATED];
        if(to.toString().charAt(1) == 'V'){
            facReceiver = factorReceiver[INDEX_VACCINATED];
        }else if(to.toString().charAt(1) == 'B'){
            facReceiver = factorReceiver[INDEX_BOOSTER];
        }

        int particles = (int) (factorSenderLocation * facSender * facReceiver * factorDist * m.getSize());
        // if(particles > 5000) System.out.println(particles);



        //write(factorDist + "," + curDist);

        if(infectionTracker.containsKey(toAddr)){
            isFirstInfection = infectionTracker.get(toAddr).addParticles(particles);
        }else{

            /*
            One person sends 1000 particles per second to another person (scenario, default range with factor 1.0 and unvaccinated)
            Source:
            Lelieveld, J.; Helleis, F.; Borrmann, S.; Cheng, Y.; Drewnick, F.; Haug, G.; Klimach, T.; Sciare, J.; Su, H.; Pöschl, U.
            Model Calculations of Aerosol Transmission and Infection Risk of COVID-19 in Indoor Environments. Int. J. Environ. Res. Public Health 2020, 17, 8114. https://doi.org/10.3390/ijerph17218114

            3.7. Infective Dose D50: Probability for infection for one copy is about 0.0022.
            Fitting to this simulation: After receiving 1000 particles, we simulate one infection event with probability prob (initial test: 0.0022) for an infection.
             */
             // use more infectious variant, more dangerous setting: party, old value: 0.0022;
            InfectionData data = new InfectionData(random, prob);
            isFirstInfection = data.addParticles(particles);
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
            write(getSimTime() + ", " + from.getAddress() + ","+ toAddr + ","  + labelLocation + "," + to.isMovementActive());
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
