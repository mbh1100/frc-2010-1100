/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package team1100.season2010.robot;

/**
 *
 * @author mark
 */
class ScaledPIDSource implements edu.wpi.first.wpilibj.PIDSource
{
    edu.wpi.first.wpilibj.PIDSource m_src;
    double m_scale;
    double m_bias;
    ScaledPIDSource(edu.wpi.first.wpilibj.PIDSource source, double scale, double bias)
    {
        m_src = source;
        m_scale = scale;
        m_bias = bias;
    }
    public double pidGet()
    {
        return m_src.pidGet() * m_scale + m_bias;
    }

    void setScale(double scale)
    {
        m_scale = scale;
    }

    void setBias(double bias)
    {
        m_bias = bias;
    }
};
