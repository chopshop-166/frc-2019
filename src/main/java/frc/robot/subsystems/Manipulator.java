package frc.robot.subsystems;

import com.chopshop166.chopshoplib.outputs.SendableSpeedController;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.robot.RobotMap.ManipulatorMap;

public class Manipulator extends Subsystem {

    private SendableSpeedController pivotPointsMotor;
    private SendableSpeedController rollersMotor;
    private DoubleSolenoid beaksPiston;
    private DigitalInput backPlateLimitSwitch;
    private DigitalInput foldedBackLimitSwitch;
    private DigitalInput intakePositionLimitSwitch;
    // private Ultrasonic highDefinitionUltrasonicRangeFinder;

    public Manipulator(final RobotMap.ManipulatorMap map) { // NOPMD
        super();
        // Take values that the subsystem needs from the map, and store them in the
        // class
        pivotPointsMotor = map.getpivotPointsMotor();
        rollersMotor = map.getrollersMotor();
        beaksPiston = map.getbeaksPiston();
        backPlateLimitSwitch = map.getbackPlateLimitSw();
        foldedBackLimitSwitch = map.getfoldedBackLimitSwitch();
        intakePositionLimitSwitch = map.getintakePositionLimitSwitch();

    }

    @Override
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());
    }


    public Command CloseArms() {
        return new InstantCommand("Close Arms", this, () -> {
            pivotPointsMotor.set(-.2);
        });
    }

    public Command openArms() {
        return new Command("Open Arms", this) {
            @Override
            protected void initialize() {
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
            protected void initialize() {
                pivotPointsMotor.set(-.2);
            }

            @Override
            protected boolean isFinished() {
                return backPlateLimitSwitch.get();
            }

            @Override
            protected void end() {
                pivotPointsMotor.set(0);
            }
        };
    }


    public Command pickUpBall() {
        // This command will pick up a ball
        return new Command("Pick up ball", this) {
            @Override
            protected void initialize() {
                // Called just before this Command runs the first time
            }

            @Override
            protected void execute() {
                // Called repeatedly when this Command is scheduled to run
            }

            @Override
            protected boolean isFinished() {
                // Make this return true when this Command no longer needs to run execute()
                return false;
            }

            @Override
            protected void end() {
                // Called once after isFinished returns true
            }

            // Called when another command which requires one or more of the same
            // subsystems is scheduled to run
            @Override
            protected void interrupted() {
                end();
            }
        };
    }

    public Command releaseBall() {
        // This command will release a ball
        return new Command("Release ball", this) {
            @Override
            protected void initialize() {
                // Called just before this Command runs the first time
            }

            @Override
            protected void execute() {
                // Called repeatedly when this Command is scheduled to run
            }

            @Override
            protected boolean isFinished() {
                // Make this return true when this Command no longer needs to run execute()
                return false;
            }

            @Override
            protected void end() {
                // Called once after isFinished returns true
            }

            // Called when another command which requires one or more of the same
            // subsystems is scheduled to run
            @Override
            protected void interrupted() {
                end();
            }
        };
    }

    public Command pickUpHatch() {
        // This command will pick up a hatch
        return new Command("Pick up Hatch", this) {
            @Override
            protected void initialize() {
                // Called just before this Command runs the first time
            }

            @Override
            protected void execute() {
                // Called repeatedly when this Command is scheduled to run
            }

            @Override
            protected boolean isFinished() {
                // Make this return true when this Command no longer needs to run execute()
                return false;
            }

            @Override
            protected void end() {
                // Called once after isFinished returns true
            }

            // Called when another command which requires one or more of the same
            // subsystems is scheduled to run
            @Override
            protected void interrupted() {
                end();
            }
        };
    }

    public Command releaseHatch() {
        // This command will relase a hatch
        return new Command("Release a Hatch", this) {
            @Override
            protected void initialize() {
                // Called just before this Command runs the first time
            }

            @Override
            protected void execute() {
                // Called repeatedly when this Command is scheduled to run
            }

            @Override
            protected boolean isFinished() {
                // Make this return true when this Command no longer needs to run execute()
                return false;
            }

            @Override
            protected void end() {
                // Called once after isFinished returns true
            }

            // Called when another command which requires one or more of the same
            // subsystems is scheduled to run
            @Override
            protected void interrupted() {
                end();
            }
        };
    }
}
