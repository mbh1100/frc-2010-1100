/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package team1100.season2010.robot.base;

import edu.wpi.first.wpilibj.SimpleRobot;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author mark
 */
public class IterativeRobot extends SimpleRobot {
    Timer timer;
    private TimerTask task;
    private boolean didDisabledPeriodic;
    private boolean didAutonomousPeriodic;
    private boolean didTeleopPeriodic;

    private boolean m_disabledInitialized;
    private boolean m_autonomousInitialized;
    private boolean m_teleopInitialized;

    public IterativeRobot() {
        timer = new Timer();
        task = new MainTimerTask(this);
    }

    public void robotMain() {
        robotInit();
        timer.scheduleAtFixedRate(task, 0, 20);
        for (;;) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void run() {
            // Call the appropriate function depending upon the current robot mode
            if (isDisabled()) {
                //System.out.println("IsDisabled");
                // call DisabledInit() if we are now just entering disabled mode from
                // either a different mode or from power-on
                if (!m_disabledInitialized) {
                    disabledInit();
                    m_disabledInitialized = true;
                    // reset the initialization flags for the other modes
                    m_autonomousInitialized = false;
                    m_teleopInitialized = false;
                    System.out.println("Disabled_Init() completed");
                }

                disabledPeriodic();
            } else if (isAutonomous()) {
                //System.out.println("IsAutonomous");

                // call Autonomous_Init() if this is the first time
                // we've entered autonomous_mode
                if (!m_autonomousInitialized) {
                    // KBS NOTE: old code reset all PWMs and relays to "safe values"
                    // whenever entering autonomous mode, before calling
                    // "Autonomous_Init()"
                    autonomousInit();
                    m_autonomousInitialized = true;
                    m_teleopInitialized = false;
                    m_disabledInitialized = false;
                    System.out.println("Autonomous_Init() completed");
                }

                getWatchdog().feed();
                autonomousPeriodic();


            } else { //Teleop
                //System.out.println("Teleop");
                // call Teleop_Init() if this is the first time
                // we've entered teleop_mode
                if (!m_teleopInitialized) {
                    teleopInit();
                    m_teleopInitialized = true;
                    m_autonomousInitialized = false;
                    m_disabledInitialized = false;
                    System.out.println("Teleop_Init() completed");
                }
                getWatchdog().feed();
                teleopPeriodic();
                didTeleopPeriodic = true;
            }
    }

    public void robotInit() {
        System.out.println("Default RobotIterativeBase::RobotInit() method running");
    }

    /**
     * Initialization code for disabled mode should go here.
     *
     * Users should override this method for initialization code which will be called each time
     * the robot enters disabled mode.
     */
    public void disabledInit() {
        System.out.println("Default RobotIterativeBase::DisabledInit() method running");
    }

    /**
     * Initialization code for autonomous mode should go here.
     *
     * Users should override this method for initialization code which will be called each time
     * the robot enters autonomous mode.
     */
    public void autonomousInit() {
        System.out.println("Default RobotIterativeBase::AutonomousInit() method running");
    }

    /**
     * Initialization code for teleop mode should go here.
     *
     * Users should override this method for initialization code which will be called each time
     * the robot enters teleop mode.
     */
    public void teleopInit() {
        System.out.println("Default RobotIterativeBase::TeleopInit() method running");
    }

    /**
     * Periodic code for disabled mode should go here.
     *
     * Users should override this method for code which will be called periodically at a regular
     * rate while the robot is in disabled mode.
     */
    public void disabledPeriodic() {
    }

    /**
     * Periodic code for autonomous mode should go here.
     *
     * Users should override this method for code which will be called periodically at a regular
     * rate while the robot is in autonomous mode.
     */
    public void autonomousPeriodic() {
    }

    /**
     * Periodic code for teleop mode should go here.
     *
     * Users should override this method for code which will be called periodically at a regular
     * rate while the robot is in teleop mode.
     */
    public void teleopPeriodic() {
    }

    private class MainTimerTask extends TimerTask {
        private IterativeRobot robot;

        public MainTimerTask(IterativeRobot r) {
            robot = r;
        }
        public void run() {
            //Run the run method in the IterativeRobot class
            robot.run();
        }
    }

}
