
package team1100.season2010.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.DigitalInput;

public class RobotDriveController
{
    private int back_pot_max     = 461;
    private int back_pot_center  = 391;
    private int back_pot_min     = 318;
    private int front_pot_max    = 862;
    private int front_pot_center = 790;
    private int front_pot_min    = 714;

    private int drive_type;
    private int joystick_type;
    private int prev_drive_type;
    private int diagnostic_state;

    private int joystick_adjust_X;
    private int joystick_adjust_Y;

    public Joystick joystick_1;
    public Joystick joystick_2;
    private AdvJaguar front_right_motor;
    private AdvJaguar front_left_motor;
    private AdvJaguar back_right_motor;
    private AdvJaguar back_left_motor;
    private int motor_direction_adjust;

    private int FRM_channel;
    private int FLM_channel;
    private int BRM_channel;
    private int BLM_channel;

    private DigitalInput limit_front_max;
    private DigitalInput limit_front_min;
    private DigitalInput limit_back_max;
    private DigitalInput limit_back_min;

    //private RobotDrive tank_drive;

    private AverageController drive_motor_speed_setpoint;

    private SteeringPID CRM_back;
    private SteeringPID CRM_front;
    private DSKnob frontCentering;
    private DSKnob rearCentering;
    private DSKnob steeringGain;

    public RobotDriveController()
    {
        this(0,1,2,1,2,3,4,5,6,7,8,10);
    }

    
    public RobotDriveController(int type, int j1_channel, int j2_channel, int frm_channel,
                                int flm_channel, int brm_channel, int blm_channel, int CRM_front_channel, int CRM_back_channel,
                                int pot_front_channel, int pot_back_channel,int avgNum)
    {
        drive_type = type;
        joystick_type = 0;
        prev_drive_type = 1;
        diagnostic_state = 0;

        joystick_adjust_Y = 1;  //JOYSTICK ADJUST LOCATION
        joystick_adjust_X = -1;

        joystick_1 = new Joystick(j1_channel);
        joystick_2 = new Joystick(j2_channel);

        front_right_motor = new AdvJaguar(frm_channel);
        FRM_channel = frm_channel;
        front_left_motor = new AdvJaguar(flm_channel);
        FLM_channel = flm_channel;
        back_right_motor = new AdvJaguar(brm_channel);
        BRM_channel = brm_channel;
        back_left_motor = new AdvJaguar(blm_channel);
        BLM_channel = blm_channel;

        drive_motor_speed_setpoint = new AverageController(avgNum);

        CRM_back = new SteeringPID(pot_back_channel, CRM_back_channel, true);
        CRM_front = new SteeringPID(pot_front_channel, CRM_front_channel, true);
        frontCentering = new DSKnob(1);
        rearCentering = new DSKnob(2);
        steeringGain = new DSKnob(4);

        CRM_back.setOperatingRangePct(15);
        CRM_back.setCenterPct(49);
        CRM_back.setLinearPct(steeringGain.getCurrentValue() * 20);
        CRM_front.setOperatingRangePct(15);
        CRM_front.setCenterPct(74);
        CRM_front.setLinearPct(steeringGain.getCurrentValue() * 20);
 
        limit_front_max = new DigitalInput(4,12);
        limit_front_min = new DigitalInput(4,11);
        limit_back_max = new DigitalInput(4,14);
        limit_back_min = new DigitalInput(4,13);
    }

    /*Sets drive type (tank, swerve, car...)
     * @param type : 0 = tank drive, 1 = car drive, 2 = swerve drive, 3 = swerve rotation, 4 = diagnostic
     * */
    public void setDriveType(int type)
    {
        drive_type = type;
        if(drive_type == 2)
        {
            //translationInit();
        }
        if(drive_type == 3)
        {
            //rotationInit();
        }
    }

    public int getDriveType ()
    {
        return drive_type;
    }

    //@param type: 0 = one joystick mode, 1 = two joystick mode
    public void setJoystickType(int type)
    {
        joystick_type = type;
    }

    public int getJoystickType()
    {
        return joystick_type;
    }

    public void change90Mode()
    {
        if(drive_type == 22)
        {
            drive_type = prev_drive_type;
        }
        else 
        {
            if(drive_type != 33)
              prev_drive_type = drive_type;
            drive_type = 22;
        }
        //translationInit();
    }

