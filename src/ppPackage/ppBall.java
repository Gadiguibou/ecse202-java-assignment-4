/** */
package ppPackage;

import static ppPackage.ppSimParams.*;

import java.awt.Color;

import acm.graphics.GOval;

/**
 * @author gabriel
 *     <p>This class encapsulates all the relevant information and methods related to the ping-pong
 *     ball. It contains the physics logic needed for ball simulation.
 */
public class ppBall extends Thread {
  // Instance variables
  // Initial position of ball - X
  private double Xinit;
  // Initial position of ball - Y
  private double Yinit;
  // Initial velocity (Magnitude)
  private double Vo;
  // Initial direction
  private double theta;
  // Energy loss on collision
  private double loss;
  // Color of ball
  @SuppressWarnings("unused")
  private Color color;
  // Instance of ping-pong table
  private ppTable table;
  // Reference to player ping-pong paddle
  private ppPaddle myPaddle;
  // Reference to agent ping-pong paddle
  private ppPaddleAgent theAgent;
  // Graphics object representing ball
  GOval myBall;
  // Whether the ball should print a trace point at every tick
  private boolean traceOn;
  // Whether the ball is moving or not, i.e. whether it has energy and is still in play
  private boolean isMoving;

  // Position and speed instance variables
  // X position and velocity variables
  private double x0;
  private double x;
  private double vx;

  // Y position and velocity variables
  private double y0;
  private double y;
  private double vy;

  /**
   * The constructor for the ppBall class copies parameters to instance variables, creates an
   * instance of a GOval to represent the ping-pong ball, and adds it to the display.
   *
   * @param Xinit Specifies the starting position of the ball X (meters).
   * @param Yinit Specifies the starting position of the ball Y (meters).
   * @param Vo Specifies the initial velocity (meters/second).
   * @param theta Specifies the initial angle to the horizontal (degrees).
   * @param color Specifies the ball color (Color).
   * @param loss Specifies the loss on collision ([0,1]).
   * @param table Specifies a reference to the ppTable class used to manage the display.
   * @param traceOn Specifies whether the ball should print a trace point at every tick.
   */
  public ppBall(
      double Xinit,
      double Yinit,
      double Vo,
      double theta,
      Color color,
      double loss,
      ppTable table,
      boolean traceOn) {
    // Copy constructor parameters to instance variables
    this.Xinit = Xinit;
    this.Yinit = Yinit;
    this.Vo = Vo;
    this.theta = theta;
    this.color = color;
    this.loss = loss;
    this.table = table;
    this.traceOn = traceOn;
    this.isMoving = true;

    this.myBall =
        new GOval(
            table.toScrX(Xinit),
            table.toScrY(Yinit),
            table.toScrX(bSize * 2),
            table.toScrX(bSize * 2));
    myBall.setFilled(true);
    myBall.setColor(color);
    table.getDisplay().add(myBall);
  }

  /**
   * Sets the value of the reference to the player's paddle.
   *
   * @param myPaddle Specifies the reference to the player's ppPaddle instance.
   */
  public void setPaddle(ppPaddle myPaddle) {
    this.myPaddle = myPaddle;
  }

  /**
   * Sets the value of the reference to the agent's paddle.
   *
   * @param theAgent Specifies the reference to the agent's ppPaddleAgent instance.
   */
  public void setAgent(ppPaddleAgent theAgent) {
    this.theAgent = theAgent;
  }

  /**
   * Predicate that returns true if the ppBall simulation is still running (i.e. whether the ball
   * has energy and is still in play).
   *
   * @return Returns whether or not the simulation is running (i.e. whether the ball is moving).
   */
  public boolean ballInPlay() {
    return isMoving;
  }

  /**
   * Getter for the Y position of the ball in simulation coordinates. Used to track the ball with
   * the agent.
   *
   * @return Returns the current absolute Y coordinate of the ball.
   */
  public double getY() {
    return y0 + y;
  }

  /**
   * Getter for the X position of the ball in simulation coordinates. Used to track the ball with
   * the agent.
   *
   * @return Returns the current absolute X coordinate of the ball.
   */
  public double getX() {
    return x0 + x;
  }

