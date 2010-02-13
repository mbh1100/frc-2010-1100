
package team1100.season2010.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Victor;

public class RobotDriveController
{
    private int drive_type;
    private int joystick_type;
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

    //private RobotDrive tank_drive;

    private AverageController drive_motor_speed_setpoint;

    private ChainRotationMotor CRM_back;
    private ChainRotationMotor CRM_front;


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

        CRM_back = new ChainRotationMotor(CRM_back_channel, avgNum, pot_back_channel);
        CRM_front = new ChainRotationMotor(CRM_front_channel, avgNum, pot_front_channel);

        translationInit();
    }

    /*Sets drive type (tank, swerve, car...)
     * @param type : 0 = tank drive, 1 = car drive, 2 = swerve drive, 3 = swerve rotation, 4 = diagnostic
     * */
    public void setDriveType(int type)
    {
        drive_type = type;
        if(drive_type == 2)
            translationInit();
        if(drive_type == 3)
            rotationInit();
    }

    //@param type: 0 = one joystick mode, 1 = two joystick mode
    public void setJoystickType(int type)
    {
        joystick_type = type;
    }

    public void change90Mode()
    {
        if(drive_type == 22)
            drive_type = 2;
        else drive_type = 22;
        translationInit();
    }

    public void change45Mode()
    {
        if(drive_type == 33)
            drive_type = 3;
        else drive_type = 33;
        rotationInit();
    }

    public String getPotVals()
    {
        return "CRM Front: " + CRM_front.getPot() + "\n\tCRM Back: " + CRM_back.getPot() + "\n";
    }

    public void drive()
    {
        //if     (drive_type == 0)
        //    tankDrive();
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
          else if(drive_type == 33)
              rotate45_TwoJoystick();
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
          else if(drive_type == 33)
              rotate45_OneJoystick();
        }
    }

    private void diagnostic()
    {

        System.out.println("CRM front: " + CRM_front.getPot());
        System.out.println("\tCRM back: " + CRM_back.getPot());

        if(joystick_1.getX()>.4)
          CRM_back.setDirect(.2);
        else if(joystick_1.getX()<-.4)
          CRM_back.setDirect(-.2);
        else CRM_back.setDirect(0);
        if(joystick_2.getX()>.4)
          CRM_front.setDirect(.2);
        else if(joystick_2.getX()<-.4)
          CRM_front.setDirect(-.2);
        else CRM_front.setDirect(0);
    }

    private void tankDrive()
    {
        if(!CRM_back.atCenter())
            CRM_back.setCenter();
        if(!CRM_front.atCenter())
           CRM_front.setCenter();
        //tank_drive.tankDrive(joystick_1, joystick_2);
        goTankDrive();
    }

    private void goTankDrive()
    {
        front_right_motor.set(joystick_2.getY());
        front_left_motor.set(joystick_1.getY());
        back_right_motor.set(joystick_2.getY());
        back_left_motor.set(joystick_1.getY());
    }

    private void carDrive()
    {
        if(!CRM_front.atCenter())
           CRM_front.setCenter();

        drive_motor_speed_setpoint.addNewValue(-1* joystick_2.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setWheelDirection(joystick_1.getX());
    }

    private void carDriveOneJoystick()
    {
        if(!CRM_front.atCenter())
           CRM_front.setCenter();

        drive_motor_speed_setpoint.addNewValue(-1* joystick_1.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setWheelDirection(joystick_1.getX());
    }

    private void swerveDrive()
    {
       // System.out.println("\t\t\t\tPCOEFF: "+(joystick_1.getZ()+1)/2);
        //System.out.println("\t\t\t\tPCOEFF: " + (joystick_2.getZ()+1)/2);
       // CRM_back.setPCoeff((joystick_1.getZ()+1)/2);
        //CRM_front.setPCoeff((joystick_2.getZ()+1)/2);

        drive_motor_speed_setpoint.addNewValue(joystick_2.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setWheelDirection(joystick_1.getX());
        CRM_front.setWheelDirection(-joystick_1.getX());
    }

    private void swerveDriveOneJoystick()
    {
       // System.out.println("\t\t\t\tPCOEFF: "+(joystick_1.getZ()+1)/2);
        //System.out.println("\t\t\t\tPCOEFF: " + (joystick_2.getZ()+1)/2);
       // CRM_back.setPCoeff((joystick_1.getZ()+1)/2);
        //CRM_front.setPCoeff((joystick_2.getZ()+1)/2);

        drive_motor_speed_setpoint.addNewValue(joystick_1.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setWheelDirection(joystick_1.getX());
        CRM_front.setWheelDirection(-joystick_1.getX());
    }

    private void translate90_TwoJoystick()
    {
        drive_motor_speed_setpoint.addNewValue(joystick_2.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setWheelDirection(1);
        CRM_front.setWheelDirection(-1);
    }

    private void translate90_OneJoystick()
    {
        drive_motor_speed_setpoint.addNewValue(joystick_1.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setWheelDirection(1);
        CRM_front.setWheelDirection(-1);
    }

    private void swerveRotationDrive()
    {
        //CRM_back.setPCoeff((joystick_1.getZ()+1)/2);
        //CRM_front.setPCoeff((joystick_1.getZ()+1)/2);
        //System.out.println("\t\t\t\tPCOEFF: "+(joystick_1.getZ()+1)/2);
        //System.out.println("\t\t\t\tPCOEFF: " + (joystick_2.getZ()+1)/2);

        drive_motor_speed_setpoint.addNewValue(joystick_2.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setWheelDirection(-joystick_1.getX());
        CRM_front.setWheelDirection(-joystick_1.getX());
    }

    private void swerveRotationDriveOneJoystick()
    {
        //CRM_back.setPCoeff((joystick_1.getZ()+1)/2);
        //CRM_front.setPCoeff((joystick_1.getZ()+1)/2);
        //System.out.println("\t\t\t\tPCOEFF: "+(joystick_1.getZ()+1)/2);
        //System.out.println("\t\t\t\tPCOEFF: " + (joystick_2.getZ()+1)/2);

        drive_motor_speed_setpoint.addNewValue(joystick_1.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setWheelDirection(-joystick_1.getX());
        CRM_front.setWheelDirection(-joystick_1.getX());
    }

    private void rotate45_TwoJoystick()
    {
        drive_motor_speed_setpoint.addNewValue(joystick_2.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        if(joystick_1.getX()<0)
        {
          CRM_back.setWheelDirection(-1);
          CRM_front.setWheelDirection(-1);
        }
        if(joystick_1.getX()>0)
        {
            CRM_back.setWheelDirection(1);
            CRM_front.setWheelDirection(1);
        }
    }

    private void rotate45_OneJoystick()
    {
        drive_motor_speed_setpoint.addNewValue(joystick_1.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        if(joystick_1.getX()<0)
        {
          CRM_back.setWheelDirection(-1);
          CRM_front.setWheelDirection(-1);
        }
        if(joystick_1.getX()>0)
        {
            CRM_back.setWheelDirection(1);
            CRM_front.setWheelDirection(1);
        }
    }

    public void setInvertedMotor(boolean m1, boolean m2, boolean m3, boolean m4)
    {
        front_right_motor.setInvertedMotor(m1);
        front_left_motor.setInvertedMotor(m2);
        back_right_motor.setInvertedMotor(m3);
        back_left_motor.setInvertedMotor(m4);
    }


    private void translationInit()
    {
        CRM_back.setPotMax(548);
        CRM_back.setPotCenter(481);
        CRM_back.setPotMin(401);
        CRM_back.setInvertedMotor(false);
        CRM_back.setMinSpeed(.4);
        CRM_back.setPCoeff(.7);

        CRM_front.setPotCenter(501);
        CRM_front.setPotMax(568);
        CRM_front.setPotMin(432);
        CRM_front.setInvertedMotor(true);
        CRM_front.setMinSpeed(.4);
        CRM_front.setPCoeff(.76);
    }

    private void rotationInit()
    {
        CRM_back.setPotMax(514);
        CRM_back.setPotCenter(481);
        CRM_back.setPotMin(441);
        CRM_back.setInvertedMotor(false);
        CRM_back.setMinSpeed(.4);
        CRM_back.setPCoeff(.7);

        CRM_front.setPotCenter(501);
        CRM_front.setPotMax(534);
        CRM_front.setPotMin(467);
        CRM_front.setInvertedMotor(true);
        CRM_front.setMinSpeed(.4);
        CRM_front.setPCoeff(.76);
    }

}
