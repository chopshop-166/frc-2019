package frc.robot.subsystems;

import com.chopshop166.chopshoplib.outputs.SendableSpeedController;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import frc.robot.Robot;
import frc.robot.RobotMap;

public class Maflipulator extends Subsystem {

    public enum MaflipulatorSide {
        kFront, kBack;
    }

    MaflipulatorSide currentPosition;

    private SendableSpeedController flipMotor;
    private Potentiometer manipulatorPot;

    public Maflipulator(final RobotMap.MaflipulatorMap map) { // NOPMD
        super();
        flipMotor = map.getFlipMotor();
        manipulatorPot = map.getManipulatorPot();
    }

    @Override
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(manualFlip());
    }

    public Command manualFlip() {
        // The command is named "Sample Command" and requires this subsystem.
        return new Command("manualFlip", this) {
            @Override
            protected void initialize() {
                // Called just before this Command runs the first time
            }

            @Override
            protected void execute() {
                flipMotor.set(Robot.coPilot.getY(Hand.kRight));
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

    public Command sampleCommand() {
        // The command is named "Sample Command" and requires this subsystem.
        return new Command("Sample Command", this) {
            @Override
            protected void initialize() {
                // Called just before this Command runs the first time
            }

            @Override
            protected void execute() {
                double flipSpeed = Robot.coPilot.getY(Hand.kRight);
                if (flipSpeed > 0 && manipulatorPot.get() >= 180) {
                    flipSpeed = 0;
                }
                if (flipSpeed > 0 && manipulatorPot.get() <= 70) {
                    flipSpeed = 0;
                }
                flipMotor.set(flipSpeed);
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

    public Command Flip() {
        // The command is named "Flip" and requires this subsystem.
        return new Command("Flip", this) {
            @Override
            protected void initialize() {
                if (currentPosition.get() = kFront) {

                }
                // Called just before this Command runs the first time
            }

            @Override
            protected void execute() {
                flipMotor.set(.7);
                // Called repeatedly when this Command is scheduled to run
            }

            @Override
            protected boolean isFinished() {
                // Make this return true when this Command no longer needs to run execute()
                if (manipulatorPot.get() >= 270) {
                    return true;
                }
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
