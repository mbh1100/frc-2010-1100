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

    AnalogChannel pot_1 = new AnalogChannel(7);

    Joystick joystick_1;
    Joystick joystick_2;

    RobotDrive drive;

    final int POT_RANGE = 10;

   

    Jaguar testMotor = new Jaguar(3);

    //PIDController pid;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit()
    {
        Timer.delay(10.0);
        

        //Sets periodic call rate to 10 milisecond intervals, i.e. 100Hz.
        this.setPeriod(0.01);
        System.out.print("ROBOT STARTUP");

        drive = new RobotDrive(1,5);
        //drive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, false);
        //drive.setInvertedMotor(RobotDrive.MotorType.kRearRight, false);

        //pid = new PIDController(.1,.001,0, pot_1, testMotor);
        //pid.setInputRange(0, 1024);

    }

    /**
     * This function is called when the robot enters autonomous mode.
     */
    public void autonomousInit()
    {
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
            System.out.println("Packet Sent (Auto)");

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

        joystick_1 = new Joystick(1);
        joystick_2 = new Joystick(2);
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
            DashboardPacker.updateDashboard();
            //System.out.println("Packet Sent (TO)");
            
            //System.out.println("1X: " + joystick_1.getX() + " 1Y: " + joystick_1.getY());
            //System.out.println("2X: " + joystick_2.getX() + " 2Y: " + joystick_2.getY());

            //System.out.println ("Joystick 1" + " X = " + joystick_1.getX() + "Y = " + joystick_1.getY() + "Z = " + joystick_1.getZ());
            //System.out.println ("Joystick 2" + "X = " + joystick_2.getX() + "Y = " + joystick_2.getY() + "Z = " + joystick_2.getZ() );


            //System.out.println("1Z: " + joystick_1.getZ());
            //System.out.println("2z: " + joystick_2.getZ());

            //testMotor.set(joystick_2.getY());

            System.out.println("POT:" + pot_1.getValue());

            if(joystick_1.getMagnitude()>=.5)
            {
                System.out.println("\tAngle: " + joystick_1.getDirectionDegrees());
                if(pot_1.getValue() <= 1024.0 / 360.0 * (joystick_1.getDirectionDegrees() + 180) - POT_RANGE)
                    testMotor.set(-1);
                else if(pot_1.getValue() >= 1024.0 / 360.0 * (joystick_1.getDirectionDegrees() + 180) + POT_RANGE)
                    testMotor.set(1);
                else testMotor.set(0);
            }
            else
                testMotor.set(0);

            //System.out.println("X val: " + joystick_1.getX()/2);
            //System.out.println("\tPot v: " + pot_1.getValue());
            //System.out.println("\t\tPot.getPid()" + pot_1.pidGet());
            

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
     * This function is called when the robot enters disabled mode.
     */
    public void disabledInit()
    {
        m_count = 0;
       // System.out.println("Disabled Init 1100 version");
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
            System.out.println("Packet Sent (D)");
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
            // System.out.println("Hello, world! in Disable mode...");
        }
    }
}
