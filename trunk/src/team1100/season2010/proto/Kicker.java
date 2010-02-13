/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package team1100.season2010.proto;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor;

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
        m_mainExtendValve = new Solenoid(kSlot, kMainExtendChannel);
        m_mainRetractValve = new Solenoid(kSlot, kMainRetractChannel);
        m_latchExtendValve = new Solenoid(kSlot, kLatchExtendChannel);
        m_latchRetractValve = new Solenoid(kSlot, kLatchRetractChannel);
        m_mainRetractVentValve = new Solenoid(kSlot, kMainRetractVentChannel);
        m_mainRetractChargeValve = new Solenoid(kSlot, kMainRetractEnableChannel);

        m_mainExtendedSensor = new DigitalInput(kMainExtendedSensorChannel);
        m_mainRetractedSensor = new DigitalInput(kMainRetractedSensorChannel);
        m_latchExtendedSensor = new  DigitalInput(kLatchExtendedSensorChannel);
        m_latchRetractedSensor = new DigitalInput(kLatchRetractedSensorChannel);

        m_running = false;
    }

    /**
     * enable the kicker
     */
    public void arm()
    {
        m_running = true;
        this.start();
    }

    /**
     * disable the kicker
     */
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
        // main retract valve. That's OK, we'll properly set the valve, based
        // on m_hardMode, after the kicker is set.
        // There's a race between changing the valves here while
        // the cycling engine is setting them according to the previous mode.
        // Synchronize to close the window between testing the mode and setting
        // the valve.
        synchronized(this)
        {
            if (m_hardMode)
            {
                openValve(m_mainRetractChargeValve);
            }
            else
            {
                openValve(m_mainRetractVentValve);
            }
        }
    }

    /**
     * start the kick cycle, operating the kicker. This won't do anything
     * if the kicker is not ready.
     */
    public void kick()
    {
        if (m_running && m_ready)
        {
            notify();
        }
    }

    /**
     * test if the kicker is ready to kick.
     * @return true if the kicker is ready, false otherwise.
     */
    public boolean isReady() { return m_ready; }

    private boolean m_hardMode = false;
    private boolean m_ready = false;
    private boolean m_running = false;

    private Solenoid m_mainExtendValve;
    private Solenoid m_mainRetractValve;
    private Solenoid m_latchExtendValve;
    private Solenoid m_latchRetractValve;
    private Solenoid m_mainRetractVentValve;
    private Solenoid m_mainRetractChargeValve;

    private DigitalInput m_mainExtendedSensor;
    private DigitalInput m_mainRetractedSensor;
    private DigitalInput m_latchExtendedSensor;
    private DigitalInput m_latchRetractedSensor;

    private static final int kMainExtendChannel = 1;
    private static final int kMainRetractChannel = 2;
    private static final int kMainRetractVentChannel = 5;
    private static final int kMainRetractEnableChannel = 6;
    private static final int kLatchExtendChannel = 3;
    private static final int kLatchRetractChannel = 4;
    private static final int kMainExtendedSensorChannel = 3; //1;
    private static final int kMainRetractedSensorChannel = 2;
    private static final int kLatchExtendedSensorChannel = 3;
    private static final int kLatchRetractedSensorChannel = 4;

    private static final int kValveOpenPulseWidthMs = 100;

    Compressor compressor;

    /*
     * override Thread.run(). This is called when the thread
     * is started.
     */
    public void run()
    {
        // WPI compresor doesn't have a pressure sensor
        // don't start until there is sufficient pressure
        //while (!compressor.isPressureReady())
        //{
        //    Sleep(1000);
        //}
        while (m_running)
        {
            // prepare to kick.
            prepareToKick();

            if (!isMainExtended())
            {
                // the latch may have failed. Don't wait in this state,
                // or there will be a penalty (rule <G30> part a)
                openTheLatch();
                extendWithMainCylinder();

                // wait to avoid exceeding the frame perimeter within
                // the allowed period.
                Sleep(2000);
                continue;
            }

            // Software measures are required to prevent kicking more
            // often than once per two seconds. If the cycle time is faster
            // than 4 seconds (assume two seconds beyond the perimeter plus
            // two seconds before we can exceed the perimeter again),
            // add artificial delay here.
            Sleep(1000);

            // wait while the compressor gets ready. Before we say
            // we're ready, be sure there's enough pressure to withdraw
            // the foot after the kick.
            // The WPI compressor doesn't support a pressure test.
            // if (!Compressor.instance().isPressureReady())
            // {
            //     Sleep(1000);
            //     continue;
            // }

            // ready to go!
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

            // save some air by pushing back as soon as the
            // ball is gone.
            Sleep(200);
            extendWithMainCylinder();
        }
    }

    /**
     * cycle the latch and charge the kicker according to the mode.
     */
    private void prepareToKick()
    {
        if (!isMainExtended())
        {
            openTheLatch();
        }
        extendWithMainCylinder();
        waitForMainExtended();
        closeTheLatch();
        waitForLatchClosed();
        synchronized(this)
        {
            if (m_hardMode)
                retractWithMainCylinder();
            else
                idleMainCylinder();
        }
    }

    private void openTheLatch()
    {
        // if (!isLatchOpen())
            openValve(m_latchExtendValve);
    }

    private void waitForLatchOpen()
    {
        // while (!isLatchOpen())
        // {
        //     Sleep(50);
        // }
        Sleep(100);
    }

    private void closeTheLatch()
    {
        // if (!isLatchClosed())
            openValve(m_latchRetractValve);
    }

    private void waitForLatchClosed()
    {
        // while (!isLatchClosed())
        // {
        //    Sleep(50);
        // }
        Sleep(100);
    }

    private void extendWithMainCylinder()
    {
        openValve(m_mainExtendValve);
    }

    private void waitForMainExtended()
    {
        while (!isMainExtended())
        {
            Sleep(100);
        }
    }

    private void retractWithMainCylinder()
    {
        openValve(m_mainRetractValve);
        openValve(m_mainRetractChargeValve);
    }

    private void waitForMainRetracted(int maxWaitMs)
    {
        // while (!isMainWithdrawn() && maxWaitMs > 0)
        // {
        //    maxWaitMs -= 100;
        //    Sleep(100);
        // }
        Sleep(100);
    }

    private void idleMainCylinder()
    {
        openValve(m_mainRetractValve);
        openValve(m_mainRetractVentValve);
    }

    private boolean isLatchOpen()
    {
        return !m_latchExtendedSensor.get();
    }

    private boolean isLatchClosed()
    {
        return !m_latchRetractedSensor.get();
    }

    private boolean isMainExtended()
    {
        return m_mainExtendedSensor.get();
    }

    private boolean isMainWithdrawn()
    {
        return !m_mainRetractedSensor.get();
    }

    private void openValve(Solenoid digout)
    {
        digout.set(true);
        Sleep(kValveOpenPulseWidthMs);
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
    private static final int kTestRetractMain = 2;
    private static final int kTestExtendMain = 3;
    private static final int kTestMainIdle = 4;
    private static final int kTestCloseLatch = 5;
    private static final int kTestRetractLatch = 6;
    private static final int kTestExtendLatch = 7;
    private static final int kTestRetractCharge = 8;
    private static final int kTestRetractVent = 9;
    private static final int kTestMainRetract = 10;
    private static final int kTestMainExtend = 11;
    private static final int kTestMainRetracted = 12;
    private static final int kTestMainExtended = 13;
    private static final int kTestLatchRetracted = 14;
    private static final int kTestLatchExtended = 15;
    private static final int kTestPrepareToKick = 16;

    public void test(Joystick joystick)
    {
        if (m_running) return;
        // when we first detect the top button extended, bump the index
        if (joystick.getTop() && !m_topExtended)
        {
            m_topExtended = true;
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
                    System.out.println("Retract Main");
                    m_kickerTest = kTestMainRetract;
                    break;
                case 5:
                    System.out.println("Extend Main");
                    m_kickerTest = kTestMainExtend;
                    break;
                case 6:
                    System.out.println("open valve 1A");
                    m_kickerTest = kTestExtendMain;
                    break;
                case 7:
                    System.out.println("open valve 1B");
                    m_kickerTest = kTestRetractMain;
                    break;
                case 8:
                    System.out.println("open valve 2A");
                    m_kickerTest = kTestExtendLatch;
                    break;
                case 9:
                    System.out.println("open valve 2B");
                    m_kickerTest = kTestRetractLatch;
                    break;
                case 10:
                    System.out.println("open valve 3A");
                    m_kickerTest = kTestRetractVent;
                    break;
                case 11:
                    System.out.println("open valve 3B");
                    m_kickerTest = kTestRetractCharge;
                    break;
                case 12:
                    System.out.println("test latch retracted...");
                    m_kickerTest = kTestLatchRetracted;
                    break;
                case 13:
                    System.out.println("test latch extended...");
                    m_kickerTest = kTestLatchExtended;
                    break;
                case 14:
                    System.out.println("test main retracted...");
                    m_kickerTest = kTestMainRetracted;
                    break;
                case 15:
                    System.out.println("test main extended...");
                    m_kickerTest = kTestMainExtended;
                    m_kickerTestCycle = 0;
                    break;
            }
        }

        if (!joystick.getTop())
            m_topExtended = false;

        // when we first detect the trigger retracted, run the test
        if (joystick.getTrigger() && !m_triggerRetracted)
        {
            doTest(m_kickerTest);
        }

        if (!joystick.getTrigger())
            m_triggerRetracted = false;
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
            case kTestRetractMain:
                System.out.println("Retracting with main cylinder");
                retractWithMainCylinder();
                break;
            case kTestExtendMain:
                System.out.println("Extending with main cylinder");
                extendWithMainCylinder();
                break;
            case kTestMainIdle:
                System.out.println("Idling main cylinder");
                idleMainCylinder();
                break;
            case kTestCloseLatch:
                System.out.println("Closing the latch");
                closeTheLatch();
                break;
            case kTestRetractLatch:
                System.out.println("opening latch retract (2B)");
                openValve(m_latchRetractValve);
                break;
            case kTestExtendLatch:
                System.out.println("opening latch extend (2A)");
                openValve(m_latchExtendValve);
                break;
            case kTestRetractCharge:
                System.out.println("opening main retract charge (3B)");
                openValve(m_mainRetractChargeValve);
                break;
            case kTestRetractVent:
                System.out.println("opening main retract vent (3A)");
                openValve(m_mainRetractVentValve);
                break;
            case kTestMainRetract:
                System.out.println("opening main retract (1B)");
                openValve(m_mainRetractValve);
                break;
            case kTestMainExtend:
                System.out.println("opening main extend (1A)");
                openValve(m_mainExtendValve);
                break;
            case kTestMainRetracted:
                rval = isMainWithdrawn();
                System.out.println("main retracted sensor = " + rval);
                break;
            case kTestMainExtended:
                rval = isMainExtended();
                System.out.println("main extended sensor = " + rval);
                break;
            case kTestLatchRetracted:
                rval = isLatchClosed();
                System.out.println("latch retracted sensor = " + rval);
                break;
            case kTestLatchExtended:
                rval = isLatchOpen();
                System.out.println("latch extended sensor = " + rval);
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

    boolean m_topExtended = false;
    boolean m_triggerRetracted = false;
    int m_kickerTestCycle = 0;
    int m_kickerTest = 0;
}

