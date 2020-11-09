/** */
package ppPackage;

import static ppPackage.ppSimParams.*;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToggleButton;

import acm.io.IODialog;
import acm.program.GraphicsProgram;
import acm.util.RandomGenerator;

/**
 * @author gabriel
 *     <p>This class is the base for initialization of the ping-pong simulation. It is the main
 *     graphics program for this simulation.
 */
@SuppressWarnings("serial")
public class ppSimPaddleAgent extends GraphicsProgram {
  // Holds a reference to the ppPaddle instance
  private ppPaddle myPaddle;
  // Holds a reference to the ppTable instance
  private ppTable myTable;
  // Holds a reference to the ppPaddleAgent instance
  private ppPaddleAgent theAgent;
  // "Trace" toggle button
  private JToggleButton traceButton;
  // Random generator instance
  private RandomGenerator rgen;
  // Ball instance
  private ppBall myBall;
  // Player and agent scores
  private int agentScore;
  private JLabel agentScoreLabel;
  private int playerScore;
  private JLabel playerScoreLabel;

  public static void main(String[] args) {
    new ppSimPaddleAgent().start(args);
  }

  /** The init function is the entry point to the ping-pong simulation. */
  public void init() {
    this.resize(scrWIDTH + OFFSET, scrHEIGHT + OFFSET);

    String playerName = new IODialog().readLine("Enter the player's name:");
    String agentName = new IODialog().readLine("Enter the agent's name:");

    agentScore = 0;
    playerScore = 0;

    agentScoreLabel = new JLabel(String.valueOf(agentScore));
    JLabel agentNameLabel = new JLabel(agentName);
    agentNameLabel.setLabelFor(agentScoreLabel);

    playerScoreLabel = new JLabel(String.valueOf(playerScore));
    JLabel playerNameLabel = new JLabel(playerName);
    playerNameLabel.setLabelFor(playerScoreLabel);

    add(agentNameLabel, NORTH);
    add(agentScoreLabel, NORTH);
    add(playerNameLabel, NORTH);
    add(playerScoreLabel, NORTH);

    JButton clearButton = new JButton("Clear");
    JButton newServeButton = new JButton("New Serve");
    traceButton = new JToggleButton("Trace", true);
    JButton quitButton = new JButton("Quit");

    add(clearButton, SOUTH);
    add(newServeButton, SOUTH);
    add(traceButton, SOUTH);
    add(quitButton, SOUTH);

    addMouseListeners();
    addActionListeners();

    rgen = RandomGenerator.getInstance();
    rgen.setSeed(RSEED);

    myTable = new ppTable(this);
    myBall = newBall();

    myPaddle = new ppPaddle(ppPaddleXinit, ppPaddleYinit, ColorPaddle, myTable);
    theAgent = new ppPaddleAgent(ppAgentXinit, ppAgentYinit, ColorAgent, myTable);

    myBall.setPaddle(myPaddle);
    myBall.setAgent(theAgent);
    theAgent.attachBall(myBall);

    // This is already handled by the ppBall constructor, there's no reason for doing it here.
    // add(myBall.getBall());

    theAgent.start();
    myPaddle.start();
    myBall.start();
  }

  /**
   * Overrides the mouseMoved(...) method in Program. Specifies what should be executed everytime
   * the mouse is moved.
   *
   * @param e Holds the MouseEvent instance with information regarding the current mouse pointer
   *     information.
   */
  public void mouseMoved(MouseEvent e) {
    myPaddle.setY(myTable.ScrtoY((double) e.getY()));
  }

  /**
   * Overrides the actionPerformed(...) method in Program. Specifies what should be executed
   * everytime an ActionEvent is registered.
   *
   * @param e Holds the ActionEvent details.
   */
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();

    if (command.equals("Clear")) {
      resetScores();
      myTable.newScreen();
    } else if (command.equals("New Serve")) {
      if (!myBall.ballInPlay()) {
        // TODO: Create methods to prevent repetition here.
        myTable.newScreen();

        myBall = newBall();

        myPaddle = new ppPaddle(ppPaddleXinit, ppPaddleYinit, ColorPaddle, myTable);
        theAgent = new ppPaddleAgent(ppAgentXinit, ppAgentYinit, ColorAgent, myTable);

        myBall.setPaddle(myPaddle);
        myBall.setAgent(theAgent);
        theAgent.attachBall(myBall);

        theAgent.start();
        myPaddle.start();
        myBall.start();
      }
    } else if (command.equals("Quit")) {
      System.exit(0);
    }
  }

  /**
   * Initializes a new ppBall instance with random parameters.
   *
   * @return Returns the resulting ppBall instance.
   */
  ppBall newBall() {
    Color iColor = Color.RED;
    double iYinit = rgen.nextDouble(YinitMIN, YinitMAX);
    double iLoss = rgen.nextDouble(EMIN, EMAX);
    double iVel = rgen.nextDouble(VoMIN, VoMAX);
    double iTheta = rgen.nextDouble(ThetaMIN, ThetaMAX);

    return new ppBall(
        XINIT, iYinit, iVel, iTheta, iColor, iLoss, myTable, traceButton.isSelected());
  }

  /**
   * Updates the player and agent scores with the corresponding differences and update the labels.
   *
   * @param agentDif Specifies by how much the agent score should be incremented.
   * @param playerDif Specifies by how much the player score should be incremented.
   */
  public void updateScores(int agentDif, int playerDif) {
    // Update scores
    agentScore += agentDif;
    playerScore += playerDif;

    // Update labels
    agentScoreLabel.setText(String.valueOf(agentScore));
    playerScoreLabel.setText(String.valueOf(playerScore));
  }

  /** Reset all scores to 0 and update labels accordingly. */
  public void resetScores() {
    // Update scores
    agentScore = 0;
    playerScore = 0;

    // Update labels
    agentScoreLabel.setText(String.valueOf(agentScore));
    playerScoreLabel.setText(String.valueOf(playerScore));
  }
}
