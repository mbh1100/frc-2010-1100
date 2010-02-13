/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package team1100.season2010.proto;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;
import java.util.Timer;

/**
 * Represents the single compressor on the robot.
 * Once started, runs until the pressure limit is reached.
 * @author mark
 */
public class Compressor {

    final static int kSwitchChannel = 5;
    final static int kCompressorMotorChannnel = 3;
    final static int kStartDelay = 10;
    final static int kCheckPeriod = 100;
    final static int kShutdownDelay = 1000;

    /**
     * Get the singleton instance of the compressor
     * @return the one and only compressor object
     */
    public static synchronized Compressor instance()
    {
        if (theInstance == null)
        {
            System.out.println("Creating Compressor");
            theInstance = new Compressor(kSwitchChannel, kCompressorMotorChannnel);
        }
        return theInstance;
    }

    /**
     * start the compressor running. Multiple calls are harmless.
     */
    public synchronized void start()
    {
        if (m_isStarted)
            return;
        m_isStarted = true;
        m_ready = false;

        m_timer.schedule(new CompressorTask(this), kStartDelay);
    }

    /**
     * stop the compressor. Immediately.
     */
    public void stop()
    {
        m_timer.cancel();
        m_isRunning = false;
        m_compressorOn.set(Relay.Value.kOff);
        m_isStarted = false;
    }

    /**
     * See if operating pressure has been reached. Since there's no
     * sensor on the output pressure, we become ready the first time
     * we hit the pressure limit. If we drain the pressure faster than the
     * compressor can make it, this won't tell us.
     * @return
     */
    public boolean isPressureReady() { return m_ready; }

    /**
     * Constructor
     * @param pressureSwitchChannel - digital input channel for the pressure
     * switch
     * @param spikeChannel - digital output channel for the relay that runs
     * the compressor
     */
    private Compressor(int pressureSwitchChannel, int spikeChannel)
    {
        m_pressureSwitch = new DigitalInput(pressureSwitchChannel);
        m_compressorOn = new Relay(spikeChannel);

        m_timer = new Timer();
    }

    private static Compressor theInstance;
    private DigitalInput m_pressureSwitch;
    private Relay m_compressorOn;
    private boolean m_isRunning = false;
    private boolean m_isStarted = false;
    private boolean m_limitDetected = false;
    private boolean m_ready = false;
    private java.util.Timer m_timer;

    private class CompressorTask extends java.util.TimerTask
    {
        private Compressor m_controller;

        public CompressorTask(Compressor controller) {
            if (controller == null) {
                throw new NullPointerException("Given Compressor was null");
            }
            m_controller = controller;
        }

        public void run() {
            m_controller.check(this);
        }
    }

    /**
     * If the compressor isn't running, but it could, start it
     * If the compressor is running but has reached the pressure limit,
     * stop it when we get a chance.
     * @param the task that called us. We'll always reschedule it.
     */
    private void check(CompressorTask task)
    {
        if (m_pressureSwitch.get() && !m_isRunning)
        {
            // we're not running and we should - turn on
            System.out.println("Turn on compressor");
            m_isRunning = true;
            m_compressorOn.set(Relay.Value.kOn);
            m_limitDetected = false;
        }
        else if (!m_pressureSwitch.get() && m_isRunning)
        {
            // we are running and we should stop.
            if (!m_limitDetected)
            {
                System.out.println("Limit reached");
                // eventually.
                m_limitDetected = true;
                m_timer.schedule(task, kShutdownDelay);
                // only schedule one future task.
                return;
            }
            else
            {
                System.out.println("Turn off compressor");
                // well, alright.
                m_ready = true;
                m_isRunning = false;
                m_compressorOn.set(Relay.Value.kOff);
            }
        }

        // check again soon
        m_timer.schedule(task, kCheckPeriod);
    }    
}
