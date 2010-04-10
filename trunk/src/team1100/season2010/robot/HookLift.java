/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package team1100.season2010.robot;

import edu.wpi.first.wpilibj.Solenoid;

/**
 *
 * @author team1100
 */
public class HookLift {
    private AdvJaguar lift_jaguar; //the winch
    private Solenoid hook_piston; //the hook on the end of the arm
    private Solenoid arm_piston; //the solenoid pushing the entire arm
    private boolean arm_dropped;
    private boolean hook_dropped;
    
    public HookLift()
    {
        //Default constructor
        this(4,8);
    }

    public HookLift(int slot, int channel)
    {
       lift_jaguar = new AdvJaguar(slot,channel);
       hook_piston = new Solenoid(8,7);
       arm_piston = new Solenoid(8,8);
       arm_dropped = false;
       hook_dropped = false;
       //raiseArm();
       //liftHook();
    }
    
    public void moveWinch(double speed)
    {
        lift_jaguar.set(speed);
    }

    public void stopWinch()
    {
        lift_jaguar.set(0);
    }

    public void dropHook()
    {
        hook_piston.set(false);
        hook_dropped = true;
    }
    public void liftHook()
    {
        // return hook to starting position
        hook_piston.set(true);
        hook_dropped = false;
    }
    
    public void dropArm()
    {
        arm_piston.set(true);
        arm_dropped = true;
    }

    public boolean isArmDropped()
    {
        return arm_dropped;
    }

    public boolean isHookDropped()
    {
        return hook_dropped;
    }

    public void raiseArm()
    {
        arm_piston.set(false);

    }
}
