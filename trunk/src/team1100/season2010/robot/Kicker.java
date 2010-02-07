/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package team1100.season2010.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;

/**
 * Class representing the robot kicker subsystem
 * @author mark
 */
public class Kicker extends Thread
{
    /**
     * constructor
     */
    public Kicker()
    {
        final int kSlot = 8;
        
        m_hardMode = false;
        m_ready = false;
        m_mainPushValve = new Solenoid(kSlot, kMainPushChannel);
        m_mainPullValve = new Solenoid(kSlot, kMainPullChannel);
        m_latchPushValve = new Solenoid(kSlot, kLatchPushChannel);
        m_latchPullValve = new Solenoid(kSlot, kLatchPullChannel);
        m_mainPullVentValve = new Solenoid(kSlot, kMainPullVentChannel);
        m_mainPullChargeValve = new Solenoid(kSlot, kMainPullEnableChannel);

        m_mainPushedSensor = new DigitalInput(kMainPushedSensorChannel);
        m_mainPulledSensor = new DigitalInput(kMainPulledSensorChannel);
        m_latchPushedSensor = new  DigitalInput(kLatchPushedSensorChannel);
        m_latchPulledSensor = new DigitalInput(kLatchPulledSensorChannel);

        m_running = false;
    }

    public void arm()
    {
        m_running = true;
        this.start();
    }

    public void disarm()
    {
        m_running = false;
    }

    /**
     * constant for hard kick mode
     */
    public final static int kickHard = 0;
    /**
     * constant for soft kick mode
     */
    public final static int kickSoft = 1;

    /**
     * adjust the kicker for short kicks or long kicks.
     * @param hardOrSoft use constant kickHard for long kicks, or kickSoft
     * for short kicks.
     */
    public void setKickMode(int hardOrSoft)
    {
        if (!m_running) return;

        boolean tmpHardMode = (hardOrSoft == kickHard);

        // if nothing changes, we're done.
        if (m_hardMode == tmpHardMode) return;

        m_hardMode = tmpHardMode;
        // if we're not charged and ready to kick, the hard/soft
        // valve won't actuate, because there's no pressure from the 
        // main pull valve. That's OK, we'll properly set the valve, based
        // on m_hardMode, after the kicker is set.
        // There's a race between changing the valves here while
        // the cycling engine is setting them according to the previous mode.
        // We could open both A and B valves at once, leaving the valve
        // in an indeterminate state. The subsequent kick could have the
        // wrong force. It's unlikely that we'll hit this problem; ignore it.
        // If the kick mode is changed while the kicker is ready,
        // there is no risk of a race.
        if (m_hardMode)
        {
            openValve(m_mainPullChargeValve);
        }
        else
        {
            openValve(m_mainPullVentValve);
        }
    }

    /**
     * start the kick cycle, operating the kicker. This won't do anything
     * if the kicker is not ready.
     */
    public void kick()
    {
        if (m_running && m_ready)
            notify();
    }

    /**
     * test if the kicker is ready to kick.
     * @return true if the kicker is ready, false otherwise.
     */
    public boolean isReady() { return m_ready; }

    private boolean m_hardMode = false;
    private boolean m_ready = false;
    private boolean m_running = false;

    private Solenoid m_mainPushValve;
    private Solenoid m_mainPullValve;
    private Solenoid m_latchPushValve;
    private Solenoid m_latchPullValve;
    private Solenoid m_mainPullVentValve;
    private Solenoid m_mainPullChargeValve;

    private DigitalInput m_mainPushedSensor;
    private DigitalInput m_mainPulledSensor;
    private DigitalInput m_latchPushedSensor;
    private DigitalInput m_latchPulledSensor;

    private static final int kMainPushChannel = 1;
    private static final int kMainPullChannel = 2;
    private static final int kMainPullVentChannel = 5;
    private static final int kMainPullEnableChannel = 6;
    private static final int kLatchPushChannel = 3;
    private static final int kLatchPullChannel = 4;
    private static final int kMainPushedSensorChannel = 1;
    private static final int kMainPulledSensorChannel = 2;
    private static final int kLatchPushedSensorChannel = 3;
    private static final int kLatchPulledSensorChannel = 4;

    private static final double kValveOpenPulseWidthS = 0.5;