    /*public void change45Mode()
    {
        if(drive_type == 33)
            drive_type = prev_drive_type;
        else
        {
            if(drive_type != 22)
                prev_drive_type = drive_type;
            drive_type = 33;
        }
        rotationInit();
    }*/

    public void setInvertJoystickX()
    {
        joystick_adjust_X = joystick_adjust_X * -1;
        System.out.println("X inverted");
    }

    public void setInvertJoystickY()
    {
        joystick_adjust_Y = joystick_adjust_Y * -1;
        System.out.println("Y inverted");
    }

    public String getPotVals()
    {
        return "POT VALS: + CRM Front: " + CRM_front.getPot() +
                "\tctr: " + CRM_front.getCtr() +
                "\tCRM Back: " + CRM_back.getPot() +
                "\tctr: " + CRM_back.getCtr() + "\n";
    }

    public String getPWMVals()
    {
        return "PWM VALS: \nCRM Front: ";//+ CRM_front.getPWM() + "\n\tCRM Back: " + CRM_back.getPWM() + "\n";
    }

    public String getJoystickVals()
    {
        if(joystick_type == 1)
          return "JOYSTICK VALS: \n X:" + joystick_1.getX() + "\n\tY: " + joystick_2.getY() + "\n\n";
        else return "JOYSTICK VALS: \n X:" + joystick_1.getX() + "\n\tY: " + joystick_1.getY() + "\n\n";
    }

    public void drive()
    {
        // System.out.println(getPotVals());
        //if     (drive_type == 0)
        //    tankDrive();
            // the analog input ranges from about 0 to 3.2
        CRM_front.setCenterPct(frontCentering.getCurrentValue()*30);
        CRM_back.setCenterPct(rearCentering.getCurrentValue()*30);
        CRM_front.setLinearPct(steeringGain.getCurrentValue() * 20);
        CRM_back.setLinearPct(steeringGain.getCurrentValue() * 20);

        if(joystick_type == 1) //2 Joystick
        {
          if(drive_type == 1)
              carDrive();
          else if(drive_type == 2)
              swerveDrive();
          else if(drive_type == 3)
              swerveRotationDrive();
          else if(drive_type == 4)
              diagnostic();
          else if(drive_type == 22)
              translate90_TwoJoystick();
          //else if(drive_type == 33)
              //rotate45_TwoJoystick();
          else if(drive_type == 5)
              diagnosticLimitSwitches();
          else if(drive_type == 6)
              pitSetup();
          else if(drive_type == 7)
              setPCoeffsDriveSetup();
        }
        else  //1 joystick
        {
          if(drive_type == 1)
              carDriveOneJoystick();
          else if(drive_type == 2)
              swerveDriveOneJoystick();
          else if(drive_type == 3)
              swerveRotationDriveOneJoystick();
          else if(drive_type == 4)
              diagnostic();
          else if(drive_type == 22)
              translate90_OneJoystick();
          //else if(drive_type == 33)
              //rotate45_OneJoystick();
          else if(drive_type == 5)
              diagnosticLimitSwitches();
          else if(drive_type == 6)
              pitSetup();
          else if(drive_type == 7)
              setPCoeffsDriveSetup();
        }

        /*if(!limit_back_max.get() || !limit_back_min.get())
            CRM_back.setDirect(0);
        if(!limit_front_max.get() || !limit_front_min.get())
            CRM_front.setDirect(0);*/
    }

    public void driveAutonomous(int driveType, double speed)
    {
        if(driveType == 1)
        {
            CRM_back.setDirection(0);
            CRM_front.setDirection(0);
            goTankDriveAutonomous(speed);
        }
        if(driveType == 2)
        {
            swerveDriveAutonomous(speed);
        }
    }

    public void setSteeringCenter()
    {
        CRM_back.setCenterPct(rearCentering.getCurrentValue()*30);
        CRM_back.setLinearPct(steeringGain.getCurrentValue() * 20);
        CRM_front.setCenterPct(frontCentering.getCurrentValue()*30);
        CRM_front.setLinearPct(steeringGain.getCurrentValue() * 20);
        CRM_back.setDirection(0);
        CRM_front.setDirection(0);
    }

