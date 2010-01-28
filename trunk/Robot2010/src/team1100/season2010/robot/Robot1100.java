package team1100.season2010.robot;


import edu.wpi.first.wpilibj.IterativeRobot;

import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.camera.AxisCameraException;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.NIVisionException;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.PIDController;


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

    edu.wpi.first.wpilibj.PIDController pid;


    Joystick joystick_1;
    Joystick joystick_2;

    RobotDrive drive;

    double kScoreThreshold = .01;
    AxisCamera cam;

    class ScaledPIDSource implements edu.wpi.first.wpilibj.PIDSource
    {
        edu.wpi.first.wpilibj.AnalogChannel m_src;
        int m_scale;
        ScaledPIDSource(edu.wpi.first.wpilibj.AnalogChannel source, int scale)
        {
            m_src = source;
            m_scale = scale;
        }
        public double pidGet()
        {
            return m_src.pidGet() * m_scale;
        }
    };
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit()
    {
        Timer.delay(10.0);
        cam = AxisCamera.getInstance();
        cam.writeResolution(AxisCamera.ResolutionT.k320x240);
        cam.writeBrightness(0);

        //Sets periodic call rate to 10 milisecond intervals, i.e. 100Hz.
        this.setPeriod(0.01);
        System.out.print("ROBOT STARTUP");

        drive = new RobotDrive(1,5);
       
        //drive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, false);
        //drive.setInvertedMotor(RobotDrive.MotorType.kRearRight, false);
        final int jagChan = 3;
        edu.wpi.first.wpilibj.Jaguar pwm;
        pwm = new edu.wpi.first.wpilibj.Jaguar(jagChan);
        ScaledPIDSource srcChan;
        srcChan = new ScaledPIDSource(new edu.wpi.first.wpilibj.AnalogChannel(1, 7), -1);
        // Hmmm pidSrc = new Hmmm(new edu.wpi.first.wpilibj.Encoder(1, 2));
        pid = new edu.wpi.first.wpilibj.PIDController(0.1, 0.001, 0.0, srcChan, pwm);
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

            try
            {
                if (cam.freshImage())
                {// && turnController.onTarget()) {
                    double gyroAngle = 50.0;
                    ColorImage image = cam.getImage();
                    Thread.yield();
                    Target[] targets = Target.findCircularTargets(image);
                    Thread.yield();
                    image.free();
                    if (targets.length == 0 || targets[0].m_score < kScoreThreshold)
                    {
                        System.out.println("No target found");
                        Target[] newTargets = new Target[targets.length + 1];
                        newTargets[0] = new Target();
                        newTargets[0].m_majorRadius = 0;
                        newTargets[0].m_minorRadius = 0;
                        newTargets[0].m_score = 0;
                        for (int i = 0; i < targets.length; i++)
                        {
                            newTargets[i + 1] = targets[i];
                        }
                        DashboardPacker.updateVisionDashboard(0.0, gyroAngle, 0.0, 0.0, newTargets);
                    }
                    else
                    {
                        System.out.println(targets[0]);
                        System.out.println("Target Angle: " + targets[0].getHorizontalAngle());
                        //turnController.setSetpoint(gyroAngle + targets[0].getHorizontalAngle());
                        DashboardPacker.updateVisionDashboard(0.0, gyroAngle, 0.0, targets[0].m_xPos / targets[0].m_xMax, targets);
                    }
                }
            }
            catch (NIVisionException ex)
            {
                ex.printStackTrace();
            }
            catch (AxisCameraException ex)
            {
                ex.printStackTrace();
            }
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

        System.out.println("TeleOp Init");

        joystick_1 = new Joystick(1);
        joystick_2 = new Joystick(2);

        pid.setInputRange(0, 1000);
        pid.setOutputRange(-1, 1);
        pid.setSetpoint(500);
        pid.enable();
    }

    /**
     * This function is called periodically (100Hz) during operator control
     */
    public void teleopPeriodic()
    {
        m_count++;
        //System.out.println("TeleOp: "+ m_count);

        drive.tankDrive(joystick_1, joystick_2);

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

            System.out.println ("Joystick 1" + " X = " + joystick_1.getX() + "Y = " + joystick_1.getY() + "Z = " + joystick_1.getZ());
            System.out.println ("Joystick 2" + "X = " + joystick_2.getX() + "Y = " + joystick_2.getY() + "Z = " + joystick_2.getZ() );


            //System.out.println("1Z: " + joystick_1.getZ());
            //System.out.println("2z: " + joystick_2.getZ());
        }

        //Runs periodically at 10Hz.
        if (m_count % 10 == 0)
        {

        }

        //Runs periodically at 5Hz.
        if (m_count % 20 == 0)
        {
            System.out.println("PID Error is " + pid.getError());
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
             System.out.println("Hello, world! in Disable mode...");
        }
    }
}