    /*
     * override Thread.run(). This is called when the thread
     * is started.
     */
    public void run()
    {
        Compressor.instance().start();
        while (!Compressor.instance().isPressureReady())
        {
            Sleep(1000);
        }

        while (m_running)
        {
            // prepare to kick.
            prepareToKick();

            if (!isMainExtended())
            {
                // the latch may have failed. Don't wait in this state,
                // or there will be a penalty (rule <G30> part a)
                openTheLatch();
                pushWithMainCylinder();

                // wait to avoid exceeding the frame perimeter within 
                // the allowed period.
                Sleep(2000);
                continue;
            }

            // The expectation is that it will normally take
            // more than two seconds to extend the main cylinder, so that
            // software measures are not required to prevent kicking more 
            // often than once per two seconds. If the cycle time is faster,
            // add artificial delay here.
            // Sleep(n);

            // wait while the compressor gets ready. Before we say
            // we're ready, be sure there's enough pressure to withdraw
            // the foot after the kick.
            if (!Compressor.instance().isPressureReady())
            {
                Sleep(1000);
                continue;
            }
            
            // set ready flag
            m_ready = true;

            // wait for kick signal
            try
            {
                wait();
            }
            catch (InterruptedException ex)
            {
                System.out.println("*** Interrupted while waiting for kick ***");
                // don't kick
                m_ready = false;
                continue;
            }

            m_ready = false;

            // kick!
            openTheLatch();

            // wait for the cylinder to fully withdraw, but give up soon
            // in case of sensor failure or cylinder jam. We need to start
            // pushing soon to avoid the penalty for being extended more
            // than 2 seconds.
            waitForMainPulled(500);
        }
    }

    /**
     * cycle the latch and charge the kicker according to the mode.
     */
    private void prepareToKick()
    {
        openTheLatch();
        pushWithMainCylinder();
        waitForMainPushed();
        closeTheLatch();
        waitForLatchClosed();
        if (m_hardMode)
            pullWithMainCylinder();
        else
            idleMainCylinder();
    }

    private void openTheLatch()
    {
        if (!isLatchOpen())
            openValve(m_latchPushValve);
    }

    private void waitForLatchOpen()
    {
        while (!isLatchOpen())
        {
            Sleep(50);
        }
    }

    private void closeTheLatch()
    {
        if (!isLatchClosed())
            openValve(m_latchPullValve);
    }

    private void waitForLatchClosed()
    {
        while (!isLatchClosed())
        {
            Sleep(50);
        }
    }

    private void pushWithMainCylinder()
    {
        openValve(m_mainPushValve);
    }

    private void waitForMainPushed()
    {
        while (!isMainExtended())
        {
            Sleep(100);
        }
    }

    private void pullWithMainCylinder()
    {
        openValve(m_mainPullValve);
        openValve(m_mainPullChargeValve);
    }

    private void waitForMainPulled(int maxWaitMs)
    {
        while (!isMainWithdrawn() && maxWaitMs > 0)
        {
            maxWaitMs -= 100;
            Sleep(100);
        }
    }

    private void idleMainCylinder()
    {
        openValve(m_mainPullValve);
        openValve(m_mainPullVentValve);
    }

    private boolean isLatchOpen()
    {
        return m_latchPushedSensor.get();
    }

    private boolean isLatchClosed()
    {
        return m_latchPulledSensor.get();
    }

    private boolean isMainExtended()
    {
        return m_mainPushedSensor.get();
    }

    private boolean isMainWithdrawn()
    {
        return m_mainPulledSensor.get();
    }

    private void openValve(Solenoid digout)
    {
        digout.set(true);
        Sleep(100);
        digout.set(false);
    }

    /**
     * sleep that doesn't throw. Just returns if the
     * sleep is interrupted.
     * @param ms milleseconds to delay
     */
    private void Sleep(long ms)
    {
        try
        {
            sleep(ms);
        }
        catch (InterruptedException ex)
        {
        }
    }

    /**
     * test index values
     */
    private static final int kTestOpenLatch = 1;
    private static final int kTestPullMain = 2;
    private static final int kTestPushMain = 3;
    private static final int kTestMainIdle = 4;
    private static final int kTestCloseLatch = 5;
    private static final int kTestPullLatch = 6;
    private static final int kTestPushLatch = 7;
    private static final int kTestPullCharge = 8;
    private static final int kTestPullVent = 9;
    private static final int kTestMainPull = 10;
    private static final int kTestMainPush = 11;
    private static final int kTestMainPulled = 12;
    private static final int kTestMainPushed = 13;
    private static final int kTestLatchPulled = 14;
    private static final int kTestLatchPushed = 15;
    private static final int kTestPrepareToKick = 16;

