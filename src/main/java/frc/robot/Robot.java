package frc.robot;

import static edu.wpi.first.wpilibj2.command.CommandGroupBase.sequence;

import java.util.function.DoubleSupplier;

import com.chopshop166.chopshoplib.CommandRobot;
import com.chopshop166.chopshoplib.controls.ButtonXboxController;
import com.chopshop166.chopshoplib.controls.ButtonXboxController.XBoxButton;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import frc.robot.maps.CurrentRobot;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Leds;
import frc.robot.subsystems.LiftSubsystem;
import frc.robot.subsystems.LiftSubsystem.Heights;
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
    final private Drive drive = new Drive(robotMap.getDriveMap());
    final private LiftSubsystem lift = new LiftSubsystem(robotMap.getLiftMap());
    final private Manipulator manipulator = new Manipulator(robotMap.getManipulatorMap());
    public static ButtonXboxController driveController = new ButtonXboxController(5);
    POVButton povDown = new POVButton(xBoxCoPilot, 180);
    POVButton povUp = new POVButton(xBoxCoPilot, 0);
    POVButton povRight = new POVButton(xBoxCoPilot, 90);
    POVButton povLeft = new POVButton(xBoxCoPilot, 270);
    public static Leds leds = new Leds();
    private UsbCamera cameraBack;

    private Command autonomousCommand;

    /**
     * This function is run when the robot is first started up and should be used
     * for any initialization code.
     */
    @Override
    public void robotInit() {
        // Initialize OI here
        cameraBack = CameraServer.getInstance().startAutomaticCapture(0);
        cameraBack.setResolution(160, 120);

        drive.setDefaultCommand(
                drive.driveNormal(driveController::getTriggers, () -> driveController.getX(Hand.kLeft)));
        lift.setDefaultCommand(lift.moveLift(xBoxCoPilot::getTriggers));

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

        // schedule the autonomous command (example)
        if (autonomousCommand != null) {
            autonomousCommand.schedule();
        }
        // we can know the match types and event names and match number
        Shuffleboard.startRecording();
        DriverStation ds = DriverStation.getInstance();
        Shuffleboard.addEventMarker(ds.getEventName() + " " + ds.getMatchType() + " " + ds.getMatchNumber(),
                EventImportance.kNormal);
    }

    @Override
    public void teleopInit() {
        // This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to
        // continue until interrupted by another command, remove
        // this line or comment it out.
        // This will record the details of the match
        if (autonomousCommand != null) {
            autonomousCommand.cancel();
        }
        Shuffleboard.addEventMarker("Sandstorm has ended", EventImportance.kNormal);
    }

    @Override
    public void disabledInit() {
        Shuffleboard.stopRecording();
    }

    public CommandBase LEDOpenBeak() {
        return sequence(manipulator.openBeak(), leds.turnOnGreen(1, 2));
    }

    public CommandBase LEDCloseBeak() {
        return sequence(manipulator.closeBeak(), leds.setTeamColor(1, 2));
    }

    public CommandBase LEDVision() {
        return sequence(leds.turnOnGreen(1, 2), drive.visionPID());
    }

    public void assignButtons() {
        driveController.getButton(XBoxButton.A).whileHeld(drive.visionPID());
        driveController.getButton(XBoxButton.BUMPER_RIGHT).whileHeld(drive.rightSlowTurn());
        driveController.getButton(XBoxButton.BUMPER_RIGHT).whileHeld(leds.blinkLights(Leds.fuschia, 1, 2));
        driveController.getButton(XBoxButton.BUMPER_LEFT).whileHeld(drive.leftSlowTurn());
        driveController.getButton(XBoxButton.BUMPER_LEFT).whileHeld(leds.blinkLights(Leds.fuschia, 1, 1));
        driveController.getButton(XBoxButton.Y).toggleWhenPressed(
                drive.driveBackwards(driveController::getTriggers, () -> driveController.getX(Hand.kLeft)));
        driveController.getButton(XBoxButton.B).whenPressed(lift.deployArms());

        DoubleSupplier copilotForward = () -> xBoxCoPilot.getY(Hand.kLeft);
        DoubleSupplier copilotTurn = () -> xBoxCoPilot.getX(Hand.kLeft);
        xBoxCoPilot.getButton(XBoxButton.B).whenPressed(lift.goToHeight(Heights.kRocketHatchMid));
        xBoxCoPilot.getButton(XBoxButton.X).whenPressed(lift.goToHeight(Heights.kRocketHatchHigh));
        xBoxCoPilot.getButton(XBoxButton.BUMPER_LEFT).whenPressed(LEDOpenBeak());
        xBoxCoPilot.getButton(XBoxButton.BUMPER_RIGHT).whenPressed(LEDCloseBeak());
        xBoxCoPilot.getButton(XBoxButton.Y).whileHeld(manipulator.intake());
        xBoxCoPilot.getButton(XBoxButton.A).whenPressed(manipulator.eject());
        xBoxCoPilot.getButton(XBoxButton.START).toggleWhenPressed(drive.copilotDrive(copilotForward, copilotTurn));

    }

}
