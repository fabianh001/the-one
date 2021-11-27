package movement.state;

import core.Coord;
import core.Settings;
import movement.MovementModel;
import movement.Path;
import movement.state.states.NodeState;
import movement.state.states.QueueState;

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
   *  the next state
   */
  private NodeState updateState(NodeState state) {
    return state.getNextState();
  }
  //==========================================================================//
}
