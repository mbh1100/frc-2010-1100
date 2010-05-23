package team1100.season2010.robot.base;


import team1100.season2010.robot.*;
import team1100.season2010.robot.base.IterativeRobot;
import team1100.season2010.robot.DashboardPacker;

import edu.wpi.first.wpilibj.camera.AxisCamera;
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

   // SteeringPID testpid;

    AdvServo servo_lift = new AdvServo(4, 10);     // Instatntiating servo_lift
    boolean freed = false;
    int prev_joystick_mode = 0;
    boolean set = false;
    boolean reset = true;
    boolean suctionControlPushed = false;
    boolean suctionOn = false;

    boolean arm_button_pressed = false;
    HookLift lift;

    int prev_count;

    Camera camera;

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

    Relay spike;


    boolean isthrown = false;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit()
    {
        Watchdog.getInstance().setEnabled(true);
        Watchdog.getInstance().feed();
        camera = new Camera();
        //Sets periodic call rate to 10 milisecond intervals, i.e. 100Hz.
        // this.setPeriod(0.01);   //however, this appears to not actually have an effect (?)
        System.out.print("ROBOT STARTUP\n");  //and the period appears to be 50Hz according to Mark

        Watchdog.getInstance().feed();

        RDC = new RobotDriveController(0,joystick_1_channel,joystick_2_channel,
                jag_FR_channel,jag_FL_channel,jag_BR_channel,jag_BL_channel,CRM_front_channel,CRM_back_channel,
                POT_front_CHANNEL_VAL, POT_back_CHANNEL_VAL,4);
        RDC.setInvertedMotor(true,false,false,true);  //front r, front l, back r, back l

        kicker = new Kicker();

       // testpid = new SteeringPID(2, 1, 6, 1, true);
       // testpid.setLinearPct(10.0);

        Watchdog.getInstance().feed();

        lift = new HookLift(4,8);

        Watchdog.getInstance().feed();

        //CAMERA
        // camera = AxisCamera.getInstance();
        // camera.writeRotation(AxisCamera.RotationT.k0);
        // camera.writeBrightness(0);
        // camera.writeResolution(AxisCamera.ResolutionT.k320x240);

        Watchdog.getInstance().feed();
       // psoc = DriverStation.getInstance().getEnhancedIO();

        spike = new Relay(4,6);
        spike.setDirection(Relay.Direction.kForward);
    }

    /**
     * This function is called when the robot enters autonomous mode.
     */
    public void autonomousInit()
    {
        System.out.println(RDC.getPotVals());
        m_count = 0;
        Watchdog.getInstance().feed();
        prev_count = 0;
        System.out.println("Autonomous Init");
        kicker.turnOnKicker();
        RDC.setDriveType(2);
        autoDelay = aut_delay_pot.getAverageValue()/2 + 50;
        autoState = 0;
        spike.set(Relay.Value.kOff);

        // initialize the lift state in case the robot hasn't been
        // rebooted since teleop.
        lift.raiseArm();
        lift.liftHook();
        RDC.setSteeringCenter();

        try
        {
           if(DriverStation.getInstance().getEnhancedIO().getDigital(1))
               autoState += 4;
           if(DriverStation.getInstance().getDigitalIn(2))
               autoState += 2;
           if(DriverStation.getInstance().getDigitalIn(3))
               autoState += 1;
           kicker.setHardSoft(DriverStation.getInstance().getDigitalIn(4));
        }
        catch (edu.wpi.first.wpilibj.DriverStationEnhancedIO.EnhancedIOException e)
        {

        }
        System.out.println("Delay: " + autoDelay);
        System.out.println("autoState " + autoState);
        Watchdog.getInstance().feed();
    }

    /**
     * This function is called periodically (100Hz) during autonomous
     */
    public void autonomousPeriodic()
    {
        //Runs periodically at 100Hz
        m_count++;

        Watchdog.getInstance().feed();

        //Runs periodically at 5Hz.
        if (m_count % 20 == 0)
        {
           kicker.primeKicker();

        }

        if(m_count < autoDelay + 5)
        {
            RDC.driveAutonomous(1, 0);
        }
        Watchdog.getInstance().feed();

        if (autoState == 0) return;

        if(m_count > autoDelay + 5 && m_count < autoDelay + 85)
        {
           spike.set(Relay.Value.kOn);
           RDC.driveAutonomous(1, .35);
        }
        if(m_count > autoDelay + 85 && m_count < autoDelay + 150)
            RDC.driveAutonomous(1, .1);
        if(m_count == autoDelay + 150)
        {
            if (autoState != 3)
                kicker.kick(m_count);
        }

        Watchdog.getInstance().feed();

        /*
         * Autonomous modos
         * 0 nothing
         * 1 roll forward, kick, forward, kick, forward, kick
         * 2 roll forward, kick, forward, kick
         * 3 roll forward a bunch
         * 4 roll forward, kick
         * 5 roll forward, kick
         * 6 roll forward, kick
         * 7 roll forward, kick
         */

        if(autoState == 1 || autoState == 2 || autoState == 5 || autoState == 6 || autoState == 7)
        {
            if(m_count > autoDelay + 170 && m_count < autoDelay + 265)
            {
                RDC.driveAutonomous(1, .35);
            }
            if(m_count > autoDelay + 265 && m_count < autoDelay + 370)
                RDC.driveAutonomous(1, 0.1);
            if(m_count == autoDelay + 371)
            {
                kicker.kick(m_count);
            }
        }

        Watchdog.getInstance().feed();

        if(autoState == 1 || autoState == 7)
        {
            if(m_count > autoDelay + 372 && m_count < autoDelay + 457)
            {
                RDC.driveAutonomous(1, .35);
            }
            if(m_count > autoDelay + 457 && m_count < autoDelay + 591 )
                RDC.driveAutonomous(1, 0.1);
            if(m_count == autoDelay + 592)
            {
                kicker.kick(m_count);
            }
        }

        Watchdog.getInstance().feed();

        if(autoState == 3)
        {
            if(m_count > autoDelay + 151 && m_count < autoDelay + 600)
              RDC.driveAutonomous(1, 0.35);
            if(m_count > autoDelay + 600)
              RDC.driveAutonomous(1,0.0);
        }

        Watchdog.getInstance().feed();

        if(autoState == 8)
        {
            if(m_count > autoDelay + 372 && m_count < autoDelay + 431)
              RDC.driveAutonomous(2, 0);
            if(m_count > autoDelay + 431 && m_count < autoDelay + 501)
              RDC.driveAutonomous(2, .4);
            if(m_count > autoDelay + 501 && m_count < autoDelay + 601)
              RDC.driveAutonomous(1,0.1);
        }

        Watchdog.getInstance().feed();

        if(autoState == 8)
        {
            if(m_count > autoDelay + 151 && m_count < autoDelay + 211)
              RDC.driveAutonomous(2, 0);
            if(m_count > autoDelay + 211 && m_count < autoDelay + 281)
              RDC.driveAutonomous(2, -.4);
            if(m_count > autoDelay + 281 && m_count < autoDelay + 381)
              RDC.driveAutonomous(1,0.1);
        }

        Watchdog.getInstance().feed();

        if(autoState == 8)
        {
            if(m_count > autoDelay + 371 && m_count < autoDelay + 431)
              RDC.driveAutonomous(2, 0);
            if(m_count > autoDelay + 431 && m_count < autoDelay + 501)
              RDC.driveAutonomous(2, -.4);
            if(m_count > autoDelay + 501 && m_count < autoDelay + 601)
              RDC.driveAutonomous(1,0.1);
        }

        Watchdog.getInstance().feed();

        if(autoState == 8)
        {
            if(m_count > 592 && m_count < 770)
                RDC.driveAutonomous(1,.35);
            if(m_count > 770 && m_count < 800)
                RDC.driveAutonomous(1,0.1);
        }
        Watchdog.getInstance().feed();        

    }



    /**
     * This function is called when the robot enters teleop mode.
     */
    public void teleopInit()
    {
        m_count = 0;
        Watchdog.getInstance().feed();
        System.out.println("TeleOp Initialized.");
        System.out.println(RDC.getPotVals());
        kicker.turnOnKicker();
        Watchdog.getInstance().feed();
        RDC.setSteeringCenter();
        spike.set(Relay.Value.kOff);

    }

    /**
     * This function is called periodically (100Hz) during operator control
     */
    public void teleopPeriodic()
    {
        m_count++;
        Watchdog.getInstance().feed();
        // double pavlovsWatch = Watchdog.getInstance().getExpiration();
        // System.out.println(pavlovsWatch);

        //Runs periodically at 100Hz
        {

            currentTime = System.currentTimeMillis();
            double difference = currentTime - lastTime;

            //System.out.println("loop time = " + difference + "; max loop time: " + maxDifference);
            if ((difference >= maxDifference) && (difference <= 1234567890000.0))
            {
                maxDifference = difference;
//                System.out.println("                                Too long " + maxDifference);
            }
            else
            {
                //System.out.println ("   slick!");
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
            /**
             *    Joystick Code
             */



            //  suction control
            if (operator_joystick.getRawButton(5))
            {
                if (!suctionControlPushed)
                {
                    // first time here
                    if (suctionOn)
                    {
                        spike.set(Relay.Value.kOff);
                        suctionOn = false;
                    }
                    else
                    {
                        spike.set(Relay.Value.kOn);
                        suctionOn = true;
                    }
                }
                suctionControlPushed = true;
            }
            else
            {
                suctionControlPushed = false;
            }
           
            Watchdog.getInstance().feed();

            /*if(RDC.joystick_1.getRawButton(6)||RDC.joystick_1.getRawButton(7))//Tank
                RDC.setDriveType(0);*/

            // Car mode button
            if(RDC.joystick_1.getRawButton(8)||RDC.joystick_1.getRawButton(9))
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

            // Swerve mode button
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

            Watchdog.getInstance().feed();

            // Diagnostic mode button
            if(RDC.joystick_2.getTrigger() && RDC.joystick_2.getRawButton(2)) //Diagnostic
                RDC.setDriveType(4);

            // Swerve Rotation button
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
                }
            if(RDC.joystick_2.getRawButton(9)&&RDC.joystick_2.getRawButton(8)) //diagnostic pcoeffs
                if(RDC.joystick_2.getTrigger())
                    RDC.setDriveType(7);

            Watchdog.getInstance().feed();

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

            Watchdog.getInstance().feed();

            // 90 degree mode button
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

            Watchdog.getInstance().feed();

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


            Watchdog.getInstance().feed();

            if(RDC.getDriveType() != 4)
            {
              //System.out.println(RDC.getPotVals());
              //System.out.println(RDC.getPWMVals());
              //System.out.println(RDC.getJoystickVals());
            }

            /*if(operator_joystick.getRawButton(7) && operator_joystick.getRawButton(4) && !freed)
                servo_lift.set(true);  //true = "forward"
            if(operator_joystick.getRawButton(7) && operator_joystick.getRawButton(6) && !freed)
                servo_lift.set(false); //false = "backward"
            if(operator_joystick.getRawButton(7) && operator_joystick.getRawButton(5) && !freed)
            {
                servo_lift.free();
                freed = true;
            }*/

            Watchdog.getInstance().feed();

            /*
             *  Operator Joystick Buttons for lift:
             *  4 - drop arm, then deploy hook when released
             *  6 - lift hook
             *  7++- - safety
             *  Joystick 2 Y controls the winch
             */

            // CODE HAS NOT BEEN TESTED YET!!!
            
          /*  if(operator_joystick.getRawButton(7))
            {
                // If drop button pushed
                if(operator_joystick.getRawButton(4))
                {
                    if(!lift.isArmDropped())
                    {
                        lift.dropArm();
                        arm_button_pressed = true;
                    }
                } 
                else
                {
                    // When drop button is released, activate latch
                    if(arm_button_pressed)
                    {
                        lift.dropHook();
                        arm_button_pressed = false;
                    }
                }
                
                
               // Manual Hook Control
                // Used for after the hook automaticly latchs
                if(operator_joystick.getRawButton(6))
                {
                    lift.liftHook();
                }
                else if(operator_joystick.getRawButton(4) && !arm_button_pressed && lift.isHookDropped())
                {
                    lift.dropHook();
                }

                if(lift.isArmDropped())
                {
                   lift.moveWinch(-RDC.joystick_2.getY());
                }
            }
            else
            {
                 lift.stopWinch();
            }
          */

            if (operator_joystick.getRawButton(7))
            {
                if (operator_joystick.getRawButton(2))
                {
                    isthrown = true;
                    lift.dropArm();
                }

               boolean trythrow = operator_joystick.getRawButton(2);

               if (isthrown && !trythrow)
               {
                   lift.raiseArm();
                                }

               if (operator_joystick.getRawButton(4))
               {
                   lift.dropHook();
               }

               if (operator_joystick.getRawButton(6))
               {
                   lift.liftHook();
               }

               RDC.setJoystickType(0);
               lift.moveWinch(-RDC.joystick_2.getY());
            }
            else
            {
                lift.moveWinch(0);
            }
            
            /*ORIGINAL CODE FOR LIFT WINCH
            if(operator_joystick.getRawButton(7))
            {
                lift.lock(false);
                System.out.println("unlocked");
                reset = false;
                if(!set)
                {
                    prev_joystick_mode = RDC.getJoystickType();
                    set = true;
                }
                RDC.setJoystickType(0);
                lift.move(-RDC.joystick_2.getY());

                if(operator_joystick.getRawButton(4))
                {
                    lift.attach(true);
                    System.out.println("attach");
                }
                if(operator_joystick.getRawButton(6))
                {
                    lift.attach(false);
                    System.out.println("un attach");
                }
            }
            else
            {
                lift.lock(true);
                System.out.println("locked");
                lift.move(0);
                set = false;
                if(!reset)
                {
                  RDC.setJoystickType(prev_joystick_mode);
                  reset = true;
                }
            }
*/
            /*if(operator_joystick.getRawButton(7) && operator_joystick.getY() > .8)
                lift.up();
            else if(operator_joystick.getRawButton(7) && operator_joystick.getY() < -.8)
                lift.down();
            else lift.stop()*/

            Watchdog.getInstance().feed();
        }

        //Runs periodically at 10Hz.
        if (m_count % 10 == 0)
        {
            // testpid.setDirection(RDC.joystick_1.getX());
        }

        //Runs periodically at 5Hz.
        if (m_count % 20 == 0)
        {


        }

        if(m_count % 20 == 0)
        {
            //if(RDC.joystick_1.getTrigger() && RDC.joystick_2.getTrigger()) // priming kicker test!!! remove 'if' for competition
                kicker.primeKicker();
                //System.out.println("priming for kick");//added by alex to test on saturday

                Watchdog.getInstance().feed();

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
        Watchdog.getInstance().feed();
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
         //   DashboardPacker.updateDashboard();
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
