/** */
package ppPackage;

import static ppPackage.ppSimParams.*;

import java.awt.Color;

import acm.graphics.GLine;

/**
 * @author gabriel
 *     <p>This class encapsulates the logic related to setting up and updating the display of the
 *     GraphicsProgram for the simulation.
 */
public class ppTable {
  // Reference to the ppSimPaddleAgent instance
  private ppSimPaddleAgent dispRef;
  /**
   * Initializes the display for the simulation. Takes a reference to the ppSimPaddle object and
   * adds the borders and floor to the display. It stores the reference to the display object for
   * future reference.
   *
   * @param dispRef Specifies a reference to the the ppSimPaddle class containing the main logic for
   *     the program
   */
  public ppTable(ppSimPaddleAgent dispRef) {
    this.dispRef = dispRef;

    drawFloor();
  }

  /** Erases screen and draws a new ground plane. */
  public void newScreen() {
    dispRef.removeAll();
    drawFloor();
  }

  /** Helper function to add a new floor plane to the diplay. */
  private void drawFloor() {
    final GLine floor = new GLine(0, scrHEIGHT, scrWIDTH + OFFSET, scrHEIGHT);
    floor.setColor(Color.BLACK);
    dispRef.add(floor);
  }

  /**
   * Translates a simulation x coordinate and returns its equivalent screen x coordinate.
   *
   * @param x Specifies the simulation coordinate in x to be translated.
   * @return Returns the equivalent x coordinate on the screen.
   */
  double toScrX(double x) {
    return x * SCALE;
  }

  /**
   * Translates a simulation y coordinate and returns its equivalent screen y coordinate.
   *
   * @param y Specifies the simulation coordinate in y to be translated.
   * @return Returns the equivalent y coordinate on the screen.
   */
  double toScrY(double y) {
    return scrHEIGHT - y * SCALE;
  }

  /**
   * Returns a reference to the ppSimPaddle class instance used by the ppTable.
   *
   * @return Returns the reference to the ppSimPaddle class instance.
   */
  ppSimPaddleAgent getDisplay() {
    return this.dispRef;
  }

  /**
   * ScrtoX translates a screen X coordinate to its equivalent simulation X coordinate.
   *
   * @param ScrX Specifies the screen X coordinate to be translated.
   * @return Returns the equivalent X simulation coordinate.
   */
  double ScrtoX(double ScrX) {
    return ScrX / SCALE;
  }

  /**
   * ScrtoY translates a screen Y coordinate to its equivalent simulation Y coordinate.
   *
   * @param ScrY Specifies the screen Y coordinate to be translated.
   * @return Returns the equivalent Y simulation coordinate.
   */
  double ScrtoY(double ScrY) {
    return (scrHEIGHT - ScrY) / SCALE;
  }
}
