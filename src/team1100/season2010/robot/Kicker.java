/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package team1100.season2010.robot;

/**
 *
 * @author team1100
 */

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;

//deal with time between commands by putting in lower Hz zone.
public class Kicker
{
  private boolean hardKick;
  private boolean primed;
  private Compressor compressor;
  private Solenoid valve_1_A;
  private Solenoid valve_1_B;
  private Solenoid valve_2_A;
  private Solenoid valve_2_B;
  private Solenoid valve_3_B;
  private Solenoid valve_3_A;
  private DigitalInput latch_mount;
  private DigitalInput latch_piston;
  private DigitalInput kick_mount;
  private DigitalInput kick_piston;

  private int prime_state;
  private int kick_state;
  private int test_state;


  public Kicker()
  {
    hardKick = true;
    primed = false;
    prime_state = 0;
    kick_state = 0;
    test_state = 0;

    valve_1_A = new Solenoid(8,1);
    valve_2_A = new Solenoid(8,3);
    valve_3_A = new Solenoid(8,5);
    valve_1_B = new Solenoid(8,2);
    valve_2_B = new Solenoid(8,4);
    valve_3_B = new Solenoid(8,6);

    latch_mount = new DigitalInput(4,4);
    latch_piston = new DigitalInput(4,3);
    kick_mount = new DigitalInput(4,2);
    kick_piston = new DigitalInput(4,1);

    compressor = new Compressor(4,5,4,1);
    depressurize();
    compressor.start();
    primeKicker();
  }

  public void setHardSoft(boolean hard_or_soft)
  {
    //hard = true, soft = false
    hardKick = hard_or_soft;

    if(primed)
      if(hardKick)
      {
        valve_1_B.set(true);
        valve_3_B.set(true);
      }
      else
      {
        valve_1_B.set(false);
        valve_3_B.set(false);
      }
  }

  public boolean getHardSoft()
  {
    return hardKick;
  }

  public void primeKicker()
  {
    if(!primed)
    {
      //DEPRESSURIZE FIRST
      if(prime_state == 0)
      {
        depressurize();
      }

      //OPEN LATCH
      else if(prime_state == 1)
      {
        valve_2_B.set(false);
        prime_state++;
      }
      else if(prime_state == 2)
      {
        valve_2_A.set(true);
        prime_state++;
      }
      else if(prime_state == 3)
      {
        if(!latch_piston.get())
          prime_state++;
      }

      //ARM KICKER
      else if(prime_state == 4)
      {
        valve_1_B.set(false);
        valve_3_B.set(false);
        prime_state++;
      }
      else if(prime_state == 5)
      {
        valve_1_A.set(true);
        prime_state++;
      }
      else if(prime_state == 6)
      {
        if(!kick_piston.get())
          prime_state++;
      }

      //LOCK LATCH
      else if(prime_state == 7)
      {
        valve_2_A.set(false);
        prime_state++;
      }
      else if(prime_state == 8)
      {
        valve_2_B.set(true);
      }

      //SET KICKER
      else if(prime_state == 9)
      {
        valve_1_A.set(false);
        prime_state++;
      }
      else if(prime_state == 10)
      {
        if(hardKick)
        {
          valve_1_B.set(true);
          valve_3_B.set(true);
        }
        primed = true;
      }
    }


  }

  public void kick()
  {
    if(primed == true)
    {
      if(kick_state == 0)
      {
        /*if(kickHard)
          {
            valve_1_B.set(true);
            valve_3_B.set(true);
          }*/

        valve_2_B.set(false);
        kick_state++;
      }

      else if(kick_state == 1)
      {
        valve_2_A.set(true);
        kick_state++;
      }

      else if(kick_state == 2)
      {
        primed = false;
        prime_state = 0;
        kick_state = 0;
      }
    }
    else System.out.println("ERR: Kick without prime.");
  }

  private void depressurize()
  {
    valve_1_A.set(false);
    valve_1_B.set(false);
    valve_2_A.set(false);
    valve_2_B.set(false);
    valve_3_A.set(false);
    valve_3_B.set(false);
  }

  public void disarm()
  {
    compressor.stop();
    depressurize();
  }

  public void testSolenoidSignal()
  {
    if(test_state == 0)
    {//depressurize
      depressurize();
      test_state++;
    }
    else if(test_state == 1)
    {//extend kicker rod
      valve_3_B.set(false);
      valve_1_B.set(false);
      test_state++;
    }
    else if(test_state == 2)
    {
      valve_1_A.set(true);
      System.out.println("Kicker piston rod extended.");
      test_state++;
    }
    else if(test_state == 3)
    {//retract kicker rod
      valve_1_A.set(false);
      test_state++;
    }
    else if(test_state == 4)
    {
      valve_3_B.set(true);
      valve_1_B.set(true);
      System.out.println("Kicker piston rod retracted.");
      test_state++;
    }
    else if(test_state == 5)
    {//extend latch rod
      valve_2_B.set(false);
      test_state++;
    }
    else if(test_state == 6)
    {
      valve_2_A.set(true);
      System.out.println("Latch piston rod extended.");
      test_state++;
    }
    else if(test_state == 7)
    {//retract latch rod
      valve_2_A.set(false);
      test_state++;
    }
    else if(test_state == 8)
    {
      valve_2_B.set(true);
      System.out.println("Latch piston rod retracted.");
      test_state = 0;
    }
  }

  public void testSensors()
  {

  }
}