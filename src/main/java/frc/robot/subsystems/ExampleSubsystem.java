package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

public class ExampleSubsystem extends Subsystem {

    @Override
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());
    }

    public Command sampleCommand() {
        return new Command("Sample Command", this) {
            // Called just before this Command runs the first time
            @Override
            protected void initialize() {
            }

            // Called repeatedly when this Command is scheduled to run
            @Override
            protected void execute() {
            }

            // Make this return true when this Command no longer needs to run execute()
            @Override
            protected boolean isFinished() {
                return false;
            }

            // Called once after isFinished returns true
            @Override
            protected void end() {
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
