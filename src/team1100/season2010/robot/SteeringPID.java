
package team1100.season2010.robot;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDController;

/**
 *
 * @author mark
 */
public class SteeringPID {

    final double kLinearPct = 2.0;
    final double kPidI = 0.0;
    final double kPidD = 0.0;
    final int kOperatingRangePct = 20;
    final int kCenterPct = 50;
    final double kInChannelMin = 0.0;
    final double kInChannelMax = 1000.0;
    final double kInWidth = kInChannelMax - kInChannelMin;
    final double kOutChannelMin = -1.0;
    final double kOutChannelMax = 1.0;

    PIDSource m_in;
    PIDOutput m_out;
    PIDController m_pid;
    PIDOutput m_scaledOut;

    int m_opRangePct = 20;
    double m_operatingRange;
    double m_rangeCenterPct = 50.0;
    double m_rangeCenter;
    double m_linearPct;
    double m_PidP;
    boolean m_running = false;
    double m_PidI = kPidI;
    double m_PidD = kPidD;
    double m_initialPotVal;

    /**
     * Construct a SteeringPID using default module slots
     * @param inputChannel - channel on the default analog input module 
     * connected to the sensing potentiometer for this device.
     * @param outputChannel - channel on the default digital output module
     * connected to the motor controller for this device.
     * @param invertOutput - invert the polarity of the output value.
     */

    public SteeringPID(int inputChannel, int outputChannel, boolean invertOutput)
    {
        this(AnalogChannel.getDefaultAnalogModule(), inputChannel,
                Jaguar.getDefaultDigitalModule(), outputChannel,
                invertOutput);
    }

    /**
     * Construct a SteeringPID
     * @param inputSlot - cRIO slot hosting the analog input module used for
     * this device.
     * @param inputChannel - channel on the selected analog module connected to
     * the sensing potentiometer for this device.
     * @param outputSlot - cRIO slot hosting the digital output module used for
     * this device.
     * @param outputChannel - channel on the selected digital module connected
     * to the motor controller for this device.
     * @param invertOutput - invert the polarity of the output value.
     */
    public SteeringPID(int inputSlot, int inputChannel,
            int outputSlot, int outputChannel, boolean invertOutput)
    {
        m_in = new AnalogChannel(inputSlot, inputChannel);
        m_out = new Jaguar(outputSlot, outputChannel);
        m_scaledOut = new PIDOutputInverter(m_out, invertOutput);
        m_pid = new PIDController(m_PidP, m_PidI, m_PidD, m_in, m_scaledOut);
        m_pid.setOutputRange(kOutChannelMin, kOutChannelMax);
        m_initialPotVal = m_in.pidGet();
        setOperatingRangePct(kOperatingRangePct);
        setCenterPct(kCenterPct);
        setLinearPct(kLinearPct);
    }

    public double getInitialPositionPct()
    {
        return 100 * (m_initialPotVal - kInChannelMin)/(kInChannelMax - kInChannelMin);
    }

    public double getPot()
    {
        return m_in.pidGet();
    }

    public double getCtr()
    {
        return m_rangeCenterPct;
    }
    /**
     * Specify the portion of the input range where the PIDController operates
     * in a linear (output not clipped) fashion. This is a percentage of the
     * entire range of input values; it is not affected by changes to the
     * operating range.
     */
    public void setLinearPct(double pct)
    {
       m_linearPct = pct/100;
       // initially compute P so the motor input is linear over the whole input range
       m_PidP = (kOutChannelMax - kOutChannelMin)/(m_operatingRange);
       // increase P so the motor input reaches its limit at m_linearPct/2 from the center
       m_PidP /= (pct/m_opRangePct);
       m_PidI = m_PidP/20;
       // update the PIDController
       m_pid.setPID(m_PidP, m_PidI, m_PidD);
    }
    
    /**
     * Specify the portion of the input range in use.
     * @param width - percent of input range to use
     */
    public void setOperatingRangePct(int widthPct)
    {
        // ignore invalid input
        if (widthPct > 100 || widthPct < 0) return;
        if (m_rangeCenterPct + widthPct/2 > 100) return;
        if (m_rangeCenterPct - widthPct/2 < 0) return;

        m_opRangePct = widthPct;
        m_operatingRange = kInWidth * widthPct / 100;
    }

    /**
     * Specify the midpoint of the operating range as a percent of the input range.
     * @param centerPct - the target value, as a percent of the input range, when
     * the direction is centered (direction is 0);
     *
     */
    public void setCenterPct(double centerPct)
    {
        // ignore invalid input
        if (centerPct > 100 || centerPct < 0) return;
        if (centerPct + m_opRangePct/2 > 100) return;
        if (centerPct - m_opRangePct/2 < 0) return;

        m_rangeCenterPct = centerPct;
        m_rangeCenter = kInWidth * centerPct / 100.0;
    }

    public void setI(double i)
    {
        m_PidI = i;
        m_pid.setPID(m_PidP, m_PidI, m_PidD);
    }

    public void setD(double d)
    {
        m_PidD = d;
        m_pid.setPID(m_PidP, m_PidI, m_PidD);
    }

    /**
     * Set the steering direction. The direction input is mapped onto the
     * operating range of the steering device. When the specified direction
     * is zero, the steering device will find the specified center.
     * @param Desired steering direction, range is -1.0 to +1.0
     */
    public void setDirection(double direction)
    {
        m_pid.setSetpoint((direction * m_operatingRange/2) + m_rangeCenter);

        if (!m_running)
        {
            m_pid.enable();
            m_running = true;
        }

        /*
          System.out.println("PID Error: " + m_pid.getError() +
                    "; Result: " + m_pid.get() +
                    "; Setpoint: "  + m_pid.getSetpoint() +
                    "; Joystick: " + direction +
                    "; Input: " + m_in.pidGet() +
                    "; P: " + m_pid.getP() +
                    "; I: " + m_pid.getI() +
                    "; D: " + m_pid.getD() +
                    "; width: " + m_operatingRange +
                    "; center: " + m_rangeCenter);
         */
         

    }
}
