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
import edu.wpi.first.wpilibj.Compressor;

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

    int prev_count;

    Kicker kicker;



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

        kicker = new Kicker();
    }

    /**
     * This function is called when the robot enters autonomous mode.
     */
    public void autonomousInit()
    {
        m_count = 0;
        prev_count = 0;
        System.out.println("Autonomous Init");
        kicker.turnOnKicker();
        RDC.setDriveType(2);
    }

    /**
     * This function is called periodically (100Hz) during autonomous
     */
    public void autonomousPeriodic()
    {
        m_count++;

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

            
        }

        //Runs periodically at 10Hz.
        if (m_count % 10 == 0)
        {

        }

        //Runs periodically at 5Hz.
        if (m_count % 20 == 0)
        {
           kicker.primeKicker();
        }

            if(m_count > 5 && m_count < 75) //wait 60
                RDC.driveAutonomous(.3);
            if(m_count > 75 && m_count < 110)
                RDC.driveAutonomous(0);
            if(m_count == 110)
                kicker.kick(m_count);

            if(m_count > 130 && m_count < 225)
                RDC.driveAutonomous(.3);
            if(m_count > 225 && m_count < 310)
                RDC.driveAutonomous(0);
            if(m_count == 311)
                kicker.kick(m_count);

            if(m_count > 312 && m_count < 402)
                RDC.driveAutonomous(.3);
            if(m_count > 402 && m_count < 511 )
                RDC.driveAutonomous(0);
            if(m_count == 512)
                kicker.kick(m_count);
        
    }

    /**
     * This function is called when the robot enters teleop mode.
     */
    public void teleopInit()
    {
        m_count = 0;

        System.out.println("TeleOp Initialized.");

        kicker.turnOnKicker();
    }

    /**
     * This function is called periodically (100Hz) during operator control
     */
    public void teleopPeriodic()
    {
        m_count++;

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

            /*if(RDC.joystick_1.getRawButton(6)||RDC.joystick_1.getRawButton(7))//Tank
                RDC.setDriveType(0);*/
            if(RDC.joystick_1.getRawButton(8)||RDC.joystick_1.getRawButton(9))//Car
                RDC.setDriveType(1);
            if(RDC.joystick_1.getRawButton(10)||RDC.joystick_1.getRawButton(11))//Swerve
                RDC.setDriveType(2);
            if(RDC.joystick_2.getTrigger() && RDC.joystick_2.getRawButton(2)) //Diagnostic
                RDC.setDriveType(4);
            if(RDC.joystick_1.getRawButton(6)||RDC.joystick_1.getRawButton(7))//Swerve Rotation
                RDC.setDriveType(3);

            if(RDC.joystick_2.getRawButton(6) && m_count - prev_count > 30)
            {
                RDC.setJoystickType(0);//one joystick
                prev_count = m_count;
            }
            if(RDC.joystick_2.getRawButton(7) && m_count - prev_count > 30)
            {
                RDC.setJoystickType(1);//two joystick
                prev_count = m_count;
            }

            if(RDC.joystick_1.getRawButton(4) && m_count - prev_count > 30)
            {
                RDC.change90Mode();
                prev_count = m_count;
            }
            /*if(RDC.joystick_1.getRawButton(5) && m_count - prev_count > 30)
            {
                RDC.change45Mode();
                prev_count = m_count;
            }*/

            if(RDC.joystick_1.getRawButton(3) && m_count - prev_count > 30)
            {
                RDC.setInvertJoystickX();
                prev_count = m_count;
            }
            if(RDC.joystick_1.getRawButton(2) && m_count - prev_count > 30)
            {
                RDC.setInvertJoystickY();
                prev_count = m_count;
            }

            RDC.drive();

            if(RDC.joystick_1.getRawButton(4))
                kicker.setHardSoft(true);
            
            if(RDC.joystick_1.getRawButton(5))
                kicker.setHardSoft(false);

            if (AxisCamera.getInstance().freshImage())
            {
                try
                {
                    System.out.println("image!");
                    ColorImage image = AxisCamera.getInstance().getImage();
                    Thread.yield();
                    image.free();
                }
                catch (NIVisionException ex)
                {
                }
                catch (AxisCameraException ex)
                {
                }
            }

            
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

        if(m_count % 15 == 0)
        {
            kicker.primeKicker();

            if(RDC.joystick_1.getTrigger())
                kicker.kick(m_count);
        }



    }



    /**
     * This function is called when the robot enters disabled mode.
     */
    public void disabledInit()
    {
        m_count = 0;
        kicker.disarm();
    }

    /**
     * This function is called periodically (100Hz) during disabled mode.
     */
    public void disabledPeriodic()
    {
        m_count++;

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
