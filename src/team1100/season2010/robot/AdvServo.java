/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package team1100.season2010.robot;

import edu.wpi.first.wpilibj.Servo;

/**
 *
 * @author team1100
 */
public class AdvServo extends Servo
{

  private double servoSetpoint;
  private double increment;
  private double servo_max;
  private double servo_min;

  public AdvServo()
  {
      this(4,10);
  }

  public AdvServo(int slot, int channel)
  {
      super(slot,channel);
      servoSetpoint = .5;
      super.set(servoSetpoint);
      increment = .02;
      servo_max = .8;
      servo_min = .2;
  }

  public void set(boolean direction)  //true = forward, false = backward
  {
      if(direction && servoSetpoint + increment < servo_max)
      {
          servoSetpoint += increment;
          super.set(servoSetpoint);
      }
      if(!direction && servoSetpoint - increment > servo_min)
      {
          servoSetpoint -= increment;
          super.set(servoSetpoint);
      }
  }

  public void free()
  {
      super.free();
  }
}
