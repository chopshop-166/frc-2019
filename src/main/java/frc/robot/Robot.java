package frc.robot;

import com.chopshop166.chopshoplib.CommandRobot;
import com.chopshop166.chopshoplib.commands.CommandChain;
import com.chopshop166.chopshoplib.controls.ButtonXboxController;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.buttons.POVButton;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.maps.CurrentRobot;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Leds;
import frc.robot.subsystems.LiftSubsystem;
import frc.robot.subsystems.LiftSubsystem.Heights;
import frc.robot.subsystems.Maflipulator;
import frc.robot.subsystems.Manipulator;
import com.chopshop166.chopshoplib.controls.ButtonXboxController.XBoxButton;

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
    POVButton povDown = new POVButton(xBoxCoPilot, 180);
    POVButton povUp = new POVButton(xBoxCoPilot, 0);
    POVButton povRight = new POVButton(xBoxCoPilot, 90);
    POVButton povLeft = new POVButton(xBoxCoPilot, 270);
    public static Leds leds = new Leds();
    private UsbCamera cameraBack;

    private Command autonomousCommand;
    final private SendableChooser<Command> chooser = new SendableChooser<>();

    /**
     * This function is run when the robot is first started up and should be used
     * for any initialization code.
     */
    @Override
    public void robotInit() {
        // Initialize OI here
        SmartDashboard.putData("Good Flip", goodFlip());
        cameraBack = CameraServer.getInstance().startAutomaticCapture(0);
        cameraBack.setResolution(160, 120);
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
        autonomousCommand = lift.homePos();

        // schedule the autonomous command (example)
        if (autonomousCommand != null) {
            autonomousCommand.start();
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

    public Command goodFlip() {
        CommandChain retValue = new CommandChain("Good Flip");

        retValue.then(lift.goToAtLeast(Heights.kLiftFlipHeight)).then(maflipulator.crappyFlip());
        return retValue;
    }

    public Command stowAndGo() {
        CommandChain retValue = new CommandChain("Stow it and go!");
        retValue.then(lift.goToHeight(Heights.kFloorLoad), maflipulator.stowAndGoPosition());
        return retValue;
    }

    public Command goToLoadingStation() {
        CommandChain retValue = new CommandChain("Go to Loading Station");

        retValue.then(lift.goToHeight(Heights.kLoadingStation));
        return retValue;

    }

    public Command goToRocketMiddleHatch() {
        CommandChain retValue = new CommandChain("Go to Rocket Middle Hatch");

        retValue.then(lift.goToHeight(Heights.kRocketHatchMid), maflipulator.goToScoringPosition());
        return retValue;

    }

    public Command goToRocketHighHatch() {
        CommandChain retValue = new CommandChain("Go to Rocket High Hatch");

        retValue.then(lift.goToHeight(Heights.kRocketHatchHigh), maflipulator.goToScoringPosition());
        return retValue;

    }

    public Command goToRocketHighCargo() {
        CommandChain retValue = new CommandChain("Go to Rocket High Cargo");

        retValue.then(lift.goToHeight(Heights.kRocketCargoHigh), maflipulator.goToScoringPosition());
        return retValue;

    }

    public Command goToRocketMiddleCargo() {
        CommandChain retValue = new CommandChain("Go to Rocket Middle Cargo");

        retValue.then(lift.goToHeight(Heights.kRocketCargoMid), maflipulator.goToScoringPosition());
        return retValue;

    }

    public Command goToRocketLowCargo() {
        CommandChain retValue = new CommandChain("Go to Rocket Low Cargo");

        retValue.then(lift.goToHeight(Heights.kRocketCargoLow), maflipulator.goToScoringPosition());
        return retValue;

    }

    public Command goToFloorLoad() {
        CommandChain retValue = new CommandChain("Go to Floor Load");

        retValue.then(lift.goToHeight(Heights.kFloorLoad), maflipulator.goToScoringPosition());
        return retValue;

    }

    public Command goToCargoShipCargo() {
        CommandChain retValue = new CommandChain("Go to Cargo Ship Cargo");

        retValue.then(lift.goToHeight(Heights.kCargoShipCargo), maflipulator.goToScoringPosition());
        return retValue;

    }

    public CommandChain LEDOpenBeak() {
        CommandChain retValue = new CommandChain("Beak Open & Green LED");
        retValue.then(manipulator.openBeak()).then(leds.turnOnGreen(1, 2));
        return retValue;
    }

    public CommandChain LEDCloseBeak() {
        CommandChain retValue = new CommandChain("Close Beak and reset LEDs");
        retValue.then(manipulator.closeBeak()).then(leds.setTeamColor(1, 2));
        return retValue;
    }

    public void assignButtons() {
        xBoxCoPilot.getButton(XBoxButton.BUMPER_LEFT).whenPressed(LEDOpenBeak());
        xBoxCoPilot.getButton(XBoxButton.BUMPER_RIGHT.get()).whenPressed(LEDCloseBeak());

        // driveController.getButton(XBoxButton.Y).whenPressed(gosodFlip());
        driveController.getButton(XBoxButton.A).whileHeld(drive.visionPID());
        driveController.getButton(XBoxButton.BUMPER_RIGHT).whileHeld(drive.leftSlowTurn());
        driveController.getButton(XBoxButton.BUMPER_LEFT).whileHeld(drive.rightSlowTurn());
        driveController.getButton(XBoxButton.START).whenPressed(maflipulator.cancel());
        driveController.getButton(XBoxButton.Y).toggleWhenPressed(drive.driveBackwards());

        // xBoxCoPilot.getButton(XBoxButton.X).whenPressed(manipulator.extendArms());
        // xBoxCoPilot.getButton(XBoxButton.B).whenPressed(manipulator.retractArms());
        driveController.getButton(XBoxButton.X).whenPressed(manipulator.extendArms());
        driveController.getButton(XBoxButton.B).whenPressed(manipulator.retractArms());
        // xBoxCoPilot.getButton(XBoxButton.A).whenPressed(manipulator.Eject());
        // xBoxCoPilot.getButton(XBoxButton.Y).whileHeld(manipulator.Intake());
        // xBoxCoPilot.getButton(XBoxButton.A).whenPressed(manipulator.extendArms());
        // xBoxCoPilot.getButton(XBoxButton.A).whenPressed(manipulator.extendArms());

        // manipulator.switchTrigger.whileActive(leds.turnOnGreen());

        // xBoxCoPilot.getButton(XBoxButton.A).whileHeld(goToLoadingStation());
        // xBoxCoPilot.getButton(XBoxButton.A).whenReleased(stowAndGo());
        // xBoxCoPilot.getButton(XBoxButton.Y).whenReleased(stowAndGo());
        // xBoxCoPilot.getButton(XBoxButton.Y).whileHeld(goToFloorLoad());

        // // povUp.whenReleased(stowAndGo());
        // // povUp.whileHeld(goToCargoShipCargo());

        // // povDown.whenReleased(stowAndGo());
        // // povDown.whileHeld(goToRocketLowCargo());

        // xBoxCoPilot.getButton(XBoxButton.A).whileHeld(maflipulator.pressRotate());
        // xBoxCoPilot.getButton(XBoxButton.A).whenReleased(stowAndGo());
        xBoxCoPilot.getButton(XBoxButton.B).whenPressed(lift.goToHeight(Heights.kRocketHatchMid));
        xBoxCoPilot.getButton(XBoxButton.X).whenPressed(lift.goToHeight(Heights.kRocketHatchHigh));
        // xBoxCoPilot.getButton(XBoxButton.B).whenReleased(stowAndGo());

        // povRight.whenReleased(stowAndGo());
        // povRight.whileHeld(goToRocketMiddleCargo());

        // xBoxCoPilot.getButton(XBoxButton.X).whileHeld(goToRocketHighHatch());
        // xBoxCoPilot.getButton(XBoxButton.X).whenReleased(stowAndGo());
        // povLeft.whenReleased(stowAndGo());
        // povLeft.whileHeld(goToRocketHighCargo());

        // //
        // xBoxCoPilot.getButton(XBoxButton.STICK_LEFT).whenReleased(stowAndGo());
        // xBoxCoPilot.getButton(XBoxButton.STICK_LEFT).whenPressed(maflipulator.goToScoringPosition());

    }

}