    public void test(Joystick joystick)
    {
        if (m_running) return;
        // when we first detect the top button pushed, bump the index
        if (joystick.getTop() && !m_topPushed)
        {
            m_topPushed = true;
            ++m_kickerTestCycle;
            switch (m_kickerTestCycle)
            {
                case 1:
                    System.out.println("Open the latch");
                    m_kickerTest = kTestOpenLatch;
                    break;
                case 2:
                    System.out.println("Close the latch");
                    m_kickerTest = kTestCloseLatch;
                    break;
                case 3:
                    System.out.println("Idle Main");
                    m_kickerTest = kTestMainIdle;
                     break;
                case 4:
                    System.out.println("Pull Main");
                    m_kickerTest = kTestMainPull;
                    break;
                case 5:
                    System.out.println("Push Main");
                    m_kickerTest = kTestMainPush;
                    break;
                case 6:
                    System.out.println("open valve 1A");
                    m_kickerTest = kTestPushMain;
                    break;
                case 7:
                    System.out.println("open valve 1B");
                    m_kickerTest = kTestPullMain;
                    break;
                case 8:
                    System.out.println("open valve 2A");
                    m_kickerTest = kTestPushLatch;
                    break;
                case 9:
                    System.out.println("open valve 2B");
                    m_kickerTest = kTestPullLatch;
                    break;
                case 10:
                    System.out.println("open valve 3A");
                    m_kickerTest = kTestPullVent;
                    break;
                case 11:
                    System.out.println("open valve 3B");
                    m_kickerTest = kTestPullCharge;
                    break;
                case 12:
                    System.out.println("test latch pulled...");
                    m_kickerTest = kTestLatchPulled;
                    break;
                case 13:
                    System.out.println("test latch pushed...");
                    m_kickerTest = kTestLatchPushed;
                    break;
                case 14:
                    System.out.println("test main pulled...");
                    m_kickerTest = kTestMainPulled;
                    break;
                case 15:
                    System.out.println("test main pushed...");
                    m_kickerTest = kTestMainPushed;
                    m_kickerTestCycle = 0;
                    break;
            }
        }

        if (!joystick.getTop())
            m_topPushed = false;

        // when we first detect the trigger pulled, run the test
        if (joystick.getTrigger() && !m_triggerPulled)
        {
            doTest(m_kickerTest);
        }

        if (!joystick.getTrigger())
            m_triggerPulled = false;
    }

    /**
     * run the selected test
     */
    private boolean doTest(int index)
    {
        boolean rval = false;
        switch (index)
        {
            case kTestOpenLatch:
                System.out.println("Opening the latch");
                openTheLatch();
                break;
            case kTestPullMain:
                System.out.println("Pulling with main cylinder");
                pullWithMainCylinder();
                break;
            case kTestPushMain:
                System.out.println("Pushing with main cylinder");
                pushWithMainCylinder();
                break;
            case kTestMainIdle:
                System.out.println("Idling main cylinder");
                idleMainCylinder();
                break;
            case kTestCloseLatch:
                System.out.println("Closing the latch");
                closeTheLatch();
                break;
            case kTestPullLatch:
                System.out.println("opening latch pull (2B)");
                openValve(m_latchPullValve);
                break;
            case kTestPushLatch:
                System.out.println("opening latch push (2A)");
                openValve(m_latchPushValve);
                break;
            case kTestPullCharge:
                System.out.println("opening main pull charge (3B)");
                openValve(m_mainPullChargeValve);
                break;
            case kTestPullVent:
                System.out.println("opening main pull vent (3A)");
                openValve(m_mainPullVentValve);
                break;
            case kTestMainPull:
                System.out.println("opening main pull (1B)");
                openValve(m_mainPullValve);
                break;
            case kTestMainPush:
                System.out.println("opening main push (1A)");
                openValve(m_mainPushValve);
                break;
            case kTestMainPulled:
                rval = isMainWithdrawn();
                System.out.println("main pulled sensor = " + rval);
                break;
            case kTestMainPushed:
                rval = isMainExtended();
                System.out.println("main pushed sensor = " + rval);
                break;
            case kTestLatchPulled:
                rval = isLatchClosed();
                System.out.println("latch pulled sensor = " + rval);
                break;
            case kTestLatchPushed:
                rval = isLatchOpen();
                System.out.println("latch pushed sensor = " + rval);
                break;
            case kTestPrepareToKick:
                System.out.println("Preparing to kick");
                prepareToKick();
                break;
            default:
                System.out.println("Invalid Kicker Test index");
                break;
        }
        return rval;
    }

    boolean m_topPushed = false;
    boolean m_triggerPulled = false;
    int m_kickerTestCycle = 0;
    int m_kickerTest = 0;
}

