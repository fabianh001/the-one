package report;

import core.DTNHost;
import core.Message;
import core.MessageListener;
import movement.UnityGuestMovementModel;
import movement.state.states.*;

import java.util.*;


/**
 *  This class handles the simulation of the disease spread. Only the direct transmission is modeled due to limited computation constraints and simplification of the simulation.
 *  Currently, the spread can be controlled via weights, that can increase or decrease the amount of viral load the recipient receive from an infected host.
 *  There are weights for the distance between hosts, the vaccination status of the infected and the vaccination status of the recipient.
 *  Always when a recipient reaches a fixed amount of viral load, there is a probability an infection may occur.
 *  We chose a gemoetric distribution in order to model the randomness of an infection, some people might be infected after a small dose of viral load and others may get away with longer contact times to an infected.
 *
 *  Sources for viral laod differences due to vaccination status:
 *
 *  "Since viral load is linked to transmission, single-dose mRNA SARS-CoV-2 vaccination may help control outbreaks."
 *  https://academic.oup.com/cid/article/73/6/e1365/6188727
 *
 *  "By analysing viral loads of over 16,000 infections during the current, Delta-variant-dominated pandemic wave in Israel, we found that BTIs in recently fully vaccinated individuals have lower viral loads than infections in unvaccinated individuals.
 *   However, this effect starts to decline 2 months after vaccination and ultimately vanishes 6 months or longer after vaccination.
 *   Notably, we found that the effect of BNT162b2 on reducing BTI viral loads is restored after a booster dose."
 *  https://www.nature.com/articles/s41591-021-01575-4
 *
 *
 *  Assumption for the recipient:
 *  A vaccination should have a positive effect on risk reduction on a infection.
 *  Therefore, one way to model this in our model is by decreasing the amount of viral load a vaccinated person receives and then fewer amount of infection events (e.g. after every x particles) will occur.
 *
 *  Sources for viral load differences dur to the distance between hosts:
 *
 *  Findings
 *  Our search identified 172 observational studies across 16 countries and six continents, with no randomised controlled trials and 44 relevant comparative studies in health-care and non-health-care settings (n=25 697 patients).
 *  Transmission of viruses was lower with physical distancing of 1 m or more, compared with a distance of less than 1 m (n=10 736, pooled adjusted odds ratio [aOR] 0·18, 95% CI 0·09 to 0·38; risk difference [RD] −10·2%, 95% CI −11·5 to −7·5;
 *  moderate certainty); protection was increased as distance was lengthened (change in relative risk [RR] 2·02 per m; pinteraction=0·041; moderate certainty).
 *  https://www.thelancet.com/journals/lancet/article/PIIS0140-6736(20)31142-9/fulltext
 *
 *  Source for difference in location:
 *  Following source compares different settings (e.g. classroom, office, choir practice) and the party setting with singing is very likely to be more infectious.
 *  https://www.mdpi.com/1660-4601/17/21/8114/htm
 *
 *  ==> Therefore, the main stage and other party areas are modeled as more infectious, the shisha bar has direct contact and is also classsified as a more risky area. The infection risk is reduced for the remaining outdoor areas.
 *
 *  Source for probability at an infection event:
 *  https://www.mdpi.com/1660-4601/17/21/8114/htm
 *
 *  ==> Baseline probability for infection of 0.0022 mentioned in the paper for one virus copy. We adjusted this value to our simulation and let a infection event occur after every 1000 particles received.
 *      Depending on the infection risk, values between 0.01 and 0.001 seems to produce believable results.
 */
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
    private static final int INDEX_HIGH_RISK_PARTY_AREA = 1;
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

    private static final NodeState[] statesHighRiskPartyAreas = new NodeState[] {
            new MainStageState(),
            new MetalBunkerState(),
            new TechnoBunkerState(),
    };

    private static final ShishaBarState stateShishaBar = new ShishaBarState();
    private static final NodeState[] statesIndoor = new NodeState[] {
            new WardrobeState(),
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

        String fromLabelLocation = "Other";
        String toLabelLocation = "Other";
        try {
            UnityGuestMovementModel movModel = (UnityGuestMovementModel) from.movement;
            fromLabelLocation = movModel.lastState.getStateName();
            movModel = (UnityGuestMovementModel) to.movement;
            toLabelLocation = movModel.lastState.getStateName();
        } catch (Exception e) {
            // Do nothing
        }

        if(m.getFrom().getAddress() != from.getAddress()) return; //only count messages that are from the first sender
        // infected group starts with group ID "I", infected groups can't get infected
        if(to.toString().startsWith("I")) return;
        // if receiver is not active in the simulation
        if(!to.isMovementActive()) return;
        //if(m.getFrom().getAddress() == to.getAddress()) return;  // message travelled in a loop
        // people in the queue can't get infected
        if (toLabelLocation == new QueueState().getStateName()) return;
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
        boolean isHighRiskArea = false;
        for(NodeState node: statesHighRiskPartyAreas) {
            if (node.getStateName() == fromLabelLocation) {
                isHighRiskArea = true;
                break;
            }
        }
        if(isHighRiskArea){
            factorSenderLocation = factorLocation[INDEX_HIGH_RISK_PARTY_AREA];
        }else if(stateShishaBar.getStateName() == fromLabelLocation){
            factorSenderLocation = factorLocation[INDEX_SHISHA_BAR]; // check shisha bar
        }else {
            for(NodeState node : statesOutside){
                if(node.getStateName() == fromLabelLocation){
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
                if (state.getStateName() == toLabelLocation) {
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
}
