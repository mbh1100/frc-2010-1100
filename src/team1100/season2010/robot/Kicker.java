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
  private boolean primeAgain;
  private boolean hardKick;
  private boolean primed;
  private int prev_count;
  private Compressor compressor;
  private Solenoid valve_1_A;
  private Solenoid valve_1_B;
  private Solenoid valve_2_A;
  private Solenoid valve_2_B;
  private Solenoid valve_3_B;
  private Solenoid valve_3_A;
  private DigitalInput limit_switch;
  private final static int TIMING_DELAY = 200;

  private int prime_state;
  private int test_state;


  public Kicker()
  {
    hardKick = true;
    primeAgain = false;
    prime_state = 0;
    test_state = 0;
    prev_count = -TIMING_DELAY;

    valve_1_A = new Solenoid(8,1);
    valve_2_A = new Solenoid(8,3);
    valve_3_A = new Solenoid(8,5);
    valve_1_B = new Solenoid(8,2);
    valve_2_B = new Solenoid(8,4);
    valve_3_B = new Solenoid(8,6);

    limit_switch = new DigitalInput(4,4);

    if(!limit_switch.get())
       {
         primed = true;
         //System.out.println("limit_switch true");//added by alex to test on saturday
       }
    else
       {
         primed = false;
         //System.out.println("limit_switch falsen (1)");//added by alex to test on saturday
       }

    compressor = new Compressor(4,5,4,1);
    compressor.start();
    primeKicker();
  }

  public void turnOnKicker()
  {
      compressor.start();
      prev_count = -TIMING_DELAY;
  }

  public void startPriming()
    {
      if (!primed)
          primeAgain = true;
    }

  public boolean isReady()
    {
      return primed;
    }

  public void setHardSoft(boolean kickHard)
  {
    //hard = true, soft = false
    hardKick = kickHard;

    if(primed)
      if(hardKick)
      {
        valve_1_A.set(false);
        valve_1_B.set(true);
        valve_3_A.set(false);
        valve_3_B.set(true);
      }
      else
      {
        valve_1_A.set(false);
        valve_1_B.set(true);
        valve_3_B.set(false);
        valve_3_A.set(true);
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
      if(prime_state == 0)
      {
        valve_2_B.set(false);
        valve_2_A.set(true);
        System.out.println("open latch");
        if (primeAgain)
            prime_state++;
      }
      //ARM KICKER
      else if(prime_state == 1)
      {
        if(limit_switch.get())
        {
          valve_1_B.set(false);
          valve_1_A.set(true);
          System.out.println("arm kicker");
          prime_state++;
        }
      }
      else if(prime_state == 2)
      {
          if(!limit_switch.get())
          {
            System.out.println("kicker armed");
            prime_state++;
          }
      }

      //LOCK LATCH
      else if(prime_state == 3)
      {
        valve_2_A.set(false);
        valve_2_B.set(true);
        prime_state++;
        System.out.println("lock latch");
      }

      //SET KICKER
      else if(prime_state == 4)
      {
          System.out.println("set kicker");
        if(hardKick)
        {
          valve_1_A.set(false);
          valve_1_B.set(true);
          valve_3_A.set(false);
          valve_3_B.set(true);
        }
        else
        {
            valve_1_A.set(false);
            valve_1_B.set(true);
            valve_3_B.set(false);
            valve_3_A.set(true);
        }
          System.out.println("kicker set");
        primed = true;
        prime_state = 0;
        primeAgain = false;
      }

    }
    if(limit_switch.get())
       {
          primed = false;
          //System.out.println("limit_switch false (2)");//added by alex to test on saturday
       }
  }

  public void kick(int curr_count)
  {
    //System.out.println("KICK!" + curr_count);
    if(primed == true && curr_count - prev_count >= TIMING_DELAY)
    {
        System.out.println("kick!");
        
        valve_2_B.set(false);
        valve_2_A.set(true);

        // going to state 1 repeats the valve setting, but
        // adds delay so we don't test the limit switch too soon.
        prime_state = 0;
 
        primed = false;
        prev_count = curr_count;
    }
    else
    {
        //System.out.println("ERR: Kick without prime or before 2 seconds.");
        //System.out.println("m_count = " + curr_count + "; prevCount = " + prev_count + "; primed = " + primed);
    }
  }

  public void disarm()
  {
    compressor.stop();
  }

  public void testSolenoidSignal()
  {
    if(test_state == 0)
    {
      valve_2_B.set(false);
      valve_2_A.set(true);
      //System.out.println("Latch piston rod extended.");
      test_state++;
    }
    else if(test_state == 1)
    {
      valve_3_B.set(false);
      valve_1_B.set(false);
      valve_1_A.set(true);
      //System.out.println("Kicker piston rod extended.");
      test_state++;
    }
    else if(test_state == 2)
    {
      valve_1_A.set(false);
      valve_3_B.set(true);
      valve_1_B.set(true);
      //System.out.println("Kicker piston rod retracted.");
      test_state++;
    }
    else if(test_state == 3)
    {
      valve_2_A.set(false);
      valve_2_B.set(true);
      //System.out.println("Latch piston rod retracted.");
      test_state = 0;
    }
  }

  public void testSensors()
  {

  }
}