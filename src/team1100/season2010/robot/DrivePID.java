/*
 * Class to encapsulate the PID control for the drive motors.
 */

package team1100.season2010.robot;

/**
 *
 * @author mark
 */

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDController;

/**
 *
 * @author mark
 */
public class DrivePID {

    final double kLinearPct = 2.0;
    final double kPidI = 0.001;
    final double kPidD = 0.0;
    final double kInChannelMin = 0.0;
    final double kInChannelMax = 1000.0; // experiment to find this
    final double kOutChannelMin = -1.0;
    final double kOutChannelMax = 1.0;

    PIDSource m_in;
    PIDOutput m_out;
    PIDController m_pid;
    ScaledPIDSource m_scaledIn;

    double m_speedRange = kInChannelMax - kInChannelMin;
    double m_linearPct;
    double m_PidP = 0.1;
    boolean m_reverse;
    boolean m_running = false;
    double m_PidI = kPidI;
    double m_PidD = kPidD;

    /**
     * Construct a DrivePID using default module slots
     * @param inputChannel - channel on the default analog input module
     * connected to the sensing potentiometer for this device.
     * @param outputChannel - channel on the default digital output module
     * connected to the motor controller for this device.
     * @param reverse - invert the output so the motor spins the other way.
     * (when traveling forward (car drive), motors mounted facing left need to
     * spin in the opposite direction from motors facing right.
     */
    public DrivePID(int inputChannel, int outputChannel, boolean reverse)
    {
        this(AnalogChannel.getDefaultAnalogModule(), inputChannel,
                Jaguar.getDefaultDigitalModule(), outputChannel, reverse);
    }

    /**
     * Construct a DrivePID
     * @param inputSlot - cRIO slot hosting the analog input module used for
     * this device.
     * @param inputChannel - channel on the selected analog module connected to
     * the sensing potentiometer for this device.
     * @param outputSlot - cRIO slot hosting the digital output module used for
     * this device.
     * @param outputChannel - channel on the selected digital module connected
     * to the motor controller for this device.
     * @param reverse - invert the output so the motor spins the other way.
     * (when traveling forward (car drive), motors mounted facing left need to
     * spin in the opposite direction from motors facing right.
    */
    public DrivePID(int inputSlot, int inputChannel,
            int outputSlot, int outputChannel, boolean reverse)
    {
        m_reverse = reverse;
        m_in = new AnalogChannel(inputSlot, inputChannel);
        m_scaledIn = new ScaledPIDSource(m_in, m_reverse? 1.0 : -1.0, 0);
        m_out = new Jaguar(outputSlot, outputChannel);
        m_pid = new PIDController(m_PidP, m_PidI, m_PidD, m_in, m_out);
        m_pid.setOutputRange(kOutChannelMin, kOutChannelMax);

        setLinearPct(kLinearPct);
    }

    /**
     * Specify the portion of the input range where the PIDController operates
     * in a linear (output not clipped) fashion. This is a percentage of the
     * entire range of input values; it is not affected by changes to the
     * operating range.
     */
    void setLinearPct(double pct)
    {
       m_linearPct = pct/100;
       // initially compute P so the motor input is linear over the whole input range
       m_PidP = (kOutChannelMax - kOutChannelMin)/(kInChannelMax - kInChannelMin);
       // increase P so the motor input reaches its limit at m_linearPct/2 from the center
       m_PidP /= m_linearPct;
       // update the PIDController
       m_pid.setPID(m_PidP, m_PidI, m_PidD);
    }

    void setI(double i)
    {
        m_PidI = i;
        m_pid.setPID(m_PidP, m_PidI, m_PidD);
    }

    void setD(double d)
    {
        m_PidD = d;
        m_pid.setPID(m_PidP, m_PidI, m_PidD);
    }

    /**
     * Set the target speed. The speed input is mapped onto the
     * operating range of the driving device. An input of 1.0 is top speed in
     * one direction, -1 is top speed in the other direction. Zero is stopped.
     * @param Desired speed, range is -1.0 to +1.0
     */
    void setSpeed(double speed)
    {
        m_pid.setSetpoint(speed * m_speedRange);

        if (!m_running)
        {
            m_pid.enable();
            m_running = true;
        }
    }
}

