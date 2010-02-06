

package team1100.season2010.robot;

import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.AnalogChannel;

public class ChainRotationMotor
{
      private AverageController CRM_speed_setpoint;
      private int pot_min = 374;
      private int pot_max = 579;
      private int pot_center = 483;
      private int pot_deadband = 10;
      private double CRM_speed;
      private Jaguar chain_rotation_motor;
      private AnalogChannel pot_1 = new AnalogChannel(7);
      private AverageController drive_direction_setpoint;

      ChainRotationMotor(int CRM_channel, int avgNum)
      {
          CRM_speed_setpoint = new AverageController(avgNum);
          CRM_speed = .2;
          chain_rotation_motor = new Jaguar(CRM_channel);
          drive_direction_setpoint = new AverageController(avgNum);
      }

      public void setCRMSpeed(double CRM_speed)
      {
        this.CRM_speed = CRM_speed;
      }

      public void setPotMin(int potMin)
      {
          pot_min = potMin;
      }

      public void setPotMax(int potMax)
      {
          pot_max = potMax;
      }

      public void setPotCenter(int potCenter)
      {
          pot_center = potCenter;
      }

      public void setPotDeadband(int potDeadband)
      {
          pot_deadband = potDeadband;
      }

      public void setWheelDirection(double position)
      {
        drive_direction_setpoint.addNewValue(position);

        double avg_dir_setpt = ((pot_max - pot_min) / 2) * (drive_direction_setpoint.getAverageValue() + 1) + pot_min;
        double newspeed;

        if(avg_dir_setpt > pot_1.getAverageValue() + pot_deadband)
            newspeed = -CRM_speed;
        else if (avg_dir_setpt < pot_1.getAverageValue() - pot_deadband)
            newspeed = CRM_speed;
        else newspeed = 0;

        if(pot_1.getAverageValue() >= pot_max + pot_deadband)
            newspeed = -CRM_speed;
        else if(pot_1.getAverageValue() <= pot_min - pot_deadband)
            newspeed = CRM_speed;

        CRM_speed_setpoint.addNewValue(newspeed);
        chain_rotation_motor.set(CRM_speed_setpoint.getAverageValue());
      }

      public void setCenter()
      {
          setWheelDirection(pot_center);
      }

      public boolean atCenter()
      {
          if(pot_1.getAverageValue() < pot_center + pot_deadband && pot_1.getAverageValue() > pot_center - pot_deadband)
              return true;
          else return false;
      }
}
