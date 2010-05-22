/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package team1100.season2010.robot;


import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Victor;

/**
 *
 * @author team1100
 */
public class Lift
{
    private Victor lift_winch;
    private Solenoid lock_piston; //spike
    private Solenoid attach_piston;
    //private Solenoid attach_mount;
    private boolean inverted;

    public Lift()
    {
        this(4,8);
    }

    public Lift(int slot, int channel)
    {
       lift_winch = new Victor(slot,channel);
       lock_piston = new Solenoid(8,8);
       attach_piston = new Solenoid(8,7);
       //attach_mount = new Solenoid(8,8);
       inverted = false;
    }

    public void setInvertedMotor(boolean tf)
    {
        inverted = tf;
    }

    public void move(double speed)
    {
        if(inverted)
            lift_winch.set(-speed);
        else lift_winch.set(speed);
    }

    public void stop()
    {
        lift_winch.set(0);
    }

    public void lock(boolean tf)
    {
        if(tf)
            lock_piston.set(false); 
        else
            lock_piston.set(true); 
    }

    public void attach(boolean tf)
    {
        if(tf)
        {
            //attach_mount.set(false);
            attach_piston.set(true);
        }
        else
        {
            attach_piston.set(false);
            //attach_mount.set(true);
        }
    }
}
