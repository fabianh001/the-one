package routing;

import core.Message;
import core.Settings;

public class CustomEpidemicRouter extends ActiveRouter {
    /**
     * Constructor. Creates a new message router based on the settings in
     * the given Settings object.
     * @param s The settings object
     */
    public CustomEpidemicRouter(Settings s) {
        super(s);
        //TODO: read&use epidemic router specific settings (if any)
    }

    /**
     * Copy constructor.
     * @param r The router prototype where setting values are copied from
     */
    protected CustomEpidemicRouter(CustomEpidemicRouter r) {
        super(r);
        //TODO: copy epidemic settings here (if any)
    }

    /**
     * Drops messages whose TTL is less than zero or when the message sender does not match with the host.
     */
    @Override
    protected void dropExpiredMessages() {
        Message[] messages = getMessageCollection().toArray(new Message[0]);
        for (int i=0; i<messages.length; i++) {
            int ttl = messages[i].getTtl();
            if (ttl <= 0 || messages[i].getFrom().getAddress() != super.getHost().getAddress()) {
                deleteMessage(messages[i].getId(), true);
            }
        }
    }

    @Override
    public void update() {
        super.update();
        if (isTransferring() || !canStartTransfer()) {
            return; // transferring, don't try other connections yet
        }

        // Try first the messages that can be delivered to final recipient
        if (exchangeDeliverableMessages() != null) {
            return; // started a transfer, don't try others (yet)
        }

        // then try any/all message to any/all connection
        this.tryAllMessagesToAllConnections();
        dropExpiredMessages(); // drop messages more aggresively
    }


    @Override
    public CustomEpidemicRouter replicate() {
        return new CustomEpidemicRouter(this);
    }
}
