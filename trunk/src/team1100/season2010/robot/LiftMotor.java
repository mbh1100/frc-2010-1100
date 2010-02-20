/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package team1100.season2010.robot;

import edu.wpi.first.wpilibj.Victor;

/**
 *
 * @author team1100
 */
public class LiftMotor
{
    private Victor victor;
    private double speed;

    public LiftMotor()
    {
        this(4,8);
    }

    public LiftMotor(int slot, int channel)
    {
       victor = new Victor(slot,channel);
       speed = .5;
    }

    public void setSpeed(double spd)
    {
        speed = spd;
    }

    public void setInvertedMotor(boolean tf)
    {
        if(tf)
            speed = Math.abs(speed);
        else speed = -1* Math.abs(speed);
    }

    public void up()
    {
        victor.set(speed);
    }

    public void down()
    {
        victor.set(speed);
    }

    public void stop()
    {
        victor.set(0);
    }
}
