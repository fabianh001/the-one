package report;

import core.DTNHost;
import core.UpdateListener;
import movement.UnityGuestMovementModel;
import movement.state.states.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Class that maps all hosts in the simulation to a given area in the Unity Simulation
 */
public class HostLocationReport
    extends Report
    implements UpdateListener {


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

    private HashMap<String, Integer> stateIndex = new HashMap<>();

    public HostLocationReport() {
        init();
    }

    @Override
    public void init() {
        super.init();
        // write all labels into first row
        Optional<String> optRow = Arrays.stream(states).map(NodeState::getStateName).reduce((a, b) -> a + "," + b);
        if (optRow.isPresent()) {
            String firstRow = "Time step," + optRow.get() + "," + "Other";
            write(firstRow);
        }

        for (int i = 0; i < states.length; i++) {
            stateIndex.put(states[i].getStateName(), i);
        }
    }

    @Override
    public void updated(List<DTNHost> hosts) {
        // at each time step, record amount of nodes at each area
        int[] amount = new int[states.length + 1];
        for (DTNHost host : hosts) {

            try {
                UnityGuestMovementModel movModel = (UnityGuestMovementModel) host.movement;
                String labelLocation = movModel.lastState.getStateName();
                amount[stateIndex.get(labelLocation)]++;
                continue;
            } catch (Exception e) {
                // Do nothing
            }

            //Other
            amount[amount.length - 1]++;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(getSimTime());
        for (int j : amount) {
            builder.append(",");
            builder.append(j);
        }
        write(builder.toString());
    }

    @Override
    public void done() {
        super.done();
    }
}
