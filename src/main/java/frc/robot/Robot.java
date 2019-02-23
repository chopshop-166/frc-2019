package frc.robot;

import com.chopshop166.chopshoplib.CommandRobot;
import com.chopshop166.chopshoplib.commands.CommandChain;
import com.chopshop166.chopshoplib.controls.ButtonXboxController;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSink;
import edu.wpi.cscore.VideoSource.ConnectionStrategy;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.maps.CurrentRobot;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Leds;
import frc.robot.subsystems.LiftSubsystem;
import frc.robot.subsystems.Maflipulator;
import frc.robot.subsystems.Manipulator;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends CommandRobot {

    final private RobotMap robotMap = new CurrentRobot();
    final public static ButtonXboxController xBoxCoPilot = new ButtonXboxController(1);
    final private Maflipulator maflipulator = new Maflipulator(robotMap.getMaflipulatorMap());
    final private Drive drive = new Drive(robotMap.getDriveMap());
    final private LiftSubsystem lift = new LiftSubsystem(robotMap.getLiftMap());
    final private Manipulator manipulator = new Manipulator(robotMap.getManipulatorMap());
    public static ButtonXboxController driveController = new ButtonXboxController(5);
    public static Leds leds = new Leds();
    private Command autonomousCommand;
    final private SendableChooser<Command> chooser = new SendableChooser<>();

    private UsbCamera cameraBack;
    private UsbCamera cameraFront;
    private VideoSink videoSink;
    private boolean cameraBackActive = true;

    /**
     * This function is run when the robot is first started up and should be used
     * for any initialization code.
     */
    @Override
    public void robotInit() {
        // Initialize OI here
        cameraBack = CameraServer.getInstance().startAutomaticCapture(0);
        cameraFront = CameraServer.getInstance().startAutomaticCapture(1);
        cameraBack.setResolution(320, 240);
        cameraFront.setResolution(320, 240);
        cameraBack.setFPS(20);
        cameraFront.setFPS(20);
        videoSink = CameraServer.getInstance().getServer();
        videoSink.getProperty("compression").set(70);
        cameraBack.setConnectionStrategy(ConnectionStrategy.kKeepOpen);
        cameraFront.setConnectionStrategy(ConnectionStrategy.kKeepOpen);
        // Initialize autonomous chooser
        // chooser.setDefaultOption("Default Auto", exampleSubsystem.sampleCommand());
        // chooser.addOption("My Auto", new MyAutoCommand());
        // SmartDashboard.putData("Auto mode", chooser);
        SmartDashboard.putData("Switch Cameras", switchCameras());
        SmartDashboard.putData("Good Flip", goodFlip());
        SmartDashboard.putData("Darken Cameras", darkenCameras());
        assignButtons();
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
            System.out.println("Camera 0" + cameraBackActive);
            if (!cameraBackActive) {
                videoSink.setSource(cameraBack);
                cameraBackActive = !cameraBackActive;
            } else {
                videoSink.setSource(cameraFront);
                cameraBackActive = !cameraBackActive;
            }
        });
    }

    public Command goodFlip() {
        CommandChain retValue = new CommandChain("Good Flip");

        retValue.then(lift.goToAtLeast(LiftSubsystem.Heights.kLiftFlipHeight)).then(maflipulator.crappyFlip());
        return retValue;
    }

    public Command darkenCameras() {
        return new InstantCommand(() -> {
            cameraBack.setBrightness(0);
            cameraFront.setBrightness(0);
        });
    }

    public Command brightenCameras() {
        return new InstantCommand(() -> {
            cameraBack.setExposureAuto();
            cameraFront.setExposureAuto();
        });
    }

    public void assignButtons() {
        xBoxCoPilot.getButton(ButtonXboxController.XBoxButton.BUMPER_LEFT).whenPressed(manipulator.openBeak());
        xBoxCoPilot.getButton(ButtonXboxController.XBoxButton.BUMPER_RIGHT.get()).whenPressed(manipulator.closeBeak());
        xBoxCoPilot.getButton(ButtonXboxController.XBoxButton.A).whenPressed(manipulator.openArms());
        xBoxCoPilot.getButton(ButtonXboxController.XBoxButton.B).whenPressed(manipulator.closeArms());
        xBoxCoPilot.getButton(ButtonXboxController.XBoxButton.Y).whenPressed(goodFlip());
        driveController.getButton(ButtonXboxController.XBoxButton.A).whenPressed(drive.align());
    }

}
