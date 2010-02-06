/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package team1100.season2010.robot;

import edu.wpi.first.wpilibj.Jaguar;

/**
 *
 * @author team1100
 */
public class AdvJaguar extends Jaguar
{
    private boolean inverted;

    public AdvJaguar(int slot, int channel, boolean inv)
    {
        super(slot, channel);
        inverted = inv;
    }

    public AdvJaguar(int slot, int channel)
    {
        this(slot,channel,false);
    }

    public AdvJaguar(int channel)
    {
        this(channel,false);
    }

    public AdvJaguar(int channel, boolean inv)
    {
        super(channel);
        inverted = inv;
    }

    public void setInvertedMotor(boolean tf)
    {
        inverted = tf;
    }

    public void set(double setspeed)
    {
        if(inverted)
            super.set(-setspeed);
        else super.set(setspeed);

    }

}
