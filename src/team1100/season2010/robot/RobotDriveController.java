
package team1100.season2010.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Victor;

public class RobotDriveController
{
    private int drive_type;
    public Joystick joystick_1;
    public Joystick joystick_2;
    private Jaguar front_right_motor;
    private Jaguar front_left_motor;
    private Jaguar back_right_motor;
    private Jaguar back_left_motor;
    private int motor_direction_adjust;

    private int FRM_channel;
    private int FLM_channel;
    private int BRM_channel;
    private int BLM_channel;

    private RobotDrive tank_drive;

    private AverageController drive_motor_speed_setpoint;

    private ChainRotationMotor CRM;


    public RobotDriveController()
    {
        this(0,1,2,1,2,3,4,5,10);
    }

    
    public RobotDriveController(int type, int j1_channel, int j2_channel, int frm_channel,
                                int flm_channel, int brm_channel, int blm_channel, int CRM_channel,
                                int avgNum)
    {
        drive_type = type;

        joystick_1 = new Joystick(j1_channel);
        joystick_2 = new Joystick(j2_channel);

        front_right_motor = new Jaguar(frm_channel);
        FRM_channel = frm_channel;
        front_left_motor = new Jaguar(flm_channel);
        FLM_channel = flm_channel;
        back_right_motor = new Jaguar(brm_channel);
        BRM_channel = brm_channel;
        back_left_motor = new Jaguar(blm_channel);
        BLM_channel = blm_channel;

        //chain_rotation_motor = new Jaguar(CRM_channel);

        CRM = new ChainRotationMotor(CRM_channel, avgNum);

        tankInit();
        carInit(avgNum);
        swerveInit();
    }

    /*Sets drive type (tank, swerve, car...)
     * @param type : 0 = tank drive, 1 = car drive, 2 = swerve drive
     * */
    public void setDriveType(int type)
    {
        drive_type = type;
    }

    public void drive()
    {
        if     (drive_type == 0)
            tankDrive();
        else if(drive_type == 1)
            carDrive();
        else if(drive_type == 2)
            swerveDrive();
    }

    private void tankDrive()
    {
        if(!CRM.atCenter())
            CRM.setCenter();
        tank_drive.tankDrive(joystick_1, joystick_2);
    }

    private void carDrive()
    {
        drive_motor_speed_setpoint.addNewValue(joystick_2.getY());

        front_right_motor.set(motor_direction_adjust * drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(motor_direction_adjust * drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(motor_direction_adjust * drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(motor_direction_adjust * drive_motor_speed_setpoint.getAverageValue());

        CRM.setWheelDirection(joystick_1.getX());

    }

    private void swerveDrive()
    {

    }

    private void tankInit()
    {
        tank_drive = new RobotDrive(FLM_channel, BLM_channel, FRM_channel, BRM_channel);
    }

    private void carInit(int avgNum)
    {
        drive_motor_speed_setpoint = new AverageController(avgNum);
    }

    private void swerveInit()
    {

    }

}
