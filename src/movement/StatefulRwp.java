package movement;

import core.Coord;
import core.Settings;
import core.SimClock;
import core.SimScenario;
import movement.state.states.*;

/**
 * Example of a state-machine driven node mobility. Each node has two states
 * LEFT and RIGHT that influence the picking of the next waypoint. Nodes
 * transition between the states with some probability defined by the state
 * transition diagram.
 *
 * @author teemuk
 */
public class StatefulRwp
extends MovementModel {

  //==========================================================================//
  // Instance vars
  //==========================================================================//
  private Coord lastWaypoint;

  private NodeState state;
  //==========================================================================//


  //==========================================================================//
  // Implementation
  //==========================================================================//
  @Override
  public Path getPath() {
    // Update state machine every time we pick a path
    this.state = this.updateState( this.state );

    // Create the path
    final Path p;
    p = new Path( generateSpeed() );
    p.addWaypoint( lastWaypoint.clone() );

    final Coord c = this.randomCoord();
    p.addWaypoint( c );

    this.lastWaypoint = c;
    return p;
  }

  @Override
  public Coord getInitialLocation() {
    this.lastWaypoint = this.randomCoord();
    return this.lastWaypoint;
  }

  @Override
  public MovementModel replicate() {
    return new StatefulRwp( this );
  }

  private Coord randomCoord() {
    final double x = 0.43;
    // TODO implement a polygon for each state
    return new Coord( x, rng.nextDouble() * super.getMaxY());
  }
  //==========================================================================//


  //==========================================================================//
  // Construction
  //==========================================================================//
  public StatefulRwp( final Settings settings ) {
    super( settings );
    this.state = new QueueState();
  }

  public StatefulRwp( final StatefulRwp other ) {
    super( other );

    // Pick a random state every time we replicate rather than copying!
    // Otherwise every node would start in the same state.
    this.state = new QueueState();
  }
  //==========================================================================//

  /**
   * This method defines the transitions in the state machine.
   *
   * @param state
   *  the current state
   * @return
   *  the next state dependent on the time passed by
   */
  private NodeState updateState(NodeState state) {
    final double curTime = SimClock.getTime();
    final double endTime = SimScenario.getInstance().getEndTime();
    final double random = Math.random();

    if (state == null) {
      return null;
    }

    //20:30 - 21:00
    if (curTime < 1800) {
      return state;
    }

    //21:00 - 22:00 Beer Happy Hour
    if (curTime < 5400 && random < 0.15) {
      NodeState newState = new BeerBarState();
      System.out.println(newState.getStateName());
      return newState;
    }

    //21:00 - 22:00 & 3:30 - 4:00 People get more snacks
    if ((curTime < 5400 || curTime > 25200) && random < 0.1) {
      NodeState newState = new PizzaBarState();
      System.out.println(newState.getStateName());
      return newState;
    }

    //01:00 - 01:30 last regular u-bahn so more people are leaving
    if (curTime > 16200 && curTime < 18000 && random < 0.15) {
      NodeState newState = new WardrobeBeforeLeavingState();
      System.out.println(newState.getStateName());
      return newState;
    }

    //4:15 party closes (at 4:30) so people leave with very high probability
    if (curTime > 27900 && random < 0.9) {
      NodeState newState = new WardrobeBeforeLeavingState();
      System.out.println(newState.getStateName());
      return newState;
    }

    return state.getNextState();
  }
  //==========================================================================//
}
