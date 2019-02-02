package frc.robot.subsystems;

import com.chopshop166.chopshoplib.outputs.SendableSpeedController;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Robot;
import frc.robot.RobotMap;

public class Lift extends Subsystem {
    private SendableSpeedController liftMotor;
    private Encoder heightEncoder;
    private DigitalInput topLimitSwitch;
    private DigitalInput bottomLimitSwitch;

    public Lift(final RobotMap.LiftMap map) { // NOPMD
        super();
        liftMotor = map.getLiftMotor();
        heightEncoder = map.getHeightEncoder();
        topLimitSwitch = map.getTopLimitSwitch();
        bottomLimitSwitch = map.getBottomLimitSwitch();
    }

    @Override
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(manualLift());
    }

    public Command manualLift() {
        // The command is named "Sample Command" and requires this subsystem.
        return new Command("manualLift", this) {
            @Override
            protected void initialize() {
                // Called just before this Command runs the first time
            }

            @Override
            protected void execute() {
                double liftSpeed = Robot.coPilot.getY(Hand.kLeft);
                if (liftSpeed > 0 && topLimitSwitch.get()) {
                    liftSpeed = 0;
                }
                if (liftSpeed < 0 && bottomLimitSwitch.get()) {
                    liftSpeed = 0;
                }
                liftMotor.set(liftSpeed);
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
