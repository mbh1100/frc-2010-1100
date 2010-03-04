package team1100.season2010.robot;


import team1100.season2010.robot.DashboardPacker;
import edu.wpi.first.wpilibj.IterativeRobot;

//import edu.wpi.first.wpilibj.camera.AxisCamera;
//import edu.wpi.first.wpilibj.camera.AxisCameraException;
//import edu.wpi.first.wpilibj.image.ColorImage;
//import edu.wpi.first.wpilibj.image.NIVisionException;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Watchdog;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStationEnhancedIO;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;

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


    AdvServo servo_lift = new AdvServo(4, 10);     // Instatntiating servo_lift
    boolean freed = false;
    int prev_joystick_mode = 0;
    boolean set = false;
    boolean reset = true;
    Lift lift;

    int prev_count;

    //Camera camera;

    //DriverStationEnhancedIO psoc;

    Kicker kicker;
    Joystick operator_joystick = new Joystick(3);

    AnalogChannel aut_delay_pot = new AnalogChannel(1,5);
    int autoDelay;
    DigitalInput aut_switch_1 = new DigitalInput(4,1);
    DigitalInput aut_switch_2 = new DigitalInput(4,2);
    DigitalInput aut_switch_3 = new DigitalInput(4,3);
    int autoState;


    double lastTime;
    double currentTime = 0;
    double maxDifference = 0;


    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit()
    {

        //camera = new Camera();
        //Sets periodic call rate to 10 milisecond intervals, i.e. 100Hz.
        this.setPeriod(0.01);   //however, this appears to not actually have an effect (?)
        System.out.print("ROBOT STARTUP");  //and the period appears to be 50Hz according to Mark

        RDC = new RobotDriveController(0,joystick_1_channel,joystick_2_channel,
                jag_FR_channel,jag_FL_channel,jag_BR_channel,jag_BL_channel,CRM_front_channel,CRM_back_channel,
                POT_front_CHANNEL_VAL, POT_back_CHANNEL_VAL,4);
        RDC.setInvertedMotor(true,false,true,false);  //front r, front l, back r, back l

        kicker = new Kicker();

        lift = new Lift(4,8);

        Watchdog.getInstance().setEnabled(true);

       // psoc = DriverStation.getInstance().getEnhancedIO();
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
        autoDelay = aut_delay_pot.getAverageValue()/2 + 50;
        autoState = 0;
        if(aut_switch_1.get())
            autoState += 4;
        if(aut_switch_2.get())
            autoState += 2;
        if(aut_switch_3.get())
            autoState += 1;
        System.out.println("Delay: " + autoDelay);
        System.out.println("autoState " + autoState);
    }

    /**
     * This function is called periodically (100Hz) during autonomous
     */
    public void autonomousPeriodic()
    {
        m_count++;

        Watchdog.getInstance().feed();

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
        /*if (m_count % 20 == 0)
        {
           kicker.primeKicker();
        }

        if(m_count < autoDelay + 5)
            RDC.driveAutonomous(1, 0);

        if(m_count > autoDelay + 5 && m_count < autoDelay + 45) //wait 60
            RDC.driveAutonomous(1, .3);
        if(m_count > autoDelay + 45 && m_count < autoDelay + 110)
            RDC.driveAutonomous(1, 0);
        if(m_count == autoDelay + 110)
        {
            kicker.kick(m_count);
            System.out.println("KICK.");
        }

        if(autoState == 1 || autoState == 2 || autoState == 5 || autoState == 6 || autoState == 7)
        {
            if(m_count > autoDelay + 130 && m_count < autoDelay + 205)
                RDC.driveAutonomous(1, .3);
            if(m_count > autoDelay + 205 && m_count < autoDelay + 310)
                RDC.driveAutonomous(1, 0);
            if(m_count == autoDelay + 311)
            {
                kicker.kick(m_count);
                System.out.println("KICK.");
            }
        }

        if(autoState == 2 || autoState == 7)
        {
            if(m_count > autoDelay + 312 && m_count < autoDelay + 377)
                RDC.driveAutonomous(1, .3);
            if(m_count > autoDelay + 377 && m_count < autoDelay + 511 )
                RDC.driveAutonomous(1, 0);
            if(m_count == autoDelay + 512)
            {
                kicker.kick(m_count);
                System.out.println("KICK.");
            }
        }

        if(autoState == 3)
        {
            if(m_count > autoDelay + 111 && m_count < autoDelay + 171)
              RDC.driveAutonomous(2, 0);
            if(m_count > autoDelay + 171 && m_count < autoDelay + 221)
              RDC.driveAutonomous(2, .3);
            if(m_count > autoDelay + 221 && m_count < autoDelay + 321)
              RDC.driveAutonomous(1,0);
        }

        if(autoState == 5)
        {
            if(m_count > autoDelay + 311 && m_count < autoDelay + 371)
              RDC.driveAutonomous(2, 0);
            if(m_count > autoDelay + 371 && m_count < autoDelay + 421)
              RDC.driveAutonomous(2, .3);
            if(m_count > autoDelay + 421 && m_count < autoDelay + 521)
              RDC.driveAutonomous(1,0);
        }

        if(autoState == 4)
        {
            if(m_count > autoDelay + 111 && m_count < autoDelay + 171)
              RDC.driveAutonomous(2, 0);
            if(m_count > autoDelay + 171 && m_count < autoDelay + 221)
              RDC.driveAutonomous(2, -.3);
            if(m_count > autoDelay + 221 && m_count < autoDelay + 321)
              RDC.driveAutonomous(1,0);
        }

        if(autoState == 6)
        {
            if(m_count > autoDelay + 311 && m_count < autoDelay + 371)
              RDC.driveAutonomous(2, 0);
            if(m_count > autoDelay + 371 && m_count < autoDelay + 421)
              RDC.driveAutonomous(2, -.3);
            if(m_count > autoDelay + 421 && m_count < autoDelay + 521)
              RDC.driveAutonomous(1,0);
        }

        if(autoState == 7)
        {
            if(m_count > 512 && m_count < 600)
                RDC.driveAutonomous(1,.3);
            if(m_count > 600 && m_count < 660)
                RDC.driveAutonomous(1,0);
        }
        */

        //System.out.println("pot val : " + aut_delay_pot.getAverageValue());
        System.out.println("switch 1: " + aut_switch_1.get());
        System.out.println("switch 2: " + aut_switch_2.get());
        System.out.println("switch_3: " + aut_switch_3.get() + "\n\n");
        autoState = 0;
        if(aut_switch_1.get())
            autoState += 4;
        if(aut_switch_2.get())
            autoState += 2;
        if(aut_switch_3.get())
            autoState += 1;
        System.out.println("AUTOSTATE: " + autoState);
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

        // Watchdog.getInstance().feed();
        // double pavlovsWatch = Watchdog.getInstance().getExpiration();
        // System.out.println(pavlovsWatch);

        //Runs periodically at 100Hz
        {

            currentTime = System.currentTimeMillis();
            double difference = currentTime - lastTime;

            System.out.println("loop time = " + difference + "; max loop time: " + maxDifference);
            if ((difference >= maxDifference) && (difference <= 1234567890000.0))
            {
                maxDifference = difference;
                System.out.println("                                Too long " + maxDifference);
            }
            else
            {
                System.out.println ("   slick!");
            }

            lastTime = currentTime;

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

            /*if(RDC.joystick_1.getRawButton(6)||RDC.joystick_1.getRawButton(7))//Tank
                RDC.setDriveType(0);*/
            if(RDC.joystick_1.getRawButton(8)||RDC.joystick_1.getRawButton(9))//Car
            {
               // turnOffLEDs();
                RDC.setDriveType(1);
               /* try
                {
                    Watchdog.getInstance().feed();
                    psoc.setLED(1, true);
                }
                catch(DriverStationEnhancedIO.EnhancedIOException e){}*/
            }
            if(RDC.joystick_1.getRawButton(10)||RDC.joystick_1.getRawButton(11))//Swerve
            {
               /* turnOffLEDs();
                try
                {
                    Watchdog.getInstance().feed();
                    psoc.setLED(2, true);
                }
                catch(DriverStationEnhancedIO.EnhancedIOException e){} */
                RDC.setDriveType(2);
            }
            if(RDC.joystick_2.getTrigger() && RDC.joystick_2.getRawButton(2)) //Diagnostic
                RDC.setDriveType(4);
            if(RDC.joystick_1.getRawButton(6)||RDC.joystick_1.getRawButton(7))//Swerve Rotation
            {
               /* turnOffLEDs();
                try
                {
                    Watchdog.getInstance().feed();
                    psoc.setLED(3, true);
                }
                catch(DriverStationEnhancedIO.EnhancedIOException e){} */
                RDC.setDriveType(3);
            }
            if(RDC.joystick_1.getTrigger() && RDC.joystick_1.getRawButton(5)) //Diagnostic limit switches
                RDC.setDriveType(5);
            if(RDC.joystick_2.getRawButton(10) && RDC.joystick_2.getRawButton(11))  //Diagnostic pit setup
                if(RDC.joystick_2.getRawButton(4) && RDC.joystick_2.getRawButton(5))
                {
                    RDC.setDriveType(6);
                    kicker.turnOnKicker();
                }
            if(RDC.joystick_2.getRawButton(9)&&RDC.joystick_2.getRawButton(8))
                if(RDC.joystick_2.getTrigger())
                    RDC.setDriveType(7);

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

            Watchdog.getInstance().feed();

            RDC.drive();

            if(operator_joystick.getRawButton(1))
                kicker.setHardSoft(true);

            if(operator_joystick.getRawButton(3))
                kicker.setHardSoft(false);


            if(RDC.getDriveType() != 4)
              System.out.println(RDC.getPotVals());
              System.out.println(RDC.getPWMVals());
              System.out.println(RDC.getJoystickVals());


            /*if(operator_joystick.getRawButton(7) && operator_joystick.getRawButton(4) && !freed)
                servo_lift.set(true);  //true = "forward"
            if(operator_joystick.getRawButton(7) && operator_joystick.getRawButton(6) && !freed)
                servo_lift.set(false); //false = "backward"
            if(operator_joystick.getRawButton(7) && operator_joystick.getRawButton(5) && !freed)
            {
                servo_lift.free();
                freed = true;
            }*/

            if(operator_joystick.getRawButton(7))
            {
                lift.lock(false);
                reset = false;
                if(!set)
                {
                    prev_joystick_mode = RDC.getJoystickType();
                    set = true;
                }
                RDC.setJoystickType(0);
                lift.move(RDC.joystick_2.getY());

                if(operator_joystick.getRawButton(4))
                    lift.attach(true);
                if(operator_joystick.getRawButton(6))
                    lift.attach(false);
            }
            else
            {
                lift.lock(true);
                set = false;
                if(!reset)
                {
                  RDC.setJoystickType(prev_joystick_mode);
                  reset = true;
                }
            }

            /*if(operator_joystick.getRawButton(7) && operator_joystick.getY() > .8)
                lift.up();
            else if(operator_joystick.getRawButton(7) && operator_joystick.getY() < -.8)
                lift.down();
            else lift.stop()*/

            /*if(operator_joystick.getRawButton(9))
                suction;*/

            Watchdog.getInstance().feed();
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
            //if(RDC.joystick_1.getTrigger() && RDC.joystick_2.getTrigger()) // priming kicker test!!! remove 'if' for competition
                kicker.primeKicker();
                System.out.println("priming for kick");//added by alex to test on saturday
            if(operator_joystick.getRawButton(8))
                kicker.kick(m_count);
        }

        /*try
        {
            Watchdog.getInstance().feed();
            if (m_count % 200 == 0)
            {
                psoc.setLED(1, true);
            }

            if (m_count % 300 == 0)
            {
                psoc.setLED(1, false);
            }
        }
        catch(DriverStationEnhancedIO.EnhancedIOException e){}*/

        /*  if (AxisCamera.getInstance().freshImage())
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
            } */
    }


  /*  public void turnOffLEDs()
    {
        try
        {
            psoc.setLED(1, false);
            psoc.setLED(2, false);
            psoc.setLED(3, false);
            psoc.setLED(4, false);
            psoc.setLED(5, false);
            psoc.setLED(6, false);
            psoc.setLED(7, false);
            psoc.setLED(8, false);
        }
        catch(DriverStationEnhancedIO.EnhancedIOException e){}
    }
*/


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

        Watchdog.getInstance().feed();

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