    private void diagnostic()
    {
        System.out.println(getPotVals());

        /*if(joystick_1.getX()>.4)
          CRM_back.setDirect(.2);
        else if(joystick_1.getX()<-.4)
          CRM_back.setDirect(-.2);
        else CRM_back.setDirect(0);
        if(joystick_2.getX()>.4)
          CRM_front.setDirect(.2);
        else if(joystick_2.getX()<-.4)
          CRM_front.setDirect(-.2);
        else CRM_front.setDirect(0);*/
    }

    private void diagnosticLimitSwitches()
    {
        /*if(joystick_1.getTrigger())
        {
            if(diagnostic_state == 0)  //find max
            {
                CRM_front.setDirect(.2);
                if(!limit_front_max.get()) //hits limit switch
                {
                    CRM_front.setDirect(0);
                    System.out.println("CRM_front pot_max: " + CRM_front.getPot());
                    CRM_front.setPotMax(CRM_front.getPot());
                    diagnostic_state++;
                }
            }
            else if(diagnostic_state == 1) //find min
            {
                CRM_front.setDirect(-.2);
                if(!limit_front_min.get()) //hits limit switch 2
                {
                    CRM_front.setDirect(0);
                    System.out.println("CRM_front pot_min: " + CRM_front.getPot());
                    CRM_front.setPotMin(CRM_front.getPot());
                    diagnostic_state++;
                }
            }
            else if(diagnostic_state == 2)  //find max
            {
                CRM_back.setDirect(.2);
                if(!limit_back_max.get()) //hits limit switch
                {
                    CRM_back.setDirect(0);
                    System.out.println("CRM_back pot_max: " + CRM_back.getPot());
                    CRM_back.setPotMax(CRM_back.getPot());
                    diagnostic_state++;
                }
            }
            else if(diagnostic_state == 3) //find min
            {
                CRM_back.setDirect(-.2);
                if(!limit_back_min.get()) //hits limit switch 2
                {
                    CRM_back.setDirect(0);
                    System.out.println("CRM_back pot_min: " + CRM_back.getPot());
                    CRM_back.setPotMin(CRM_back.getPot());
                    diagnostic_state++;
                }
            }
        }*/

    }

    private void tankDrive()
    {
        CRM_back.setDirection(0);
        CRM_front.setDirection(0);
        goTankDrive();
    }

    private void goTankDrive()
    {
        front_right_motor.set(joystick_2.getY());
        front_left_motor.set(joystick_1.getY());
        back_right_motor.set(joystick_2.getY());
        back_left_motor.set(joystick_1.getY());
    }

    private void goTankDriveAutonomous(double spd)
    {
        spd = -spd;
        front_right_motor.set(spd);
        front_left_motor.set(spd);
        back_right_motor.set(spd);
        back_left_motor.set(spd);
    }

