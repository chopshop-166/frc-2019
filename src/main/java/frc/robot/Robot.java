package frc.robot;

import com.chopshop166.chopshoplib.CommandRobot;
import com.chopshop166.chopshoplib.commands.CommandChain;
import com.chopshop166.chopshoplib.controls.ButtonXboxController;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.POVButton;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.maps.CurrentRobot;
import frc.robot.maps.Tempest;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Leds;
import frc.robot.subsystems.LiftSubsystem;
import frc.robot.subsystems.Maflipulator;
import frc.robot.subsystems.Manipulator;
import frc.robot.subsystems.LiftSubsystem.Heights;

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
    POVButton povDown = new POVButton(xBoxCoPilot, 270);
    POVButton povUp = new POVButton(xBoxCoPilot, 90);
    POVButton povRight = new POVButton(xBoxCoPilot, 0);
    POVButton povLeft = new POVButton(xBoxCoPilot, 180);
    public static Leds leds = new Leds();
    private Command autonomousCommand;
    final private SendableChooser<Command> chooser = new SendableChooser<>();

    /**
     * This function is run when the robot is first started up and should be used
     * for any initialization code.
     */
    @Override
    public void robotInit() {
        // Initialize OI here
        // SmartDashboard.putData("Good Flip", goodFlip());
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

    public Command goodFlip() {
        CommandChain retValue = new CommandChain("Good Flip");

        retValue.then(lift.goToAtLeast(LiftSubsystem.Heights.kLiftFlipHeight)).then(maflipulator.crappyFlip());
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

    public void assignButtons() {
        xBoxCoPilot.getButton(ButtonXboxController.XBoxButton.BUMPER_LEFT).whenPressed(manipulator.openBeak());
        xBoxCoPilot.getButton(ButtonXboxController.XBoxButton.BUMPER_RIGHT.get()).whenPressed(manipulator.closeBeak());

        driveController.getButton(ButtonXboxController.XBoxButton.Y).whenPressed(goodFlip());
        driveController.getButton(ButtonXboxController.XBoxButton.A).whileHeld(drive.visionPID());
        driveController.getButton(ButtonXboxController.XBoxButton.BUMPER_LEFT).whileHeld(drive.leftSlowTurn());
        driveController.getButton(ButtonXboxController.XBoxButton.BUMPER_RIGHT).whileHeld(drive.rightSlowTurn());

        // manipulator.switchTrigger.whileActive(leds.turnOnGreen());

        // xBoxCoPilot.getButton(ButtonXboxController.XBoxButton.A).whileHeld(goToLoadingStation());
        // xBoxCoPilot.getButton(ButtonXboxController.XBoxButton.A).whenReleased(stowAndGo());

        // xBoxCoPilot.getButton(ButtonXboxController.XBoxButton.Y).whenReleased(stowAndGo());
        // xBoxCoPilot.getButton(ButtonXboxController.XBoxButton.Y).whileHeld(goToFloorLoad());

        // // povUp.whenReleased(stowAndGo());
        // // povUp.whileHeld(goToCargoShipCargo());

        // // povDown.whenReleased(stowAndGo());
        // // povDown.whileHeld(goToRocketLowCargo());

        // //
        // xBoxCoPilot.getButton(ButtonXboxController.XBoxButton.A).whileHeld(maflipulator.pressRotate());
        // //
        // xBoxCoPilot.getButton(ButtonXboxController.XBoxButton.A).whenReleased(stowAndGo());

        // xBoxCoPilot.getButton(ButtonXboxController.XBoxButton.B).whileHeld(goToRocketMiddleHatch());
        // xBoxCoPilot.getButton(ButtonXboxController.XBoxButton.B).whenReleased(stowAndGo());

        // povRight.whenReleased(stowAndGo());
        // povRight.whileHeld(goToRocketMiddleCargo());

        // xBoxCoPilot.getButton(ButtonXboxController.XBoxButton.X).whileHeld(goToRocketHighHatch());
        // xBoxCoPilot.getButton(ButtonXboxController.XBoxButton.X).whenReleased(stowAndGo());
        // povLeft.whenReleased(stowAndGo());
        // povLeft.whileHeld(goToRocketHighCargo());

        // //
        // xBoxCoPilot.getButton(ButtonXboxController.XBoxButton.STICK_LEFT).whenReleased(stowAndGo());
        // //
        // xBoxCoPilot.getButton(ButtonXboxController.XBoxButton.STICK_LEFT).whenPressed(maflipulator.goToScoringPosition());

        // xBoxCoPilot.getButton(ButtonXboxController.XBoxButton.STICK_RIGHT).whenPressed(manipulator.rollerEject());
        // xBoxCoPilot.getButton(ButtonXboxController.XBoxButton.STICK_RIGHT).whenPressed(manipulator.rollerIntake());

    }

}
