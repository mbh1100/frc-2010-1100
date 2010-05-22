/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package team1100.season2010.robot;

/**
 *
 * @author team1100
 */

import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.camera.AxisCameraException;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.NIVisionException;
import java.util.TimerTask;

public class Camera {

    public Camera()
    {
        int  comp = AxisCamera.getInstance().getCompression();

        System.out.println("jpeg compression =" + comp);
        AxisCamera.getInstance().writeRotation(AxisCamera.RotationT.k180);
        AxisCamera.getInstance().writeCompression(75);
        AxisCamera.getInstance().writeResolution(AxisCamera.ResolutionT.k320x240);
        m_controlLoop = new java.util.Timer();
       // m_controlLoop.schedule(new CameraTask(this), 0L, 1000L);
    }
    
    java.util.Timer m_controlLoop;

    private class CameraTask extends TimerTask
    {
        private Camera m_controller;

        public CameraTask(Camera controller)
        {
            if (controller == null)
            {
                throw new NullPointerException("Given PIDController was null");
            }
            m_controller = controller;
        }

        public void run()
        {
            m_controller.camera_get_image();
        }
    }

    public void camera_get_image()
    {
        if (AxisCamera.getInstance().freshImage())
        {
            try
            {
                System.out.println("image!");
                ColorImage image = AxisCamera.getInstance().getImage();
                Thread.yield();
                image.free();
            }
            catch (NIVisionException ex)
            {
            }
            catch (AxisCameraException ex)
            {
            }
        }
    }
}
