/** */
package ppPackage;

import static ppPackage.ppSimParams.*;

import java.awt.Color;

/**
 * @author gabriel
 *     <p>Extends ppPaddle with a method for moving the paddle autonomously, tracking the attached
 *     ppBall.
 */
public class ppPaddleAgent extends ppPaddle {
  // Reference to the ball instance
  private ppBall myBall;

  /**
   * Initializes the agent instance and adds its paddle to the display.
   *
   * @param X Specifies the X position of the center of the paddle.
   * @param Y Specifies the Y position of the center of the paddle.
   * @param myColor Specifies the color that the ppPaddle should be drawn in.
   * @param myTable Specifies a reference to the ppTable object holding a reference to the display.
   */
  public ppPaddleAgent(double X, double Y, Color myColor, ppTable myTable) {
    super(X, Y, myColor, myTable);
  }

  /**
   * Sets the ball instance that the paddle should track.
   *
   * @param myBall Specifies the ppBall instance to track.
   */
  public void attachBall(ppBall myBall) {
    this.myBall = myBall;
  }

  /** Overrides the run method of java.lang.Thread. Holds the main loop for the ppPaddle. */
  public void run() {
    while (canPlay) {
      // Get ball position and speed
      double ballX = myBall.getX();
      double ballY = myBall.getY();
      double ballVx = myBall.getVx();
      double ballVy = myBall.getVy();
      // Track the ball if it is on the agent's side coming towards him
      if (ballVx < 0 && ballX < (ppPaddleXinit + ppAgentXinit) / 2) {
        // Find linear approximation of ball's Y position when it reaches the paddle's X position
        double predictedY = ballVy / ballVx * (getX() - ballX) + ballY;
        myTable.getDisplay().pause(AGENTLAG);
        setY(predictedY);
      }
      // Update paddle parameters
      updateParams();
      // Time has to be translated to ms
      myTable.getDisplay().pause(TICK * TIMESCALE);
    }
  }
}
