package report;

import core.*;
import movement.StatefulRwp;
import movement.state.states.*;

import java.util.*;

class NodeInformation {
    HashMap<String, Double> locations = new HashMap<>();
    double lastTimeStep = 0;
    String currentNodeState = new QueueState().getStateName();

    NodeInformation(double lastTimeStep) {
        this.lastTimeStep = lastTimeStep;
    }
}

public class UnityMovementReport extends Report implements MovementListener {

    private HashMap<Integer, NodeInformation> nodeMap = new HashMap<>();
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
        NodeInformation currentNodeInfo = nodeMap.get(host.getAddress());
        HashMap<String, Double> locationHashMap = currentNodeInfo.locations;

        Double currentTime = getSimTime();
        Double time = currentTime - currentNodeInfo.lastTimeStep;
        Double count = locationHashMap.get(currentNodeInfo.currentNodeState);
        if (count == null) {
            locationHashMap.put(currentNodeInfo.currentNodeState, time);
        } else {
            locationHashMap.put(currentNodeInfo.currentNodeState, count + time);
        }

        try {
            StatefulRwp movModel = (StatefulRwp) host.movement;
            String labelLocation = movModel.lastState.getStateName();
            currentNodeInfo.lastTimeStep = currentTime;
            currentNodeInfo.currentNodeState = labelLocation;
            return;
        } catch (Exception e) {
            // Do nothing
        }

        currentNodeInfo.lastTimeStep = currentTime;
        currentNodeInfo.currentNodeState = "Other";
    }

    @Override
    public void initialLocation(DTNHost host, Coord location) {
        nodeMap.put(host.getAddress(), new NodeInformation(getSimTime()));
    }

    @Override
    public void done() {
        // write all labels into first row
        Optional<String> optRow = Arrays.stream(states).map(NodeState::getStateName).reduce((a, b)->a + "\",\"" + b);
        if(optRow.isPresent()){
            String firstRow = "\"Node Address\",\"" + optRow.get() + "\"," + "Other" + "," + "\"Total Time\"";
            write(firstRow);
        }

        //create line of all times
        for (Map.Entry<Integer, NodeInformation> entry : nodeMap.entrySet()) {
            StringBuilder locations = new StringBuilder();
            int total = 0;
            for (NodeState state : states) {
                Double value = entry.getValue().locations.get(state.getStateName());
                if (value == null) {
                    value = 0.0;
                }
                total += value;
                locations.append(",");
                locations.append(value);
            }
            //Add "Other" time
            locations.append(",");
            Double otherValue = entry.getValue().locations.get("Other");
            if (otherValue == null) {
                otherValue = 0.0;
            }
            total += otherValue;
            locations.append(otherValue);
            //Add total time
            locations.append(",");
            locations.append(total);
            write("\"Node " + entry.getKey() + "\"" + locations.toString());
        }

        super.done();
    }
}
