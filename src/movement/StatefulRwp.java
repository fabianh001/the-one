package movement;

import core.Coord;
import core.Settings;
import core.SimClock;
import core.SimScenario;
import movement.state.states.*;

import java.util.List;

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

    Coord c;
    do {
      c = this.randomCoord();
    } while ( pathIntersects( this.state.getPolygon(), this.lastWaypoint, c ) );
    p.addWaypoint( c );

    this.lastWaypoint = c;
    return p;
  }

  @Override
  public Coord getInitialLocation() {
    do {
      this.lastWaypoint = this.randomCoord();
    } while (!isInside( this.state.getPolygon(), this.lastWaypoint ));
    return this.lastWaypoint;
  }

  @Override
  public MovementModel replicate() {
    return new StatefulRwp( this );
  }

  private Coord randomCoord() {
    return new Coord(
            rng.nextDouble() * super.getMaxX(),
            rng.nextDouble() * super.getMaxY() );
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

  //==========================================================================//
  // Private - geometry
  //==========================================================================//
  private static boolean isInside(
          final List <Coord> polygon,
          final Coord point ) {
    final int count = countIntersectedEdges( polygon, point,
            new Coord( -10,0 ) );
    return ( ( count % 2 ) != 0 );
  }

  private static boolean pathIntersects(
          final List<Coord> polygon,
          final Coord start,
          final Coord end ) {
    final int count = countIntersectedEdges( polygon, start, end );
    return ( count > 0 );
  }

  private static int countIntersectedEdges(
          final List <Coord> polygon,
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
    if ( dotProduct > squaredLength ) return false;

    return true;
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

  //==========================================================================//

  //==========================================================================//
  // Private - State machine
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
