package interfaces;

/*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */

import java.util.Collection;

import core.CBRConnection;
import core.Connection;
import core.NetworkInterface;
import core.Settings;

/**
 * A simple Network Interface that provides a constant bit-rate service, where
 * one transmission can be on at a time.
 * Change compared to SimpleBroadcastInterface: Only host interfaces with different infection status (non-infected or infected) will connect to each other.
 * Infection status is indicated with the first character of the group id (N(ot infected) or I(nfected))
 */
public class InfectionSimpleBroadcastInterface extends NetworkInterface {

    /**
     * Reads the interface settings from the Settings file
     */
    public InfectionSimpleBroadcastInterface(Settings s)	{
        super(s);
    }

    /**
     * Copy constructor
     * @param ni the copied network interface object
     */
    public InfectionSimpleBroadcastInterface(InfectionSimpleBroadcastInterface ni) {
        super(ni);
    }

    public NetworkInterface replicate()	{
        return new InfectionSimpleBroadcastInterface(this);
    }

    /**
     * Tries to connect this host to another host. The other host must be
     * active and within range of this host for the connection to succeed.
     * @param anotherInterface The interface to connect to
     */
    public void connect(NetworkInterface anotherInterface) {
        //System.out.println(getHost().toString().charAt(0) + "," + anotherInterface.getHost().toString().charAt(0));
        if(getHost().toString().charAt(0) == anotherInterface.getHost().toString().charAt(0)){
            // first character is the same ==> same infection status and don't connect
            return;
        }
        if (isScanning()
                && anotherInterface.getHost().isRadioActive()
                && isWithinRange(anotherInterface)
                && !isConnected(anotherInterface)
                && (this != anotherInterface)) {
            // new contact within range
            // connection speed is the lower one of the two speeds
            int conSpeed = anotherInterface.getTransmitSpeed(this);
            if (conSpeed > this.transmitSpeed) {
                conSpeed = this.transmitSpeed;
            }

            Connection con = new CBRConnection(this.host, this,
                    anotherInterface.getHost(), anotherInterface, conSpeed);
            connect(con,anotherInterface);
        }
    }

    /**
     * Updates the state of current connections (i.e. tears down connections
     * that are out of range and creates new ones).
     */
    public void update() {
        if (optimizer == null) {
            return; /* nothing to do */
        }

        // First break the old ones
        optimizer.updateLocation(this);
        for (int i=0; i<this.connections.size(); ) {
            Connection con = this.connections.get(i);
            NetworkInterface anotherInterface = con.getOtherInterface(this);

            // all connections should be up at this stage
            assert con.isUp() : "Connection " + con + " was down!";

            if (!isWithinRange(anotherInterface)) {
                disconnect(con,anotherInterface);
                connections.remove(i);
            }
            else {
                i++;
            }
        }
        // Then find new possible connections
        Collection<NetworkInterface> interfaces =
                optimizer.getNearInterfaces(this);
        for (NetworkInterface i : interfaces) {
            connect(i);
        }
    }

    /**
     * Creates a connection to another host. This method does not do any checks
     * on whether the other node is in range or active
     * @param anotherInterface The interface to create the connection to
     */
    public void createConnection(NetworkInterface anotherInterface) {
        if(getHost().toString().charAt(0) == anotherInterface.getHost().toString().charAt(0)){
            // first character is the same ==> same infection status and don't connect
            //System.out.println(getHost().toString().charAt(0) + "," + anotherInterface.getHost().toString().charAt(0));
            return;
        }
        if (!isConnected(anotherInterface) && (this != anotherInterface)) {
            // connection speed is the lower one of the two speeds
            int conSpeed = anotherInterface.getTransmitSpeed(this);
            if (conSpeed > this.transmitSpeed) {
                conSpeed = this.transmitSpeed;
            }

            Connection con = new CBRConnection(this.host, this,
                    anotherInterface.getHost(), anotherInterface, conSpeed);
            connect(con,anotherInterface);
        }
    }

    /**
     * Returns a string representation of the object.
     * @return a string representation of the object.
     */
    public String toString() {
        return "InfectionSimpleBroadcastInterface " + super.toString();
    }

}
