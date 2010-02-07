package team1100.season2010.robot;


import team1100.season2010.robot.DashboardPacker;
import edu.wpi.first.wpilibj.IterativeRobot;

import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.camera.AxisCameraException;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.NIVisionException;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Watchdog;
import edu.wpi.first.wpilibj.Solenoid;
//import edu.wpi.first.wpilibj.Compressor;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot1100 extends IterativeRobot
{
    //Counts how many periodic cycles have passed.
    int m_count;

    final int POT_front_CHANNEL_VAL = 1;
    final int POT_back_CHANNEL_VAL = 2;

    final int joystick_1_channel = 1;
    final int joystick_2_channel = 2;
    final int jag_FR_channel = 1;
    final int jag_FL_channel = 5;
    final int jag_BR_channel = 2;
    final int jag_BL_channel = 6;
    final int CRM_back_channel = 4;
    final int CRM_front_channel = 3;

    RobotDriveController RDC;
    Kicker kicker;

    /*AnalogChannel p1 = new AnalogChannel(1);
    AnalogChannel p2 = new AnalogChannel(2);
    Joystick j1 = new Joystick(1);
    Joystick j2 = new Joystick(2);
    Jaguar m1 = new Jaguar(3);
    Jaguar m2 = new Jaguar(4);*/

    //Compressor compressor;


    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit()
    {
        //Sets periodic call rate to 10 milisecond intervals, i.e. 100Hz.
        this.setPeriod(0.01);
        System.out.print("ROBOT STARTUP");

        RDC = new RobotDriveController(0,joystick_1_channel,joystick_2_channel,
                jag_FR_channel,jag_FL_channel,jag_BR_channel,jag_BL_channel,CRM_front_channel,CRM_back_channel,
                POT_front_CHANNEL_VAL, POT_back_CHANNEL_VAL,4);
        RDC.setInvertedMotor(true,true,true,true);

        //compressor = new Compressor(4,5,4,1);
        kicker = new Kicker();
    }

    /**
     * This function is called when the robot enters autonomous mode.
     */
    public void autonomousInit()
    {
        Compressor.instance().start();
        // kicker.arm();
        m_count = 0;
        System.out.println("Autonomous Init");
    }

    /**
     * This function is called periodically (100Hz) during autonomous
     */
    public void autonomousPeriodic()
    {
        m_count++;
        //System.out.println("AutoCount: " + m_count);

        //Runs periodically at 100Hz
        {

        }

        //Runs periodically at 50Hz.
        if (m_count % 2 == 0)
        {

        }

        //Runs periodically at 25Hz.
        if (m_count % 4 == 0)
        {

        }

        //Runs periodically at 20Hz.
        if (m_count % 5 == 0)
        {
            DashboardPacker.updateDashboard();
            // System.out.println("Packet Sent (Auto)");

        }

        //Runs periodically at 10Hz.
        if (m_count % 10 == 0)
        {

        }

        //Runs periodically at 5Hz.
        if (m_count % 20 == 0)
        {

        }
    }

    /**
     * This function is called when the robot enters teleop mode.
     */
    public void teleopInit()
    {
        m_count = 0;

        System.out.println("TeleOp Initialized.");

        Compressor.instance().start();
        // kicker.arm();
    }

    /**
     * This function is called periodically (100Hz) during operator control
     */
    public void teleopPeriodic()
    {
        m_count++;
        //System.out.println("TeleOp: "+ m_count);

        //drive.tankDrive(joystick_1, joystick_2);

        //Runs periodically at 100Hz
        {

        }

        //Runs periodically at 50Hz.
        if (m_count % 2 == 0)
        {

        }

        //Runs periodically at 25Hz.
        if (m_count % 4 == 0)
        {

        }

        //Runs periodically at 20Hz.
        if (m_count % 5 == 0)
        {
            Watchdog.getInstance().feed();
            DashboardPacker.updateDashboard();

            if(RDC.joystick_1.getRawButton(6)||RDC.joystick_1.getRawButton(7))
                RDC.setDriveType(0);
            if(RDC.joystick_1.getRawButton(8)||RDC.joystick_1.getRawButton(9))
                RDC.setDriveType(1);
            if(RDC.joystick_1.getRawButton(10)||RDC.joystick_1.getRawButton(11))
                RDC.setDriveType(2);

            RDC.drive();
            
            System.out.println(RDC.getPotVals());
            
           /* System.out.println("CRM front: " + p1.getAverageValue());
            System.out.println("\tCRM back: " + p2.getAverageValue());

            if(j1.getX()>.4)
              m1.set(.2);
            else if(j1.getX()<-.4)
              m1.set(-.2);
            else m1.set(0);
            if(j2.getX()>.4)
                m2.set(.2);
            else if(j2.getX()<-.4)
                m2.set(-.2);
            else m2.set(0);*/



            //System.out.println("Pressure Switch: " + compressor.getPressureSwitchValue());

            /*
            if (joystick_1.getTop())
            {
                if (joystick_1.getZ() > 0 )
                {
                    System.out.println("entering hard kick mode");
                    kicker.setKickMode(Kicker.kickHard);
                }
                else
                {
                    System.out.println("entering soft kick mode");
                    kicker.setKickMode(Kicker.kickSoft);
                }
            }

            if (kicker.isReady())
            {
                // light the 'armed' light.
                DriverStation.getInstance().setDigitalOut(1, true);
                // kick
                if (joystick_1.getTrigger())
                {
                    kicker.kick();
                    DriverStation.getInstance().setDigitalOut(1, false);
                }
            }
*/

        }

        //Runs periodically at 10Hz.
        if (m_count % 10 == 0)
        {

        }

        //Runs periodically at 5Hz.
        if (m_count % 20 == 0)
        {
            kicker.test(RDC.joystick_1);
        }
    }



    /**
     * This function is called when the robot enters disabled mode.
     */
    public void disabledInit()
    {
        m_count = 0;
        // System.out.println("Disabled Init 1100 version");
        Compressor.instance().stop();
        kicker.disarm();
    }

    /**
     * This function is called periodically (100Hz) during disabled mode.
     */
    public void disabledPeriodic()
    {
        m_count++;
       // System.out.println("Mcount =" + m_count);

        //Runs periodically at 100Hz
        {

        }

        //Runs periodically at 50Hz.
        if (m_count % 2 == 0)
        {

        }

        //Runs periodically at 25Hz.
        if (m_count % 4 == 0)
        {

        }

        //Runs periodically at 20Hz.
        if (m_count % 5 == 0)
        {
            DashboardPacker.updateDashboard();
            //System.out.println("Packet Sent (D)");
            //System.out.println(RDC.getPotVals());
        }

        //Runs periodically at 10Hz.
        if (m_count % 10 == 0)
        {

        }

        //Runs periodically at 5Hz.
        if (m_count % 20 == 0)
        {

        }

        //Runs periodically at 1/5 Hz.
        if (m_count % 500 == 0)
        {
            
        }
    }
}
