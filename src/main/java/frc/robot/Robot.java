package frc.robot;

import com.chopshop166.chopshoplib.CommandRobot;
import com.chopshop166.chopshoplib.controls.ButtonXboxController;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSink;
import edu.wpi.cscore.VideoSource.ConnectionStrategy;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.maps.PracticeBot;
import frc.robot.subsystems.ExampleSubsystem;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends CommandRobot {

    final private RobotMap robotMap = new PracticeBot();
    public static ButtonXboxController xBoxCoPilot = new ButtonXboxController(4);
    final private ExampleSubsystem exampleSubsystem = new ExampleSubsystem(robotMap);

    public static XboxController driveController = new XboxController(1);

    private Command autonomousCommand;
    final private SendableChooser<Command> chooser = new SendableChooser<>();

    UsbCamera Camera0;
    UsbCamera Camera1;
    VideoSink videoSink;
    boolean activeCamera = false;
    boolean camera0Active = true;

    /**
     * This function is run when the robot is first started up and should be used
     * for any initialization code.
     */
    @Override
    public void robotInit() {
        // Initialize OI here
        Camera0 = CameraServer.getInstance().startAutomaticCapture(0);
        Camera1 = CameraServer.getInstance().startAutomaticCapture(1);
        Camera0.setResolution(640, 480);
        Camera1.setResolution(640, 480);
        videoSink = CameraServer.getInstance().getServer();
        videoSink.getProperty("compression").set(70);
        // Initialize autonomous chooser
        chooser.setDefaultOption("Default Auto", exampleSubsystem.sampleCommand());
        // chooser.addOption("My Auto", new MyAutoCommand());
        SmartDashboard.putData("Auto mode", chooser);
        SmartDashboard.putData("Switch Cameras", switchCameras());
    }

    /**
     * This autonomous (along with the chooser code above) shows how to select
     * between different autonomous modes using the dashboard. The sendable chooser
     * code works with the Java SmartDashboard.
     *
     * <p>
     * You can add additional auto modes by adding additional commands to the
     * chooser code above (like the commented example).
     */
    @Override
    public void autonomousInit() {
        autonomousCommand = chooser.getSelected();

        // schedule the autonomous command (example)
        if (autonomousCommand != null) {
            autonomousCommand.start();
        }
    }

    @Override
    public void teleopInit() {
        // This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to
        // continue until interrupted by another command, remove
        // this line or comment it out.
        if (autonomousCommand != null) {
            autonomousCommand.cancel();
        }
    }

    public Command switchCameras() {
        return new InstantCommand(() -> {
            System.out.println("Camera 0" + camera0Active);
            if (!camera0Active) {
                videoSink.setSource(Camera0);
                camera0Active = !camera0Active;
            } else {
                videoSink.setSource(Camera1);
                camera0Active = !camera0Active;
            }
        });
    }
}
