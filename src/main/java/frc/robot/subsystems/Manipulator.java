package frc.robot.subsystems;

import com.chopshop166.chopshoplib.commands.CommandChain;
import com.chopshop166.chopshoplib.outputs.SendableSpeedController;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;

public class Manipulator extends Subsystem {

    private SendableSpeedController pivotPointsMotor;
    private SendableSpeedController rollersMotor;
    private DoubleSolenoid beaksPiston;
    private DigitalInput gamepieceLimitSwitch;
    private DigitalInput foldedBackLimitSwitch;
    private DigitalInput intakePositionLimitSwitch;

    public Manipulator(final RobotMap.ManipulatorMap map) { // NOPMD
        super();
        // Take values that the subsystem needs from the map, and store them in the
        // class
        pivotPointsMotor = map.getpivotPointsMotor();
        rollersMotor = map.getrollersMotor();
        beaksPiston = map.getbeaksPiston();
        gamepieceLimitSwitch = map.getGamepieceLimitSwitch();
        foldedBackLimitSwitch = map.getfoldedBackLimitSwitch();
        intakePositionLimitSwitch = map.getintakePositionLimitSwitch();

    }

    @Override
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());
    }

    // #region Command Chains
    public Command pickUpBall() {
        CommandChain retValue = new CommandChain("Pick up a Ball");
        retValue.then(openBeak()).then(rollerIntake()).then(closeArms()).then(gamepieceCheck()).then(rollerStop());
        return retValue;
    }
    // #endregion

    // #region Commands
    public Command openBeak() {
        return new InstantCommand("Open Beak", this, () -> {
            beaksPiston.set(Value.kForward);
        });
    }

    public Command closeBeak() {
        return new InstantCommand("Close Beak", this, () -> {
            beaksPiston.set(Value.kReverse);
        });
    }

    public Command rollerIntake() {
        return new InstantCommand("Intake Rollers", this, () -> {
            rollersMotor.set(.2);
        });
    }

    public Command rollerEject() {
        return new InstantCommand("Eject Rollers", this, () -> {
            rollersMotor.set(-.2);
        });
    }

    public Command rollerStop() {
        return new InstantCommand("Stop Rollers", this, () -> {
            rollersMotor.set(0);
        });
    }

    public Command openArms() {
        return new Command("Open Arms", this) {
            @Override
            protected void execute() {
                pivotPointsMotor.set(.2);
            }

            @Override
            protected boolean isFinished() {
                return foldedBackLimitSwitch.get();
            }

            @Override
            protected void end() {
                pivotPointsMotor.set(0);
            }
        };
    }

    public Command closeArms() {
        return new Command("Close Arms", this) {
            @Override
            protected void execute() {
                pivotPointsMotor.set(-.2);
            }

            @Override
            protected boolean isFinished() {
                return intakePositionLimitSwitch.get();
            }

            @Override
            protected void end() {
                pivotPointsMotor.set(0);
            }
        };
    }

    public Command gamepieceCheck() {
        // This command will pick up a ball
        return new Command("Check for Gamepiece", this) {

            @Override
            protected boolean isFinished() {
                return gamepieceLimitSwitch.get();
            }
        };
    }
    // #endregion
}