    private void carDrive()
    {
        CRM_back.setDirection(0);

        drive_motor_speed_setpoint.addNewValue(joystick_adjust_Y * joystick_2.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_front.setDirection(-joystick_adjust_X * joystick_1.getX());
    }

    private void carDriveOneJoystick()
    {
        CRM_back.setDirection(0);

        drive_motor_speed_setpoint.addNewValue(joystick_adjust_Y *joystick_1.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_front.setDirection(-joystick_adjust_X * joystick_1.getX());
    }

    private void swerveDrive()
    {
        drive_motor_speed_setpoint.addNewValue(joystick_adjust_Y * joystick_2.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setDirection(joystick_adjust_X * joystick_1.getX());
        CRM_front.setDirection(-joystick_adjust_X * joystick_1.getX());
    }

    private void swerveDriveAutonomous(double spd)
    {
        drive_motor_speed_setpoint.addNewValue(spd);

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setDirection(1);
        CRM_front.setDirection(-1);
    }

    private void swerveDriveOneJoystick()
    {
        drive_motor_speed_setpoint.addNewValue(joystick_adjust_Y * joystick_1.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setDirection(joystick_adjust_X * joystick_1.getX());
        CRM_front.setDirection(-joystick_adjust_X * joystick_1.getX());
    }

    private void translate90_TwoJoystick()
    {
        drive_motor_speed_setpoint.addNewValue(joystick_adjust_Y * joystick_2.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setDirection(joystick_adjust_X);
        CRM_front.setDirection(-joystick_adjust_X);
    }

    private void translate90_OneJoystick()
    {
        drive_motor_speed_setpoint.addNewValue(joystick_adjust_Y * joystick_1.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setDirection(joystick_adjust_X);
        CRM_front.setDirection(-joystick_adjust_X);
    }

    private void swerveRotationDrive()
    {
        drive_motor_speed_setpoint.addNewValue(joystick_adjust_Y * joystick_2.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setDirection(-joystick_adjust_X * joystick_1.getX() * 0.4);
        CRM_front.setDirection(-joystick_adjust_X * joystick_1.getX() * 0.4);
    }

    private void swerveRotationDriveOneJoystick()
    {
        drive_motor_speed_setpoint.addNewValue(joystick_adjust_Y * joystick_1.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setDirection(-joystick_adjust_X * joystick_1.getX() * 0.4);
        CRM_front.setDirection(-joystick_adjust_X * joystick_1.getX() * 0.4);
    }

    /*private void rotate45_TwoJoystick()
    {
        drive_motor_speed_setpoint.addNewValue(joystick_adjust_Y * joystick_2.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        if(joystick_1.getX()<0)
        {
          CRM_back.setWheelDirection(joystick_adjust_X);
          CRM_front.setWheelDirection(joystick_adjust_X);
        }
       else
        {
            CRM_back.setWheelDirection(-joystick_adjust_X);
            CRM_front.setWheelDirection(-joystick_adjust_X);
        }
    }

    private void rotate45_OneJoystick()
    {
        drive_motor_speed_setpoint.addNewValue(joystick_adjust_Y * joystick_1.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        if(joystick_1.getX()<0)
        {
          CRM_back.setWheelDirection(joystick_adjust_X);
          CRM_front.setWheelDirection(joystick_adjust_X);
        }
        else
        {
            CRM_back.setWheelDirection(-joystick_adjust_X);
            CRM_front.setWheelDirection(-joystick_adjust_X);
        }
    }*/


    public void pitSetup()
    {
        CRM_front.setDirection(0);
        CRM_back.setDirection(0);
    }

    public void setPCoeffsDriveSetup()
    {
        //Z-Axis of joystick 1 controls the pCoeffs of the CRMs
        //Z-Axis of joystick 2 controls the minSpeed of the CRMs
        System.out.println("\t\t\t\tPCOEFF FRONT: "+(joystick_1.getZ()+1)/2);
        System.out.println("\t\t\t\tPCOEFF BACK: " + (joystick_2.getZ()+1)/2);
        //CRM_back.setPCoeff((joystick_2.getZ()+1)/2);
        //CRM_front.setPCoeff((joystick_1.getZ()+1)/2);
        //CRM_back.setMinSpeed((joystick_2.getZ()+1)/2);
        //CRM_front.setMinSpeed((joystick_2.getZ()+1)/2);

        drive_motor_speed_setpoint.addNewValue(joystick_adjust_Y * joystick_1.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setDirection(joystick_adjust_X * joystick_1.getX());
        CRM_front.setDirection(-joystick_adjust_X * joystick_1.getX());
    }

    public void setInvertedMotor(boolean m1, boolean m2, boolean m3, boolean m4)
    {
        front_right_motor.setInvertedMotor(m1);
        front_left_motor.setInvertedMotor(m2);
        back_right_motor.setInvertedMotor(m3);
        back_left_motor.setInvertedMotor(m4);
    }

    /*private void translationInit()
    {
        CRM_back.setPotMax(back_pot_max);
        CRM_back.setPotCenter(back_pot_center);
        CRM_back.setPotMin(back_pot_min);

        CRM_front.setPotCenter(front_pot_center);
        CRM_front.setPotMax(front_pot_max);
        CRM_front.setPotMin(front_pot_min);        
    }

    private void rotationInit()
    {
        CRM_back.setPotMax((back_pot_max - back_pot_center)/2 + back_pot_center);
        CRM_back.setPotCenter(back_pot_center);
        CRM_back.setPotMin((back_pot_center - back_pot_min)/2 + back_pot_min);

        CRM_front.setPotMax((front_pot_max - front_pot_center)/2 + front_pot_center);
        CRM_front.setPotCenter(front_pot_center);
        CRM_front.setPotMin((front_pot_center - front_pot_min)/2 + front_pot_min);
    }*/

}