  /**
   * Getter for the Y velocity of the ball in simulation coordinates. Used to track the ball with
   * the agent.
   *
   * @return Returns the current Y velocity of the ball.
   */
  public double getVy() {
    return vy;
  }

  /**
   * Getter for the X velocity of the ball in simulation coordinates. Used to track the ball with
   * the agent.
   *
   * @return Returns the current X velocity of the ball.
   */
  public double getVx() {
    return vx;
  }

  /**
   * In a thread, the run method is NOT started automatically (like in Assignment 1). Instead, a
   * start message must be sent to each instance of the ppBall class, e.g.,
   *
   * <p><code>ppBall myBall = new ppBall (--parameters--);</code>
   *
   * <p><code>myBall.start();</code> The body of the run method is essentially the simulator code
   * you wrote for A1.
   */
  public void run() {

    // Initialize simulation parameters
    // Initializing variables
    // Time (reset after each bounce)
    double time = 0;

    // Terminal velocity
    final double Vt = bMass * g / (4 * Math.PI * bSize * bSize * k);

    // Kinetic energy in X and Y directions
    double kx = ETHR;
    double ky = ETHR;

    // Initial velocity components in X and Y
    double v0x = Vo * Math.cos(theta * Pi / 180);
    double v0y = Vo * Math.sin(theta * Pi / 180);

    // Initial X and Y position of the ball
    x0 = Xinit;
    y0 = Yinit;

    // Main simulation loop
    while (isMoving) {
      // Update parameters according to formulas
      x = v0x * Vt / g * (1 - Math.exp(-g * time / Vt));
      y = Vt / g * (v0y + Vt) * (1 - Math.exp(-g * time / Vt)) - Vt * time;
      vx = v0x * Math.exp(-g * time / Vt);
      vy = (v0y + Vt) * Math.exp(-g * time / Vt) - Vt;

      // Check for collision with the ground
      if ((vy < 0) && (y0 + y <= bSize)) {
        // Compute new ball energy
        kx = 0.5 * bMass * vx * vx * (1 - loss);
        ky = 0.5 * bMass * vy * vy * (1 - loss);
        final double PE = 0;

        v0x = Math.sqrt(2 * kx / bMass);
        v0y = Math.sqrt(2 * ky / bMass);

        // Making sure the ball maintains the same horizontal direction
        if (vx < 0) {
          v0x = -v0x;
        }

        // time is reset at every collision
        time = 0;
        // need to accumulate distance between collisions
        x0 += x;
        // the absolute position of the ball on the ground
        y0 = bSize;
        // (X,Y) is the instantaneous position along an arc,
        x = 0;
        // Absolute position is (Xo+X,Yo+Y).
        y = 0;

        if ((kx + ky + PE) < ETHR) {
          isMoving = false;
        }
      }

      // Check for out of bound shot on player's side
      if (vx > 0 && x0 + x > myPaddle.getX() - bSize - ppPaddleW / 2 && y0 + y > YMAX) {
        myPaddle.stopPlay();
        theAgent.stopPlay();

        table.getDisplay().updateScores(0, 1);
        isMoving = false;
      } else if (vx > 0
          && x0 + x > myPaddle.getX() - bSize - ppPaddleW / 2
          && myPaddle.contact(x0 + x + bSize, y0 + y)) {
        // Check for collision with the player's paddle
        // Compute new ball energy
        kx = 0.5 * bMass * vx * vx * (1 - loss);
        ky = 0.5 * bMass * vy * vy * (1 - loss);
        final double PE = bMass * g * y;

        // Account for loss of energy
        v0x = Math.sqrt(2 * kx / bMass);
        v0y = Math.sqrt(2 * ky / bMass);

        // Scale X component of velocity
        v0x = -Math.min(v0x * ppPaddleXgain, VMAX);
        // Scale Y component of velocity and use same direction as paddle
        v0y =
            myPaddle.getSgnVy()
                * Math.min(v0y * ppPaddleYgain * myPaddle.getVy() / myPaddle.getSgnVy(), VMAX);

        // time is reset at every collision
        time = 0;
        // the absolute position of the ball on the paddle
        x0 = myPaddle.getX() - ppPaddleW / 2 - bSize;
        // need to accumulate distance between collisions
        y0 += y;
        // (X,Y) is the instantaneous position along an arc,
        x = 0;
        // Absolute position is (Xo+X,Yo+Y).
        y = 0;

        if ((kx + ky + PE) < ETHR) {
          isMoving = false;
        }
      } else if ((vx > 0)
          && (x0 + x - bSize > myPaddle.getX() + ppPaddleW / 2)
          && !myPaddle.contact(x0 + x + bSize, y0 + y)) {
        // If the ball is not stopped by the paddle, stop the simulation and
        // prevent both paddles from moving.
        myPaddle.stopPlay();
        theAgent.stopPlay();

        table.getDisplay().updateScores(1, 0);
        isMoving = false;
      }

      // Check for out of bounds shot on agent's side
      if (vx < 0 && x0 + x < theAgent.getX() + bSize + ppPaddleW / 2 && y0 + y > YMAX) {
        myPaddle.stopPlay();
        theAgent.stopPlay();

        table.getDisplay().updateScores(1, 0);
        isMoving = false;
      } else if (vx < 0
          && x0 + x < theAgent.getX() + bSize + ppPaddleW / 2
          && theAgent.contact(x0 + x - bSize, y0 + y)) {
        // Check for collision with the agent's paddle
        // Compute new ball energy
        kx = 0.5 * bMass * vx * vx * (1 - loss);
        ky = 0.5 * bMass * vy * vy * (1 - loss);
        final double PE = bMass * g * y;

        // Account for loss of energy
        v0x = Math.sqrt(2 * kx / bMass);
        v0y = Math.sqrt(2 * ky / bMass);

        // Scale X component of velocity
        v0x = Math.min(v0x * ppPaddleXgain, VMAX);
        // Scale Y component of velocity and use same direction as paddle
        v0y =
            myPaddle.getSgnVy()
                * Math.min(v0y * ppPaddleYgain * myPaddle.getVy() / myPaddle.getSgnVy(), VMAX);
        // time is reset at every collision
        time = 0;
        // the absolute position of the ball on the paddle
        x0 = theAgent.getX() + ppPaddleW / 2 + bSize;
        // need to accumulate distance between collisions
        y0 += y;
        // (X,Y) is the instantaneous position along an arc,
        x = 0;
        // Absolute position is (Xo+X,Yo+Y).
        y = 0;

        if ((kx + ky + PE) < ETHR) {
          isMoving = false;
        }
      } else if ((vx < 0)
          && (x0 + x + bSize < theAgent.getX() - ppPaddleW / 2)
          && !theAgent.contact(x0 + x - bSize, y0 + y)) {
        // If the ball is not stopped by the paddle, stop the simulation and
        // prevent both paddles from moving.
        myPaddle.stopPlay();
        theAgent.stopPlay();

        isMoving = false;
        table.getDisplay().updateScores(0, 1);
      }

      // Increment time
      time += TICK;

      // Update and display
      // Note: bSize is subtracted from X and added to Y so
      // that ball is positioned at its center.
      final int ScrX = (int) table.toScrX(x0 + x - bSize);
      final int ScrY = (int) table.toScrY(y0 + y + bSize);

      myBall.setLocation(ScrX, ScrY);

      // Print a black dot at the ball's location if trace point are on.
      if (traceOn) {
        table.getDisplay().add(new GOval(table.toScrX(x0 + x), table.toScrY(y0 + y), PD, PD));
      }

      if (DEBUG) {
        System.out.printf(
            "t: %.2f X: %.2f Y: %.2f Vx: %.2f Vy:%.2f\n", time, x0 + x, y0 + y, vx, vy);
      }

      // Pause display between each tick
      table.getDisplay().pause(TICK * TIMESCALE);
    }
  }
}
