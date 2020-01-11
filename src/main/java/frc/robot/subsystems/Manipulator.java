package frc.robot.subsystems;

import com.chopshop166.chopshoplib.outputs.SendableSpeedController;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.robot.RobotMap;
import frc.robot.triggers.LimitSwitchTrigger;

public class Manipulator extends SubsystemBase {

    private SendableSpeedController rollersMotor;
    private DoubleSolenoid beaksPiston;
    private DigitalInput gamepieceLimitSwitch;
    private DigitalInput foldedBackLimitSwitch;
    private DigitalInput intakePositionLimitSwitch;
    public LimitSwitchTrigger switchTrigger;

    public Manipulator(final RobotMap.ManipulatorMap map) { // NOPMD
        super();
        // Take values that the subsystem needs from the map, and store them in the
        // class
        rollersMotor = map.getrollersMotor();
        beaksPiston = map.getbeaksPiston();
        gamepieceLimitSwitch = map.getGamepieceLimitSwitch();
        foldedBackLimitSwitch = map.getfoldedBackLimitSwitch();
        intakePositionLimitSwitch = map.getintakePositionLimitSwitch();
        switchTrigger = new LimitSwitchTrigger(gamepieceLimitSwitch);
        addChildren();
        SmartDashboard.putData(openBeak());
        SmartDashboard.putData(closeBeak());
    }

    public void addChildren() {
        addChild("Rollers", rollersMotor);
        addChild("Beaks", beaksPiston);
        addChild("Game Piece", gamepieceLimitSwitch);
        addChild("Fold", foldedBackLimitSwitch);
        addChild("Intake", intakePositionLimitSwitch);
    }

    double rollerspeed = 1;

    // // #region Command Chains
    // #endregion

    // #region Commands
    public InstantCommand openBeak() {
        return new InstantCommand(() -> {
            beaksPiston.set(Value.kForward);
        }, this);
    }

    public InstantCommand closeBeak() {
        return new InstantCommand(() -> {
            beaksPiston.set(Value.kReverse);
        }, this);
    }

    public InstantCommand rollerStop() {
        return new InstantCommand(() -> {
            rollersMotor.set(0);
        }, this);
    }

    public WaitUntilCommand gamepieceCheck() {
        // This command will check if a gamepiece is held
        return new WaitUntilCommand(gamepieceLimitSwitch::get) {
            {
                addRequirements(Manipulator.this);
            }
        };

    }
    // #endregion

    public StartEndCommand intake() {
        return new StartEndCommand(() -> {
            rollersMotor.set(rollerspeed);
        }, () -> {
            rollersMotor.set(0);
        }, this);
    }

    public ParallelRaceGroup eject() {
        return new StartEndCommand(() -> {
            rollersMotor.set(-rollerspeed);
        }, () -> {
            rollersMotor.set(0);
        }, this).withTimeout(1.0);
    }
}
