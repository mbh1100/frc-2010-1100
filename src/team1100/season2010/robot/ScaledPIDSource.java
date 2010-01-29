/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package team1100.season2010.robot;

/**
 *
 * @author mark
 */
public class ScaledPIDSource implements edu.wpi.first.wpilibj.PIDSource
{
    edu.wpi.first.wpilibj.AnalogChannel m_src;
    double m_scale;
    ScaledPIDSource(edu.wpi.first.wpilibj.AnalogChannel source, double scale)
    {
        m_src = source;
        m_scale = scale;
    }
    public double pidGet()
    {
        return m_src.pidGet() * m_scale;
    }
};
