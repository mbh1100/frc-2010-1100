

package team1100.season2010.robot;

import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.AnalogChannel;

public class ChainRotationMotor
{
      private AverageController CRM_speed_setpoint;
      private double p_coeff;
      private double minSpeed;
      private double maxSpeed;
      private int pot_min = 374;
      private int pot_max = 579;
      private int pot_center = 483;
      private int pot_deadband = 3;
      private AdvJaguar chain_rotation_motor;
      private AnalogChannel pot;
      private AverageController drive_direction_setpoint;

      ChainRotationMotor(int CRM_channel, int avgNum, int pot_channel)
      {
          CRM_speed_setpoint = new AverageController(avgNum);
          chain_rotation_motor = new AdvJaguar(CRM_channel);
          drive_direction_setpoint = new AverageController(avgNum);
          pot = new AnalogChannel(pot_channel);
          p_coeff = 1;
          minSpeed = .5;
          maxSpeed = 1;
      }
      
      public void setMinSpeed(double mS)
      {
          minSpeed = mS;
      }
      
      public void setMaxSpeed(double mS)
      {
          maxSpeed = mS;
      }

      public void setPCoeff(double pcoeff)
      {
          p_coeff = pcoeff;
      }

      public void setInvertedMotor(boolean tf)
      {
          chain_rotation_motor.setInvertedMotor(tf);
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

      public int getPot()
      {
          return pot.getAverageValue();
      }

      public double getPWM()
      {
          return CRM_speed_setpoint.getAverageValue();
      }

      public void setWheelDirection(double position)
      {
        drive_direction_setpoint.addNewValue(position);

        double avg_dir_setpt = ((pot_max - pot_min) / 2) * (drive_direction_setpoint.getAverageValue() + 1) + pot_min;
        double newspeed;

        if(avg_dir_setpt > pot.getAverageValue() + pot_deadband)
            newspeed = p_coeff * (avg_dir_setpt - pot.getAverageValue())*(maxSpeed - minSpeed)/(pot_max - pot_min) + minSpeed;
        else if (avg_dir_setpt < pot.getAverageValue() - pot_deadband)
            newspeed = p_coeff * (avg_dir_setpt - pot.getAverageValue())*(maxSpeed - minSpeed)/(pot_max - pot_min) - minSpeed;
        else newspeed = 0;

        //System.out.println("\t\t\tSpeed: " + p_coeff * (avg_dir_setpt - pot.getAverageValue())*(maxSpeed - minSpeed)/(pot_max - pot_min) + minSpeed);

        if(pot.getAverageValue() >= pot_max + pot_deadband)
            newspeed = -minSpeed;
        else if(pot.getAverageValue() <= pot_min - pot_deadband)
            newspeed = minSpeed;

        //System.out.println("\tSpeed sent: " + newspeed + "\t Potval: " + pot.getAverageValue());

        CRM_speed_setpoint.addNewValue(newspeed);
        chain_rotation_motor.set(CRM_speed_setpoint.getAverageValue());
      }

      public void setCenter()
      {
          setWheelDirection(0);
      }

      public boolean atCenter()
      {
          if(pot.getAverageValue() < pot_center + pot_deadband && pot.getAverageValue() > pot_center - pot_deadband)
              return true;
          else return false;
      }

      public void resetArray()
      {
          for(int i=0; i<drive_direction_setpoint.getSize(); i++)
              drive_direction_setpoint.addNewValue(0);
      }

      public void setDirect(double speed)
      {
          chain_rotation_motor.set(speed);
      }
}
