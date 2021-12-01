package input;

import core.DTNHost;
import core.Message;
import core.World;

public class MessageBroadcastCreateEvent extends ExternalEvent {
    /** address of the node the message is from */
    protected int fromAddr;
    /** identifier of the message */
    protected String id;

    private int size;
    private int responseSize;
    private int[] receivers;
    /**
     * Creates a message creation event with a optional response request
     * @param from The creator of the message
     * @param receivers Where the message is destined to
     * @param id ID of the message
     * @param size Size of the message
     * @param responseSize Size of the requested response message or 0 if
     * no response is requested
     * @param time Time, when the message is created
     */
    public MessageBroadcastCreateEvent(int from, int[] receivers, String id, int size,
                                       int responseSize, double time) {
        super(time);
        this.fromAddr = from;
        this.id = id;
        this.receivers = receivers;
        this.size = size;
        this.responseSize = responseSize;
    }

    /**
     * Creates the message this event represents.
     */
    @Override
    public void processEvent(World world) {
        for (int i = this.receivers[0]; i <= this.receivers[1]; i++) {
            DTNHost to = world.getNodeByAddress(i);
            DTNHost from = world.getNodeByAddress(this.fromAddr);
            // TODO Configure range for message creation
            if(to.getLocation().distance(from.getLocation()) < 10){
                Message m = new Message(from, to, this.id, this.size);
                m.setResponseSize(this.responseSize);
                from.createNewMessage(m);
            }
        }

    }

    @Override
    public String toString() {
        return super.toString() + " [" + fromAddr + "->" + receivers.length + " hosts] " +
                "size:" + size + " CREATE";
    }
}
