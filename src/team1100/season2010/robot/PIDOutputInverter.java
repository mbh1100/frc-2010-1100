/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package team1100.season2010.robot;

import edu.wpi.first.wpilibj.PIDOutput;

/**
 *
 * @author mark
 */
public class PIDOutputInverter implements PIDOutput
{
    double m_scale = 1.0;
    PIDOutput m_out;
    public PIDOutputInverter(PIDOutput outDevice, boolean invert)
    {
        m_out = outDevice;
        if (invert) m_scale = -1.0;
    }

    public void pidWrite(double outval)
    {
        m_out.pidWrite(outval * m_scale);
    }
}
