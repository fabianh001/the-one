package report;

import core.DTNHost;
import core.Message;
import core.MessageListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class InfectionReport
        extends Report
        implements MessageListener {

    private HashMap<Integer, InfectionData> infectionTracker;
    private Random random;

    public InfectionReport(){
       init();
    }

    @Override
    protected void init() {
        super.init();
        random = new Random();
        infectionTracker = new HashMap<Integer, InfectionData>();
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
        //if(m.getFrom().getAddress() == to.getAddress()) return;  // message travelled in a loop
        int toAddr = to.getAddress();
        boolean isFirstInfection = false;
        if(infectionTracker.containsKey(toAddr)){
            isFirstInfection = infectionTracker.get(toAddr).addParticles(m.getSize());
        }else{
            // 1/(30 * 15)
            InfectionData data = new InfectionData(random, 1.0/(15 * 30));
            isFirstInfection = data.addParticles(m.getSize());
            infectionTracker.put(toAddr, data);
        }
        if(isFirstInfection){
            write(getSimTime() + ", " + from.getAddress() + " --> "+ toAddr);
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

    public class InfectionData {

        private boolean isInfected;
        private int receivedParticles;
        private double probInfection;
        private Random r;

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
                }else{
                    continue;
                }
            }
            return false;
        }
    }

}
